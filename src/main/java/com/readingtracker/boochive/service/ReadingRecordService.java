package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.ReadingRecord;
import com.readingtracker.boochive.dto.record.ReadingRecordRequest;
import com.readingtracker.boochive.dto.record.ReadingRecordResponse;
import com.readingtracker.boochive.enums.ReadingStatus;
import com.readingtracker.boochive.dto.common.BatchOperationRequest;
import com.readingtracker.boochive.mapper.ReadingRecordMapper;
import com.readingtracker.boochive.repository.ReadingBookJpaRepository;
import com.readingtracker.boochive.repository.ReadingRecordRepository;
import com.readingtracker.boochive.util.ResourceAccessUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReadingRecordService {

    private final ReadingRecordRepository readingRecordRepository;
    private final ReadingBookJpaRepository readingBookRepository;

    private final ResourceAccessUtil<ReadingRecord> resourceAccessUtil;
    private final ResourceAccessUtil<ReadingBook> readingBookResourceAccessUtil;

    /**
     * C[R]UD - READ
     *   NOTE: 메서드명 키워드 뜻 정리
     *   1. Record : 전체 독서 이력
     *   2. ReadingRecord : '읽는 중' 상태의 독서 이력 (완독일 Null)
     *   3. ReadRecord : 완독 이력 (완독일 Not Null)
     */
    // '읽는 중' 상태의 가장 최근 독서 이력 (최근 = 독서 시작일 X, 이력 생성일 O 기준)
    @Transactional(readOnly = true)
    public Optional<ReadingRecordResponse> findLatestReadingRecordByUserAndBook(Long userId, Long readingBookId) {
        return readingRecordRepository
                .findFirstByUserIdAndReadingBookIdAndEndDateIsNullOrderByIdDesc(userId, readingBookId)
                .map(record -> {
                    ReadingRecordResponse recordResponse = ReadingRecordMapper.INSTANCE.toDto(record);
                    // 추가 정보 1. 독서 시작 디데이
                    recordResponse.setDDay(calculateDDay(record.getStartDate()));
                    return recordResponse;
                });
    }

    // 가장 최근 완독 이력 (최근 = 이력 생성일 기준)
    @Transactional(readOnly = true)
    public Optional<ReadingRecordResponse> findLatestReadRecordByUserAndBook(Long userId, Long readingBookId) {
        return readingRecordRepository
                .findFirstByUserIdAndReadingBookIdAndEndDateIsNotNullOrderByIdDesc(userId, readingBookId)
                .map(ReadingRecordMapper.INSTANCE::toDto);
    }

    // 전체 독서 이력 (완독 여부 상관 X)
    @Transactional(readOnly = true)
    public List<ReadingRecordResponse> getRecordsByUserAndBook(Long userId, Long readingBookId) {
        return readingRecordRepository.findAllByUserIdAndReadingBookId(userId, readingBookId)
                .stream()
                .map(ReadingRecordMapper.INSTANCE::toDto)
                .toList();
    }

    // 로그인 유저의 책 완독 수 계산
    @Transactional(readOnly = true)
    public Integer getUserBookReadCount(Long userId, Long readingBookId) {
        return readingRecordRepository.countByUserIdAndReadingBookIdAndEndDateIsNotNull(userId, readingBookId);
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public ReadingRecordResponse updateReadingRecord(Long id, ReadingRecordRequest readingRecord) {
        return ReadingRecordMapper.INSTANCE.toDto(update(id, readingRecord));
    }

    /**
     * CR[U][D] - BATCH UPDATE/DELETE
     */
    @Transactional
    public Integer handleReadingRecords(BatchOperationRequest<ReadingRecordRequest> request, Long userId) {
        Set<Long> readingBookIdSet = new HashSet<>(); // 참조 ID 예외 처리를 위해 (Unique 적용을 위해 HashSet 사용)

        for (ReadingRecordRequest record : request.getUpdateList()) {
            ReadingRecord existing = update(record.getId(), record);
            readingBookIdSet.add(existing.getReadingBook().getId());
        }
        for (Long id : request.getDeleteList()) {
            ReadingRecord existing = resourceAccessUtil.checkAccessAndRetrieve(id);
            readingBookIdSet.add(existing.getReadingBook().getId());
            readingRecordRepository.delete(existing);
        }

        if (readingBookIdSet.size() > 1) {
            throw new IllegalArgumentException("책 정보가 일치하지 않습니다. 문제가 계속되면 관리자에게 문의해 주세요");
        }
        Long readingBookId = readingBookIdSet.iterator().next();

        // 저장 전 참조 데이터(독서 책) 유효성 검증
        validateReferenceReadingBook(readingBookId);

        // (이후 연계 작업) 완독 이력 모두 삭제됐을 시 독서 상태 '읽을 예정' 상태로 자동 변경
        Integer remainingCompletedReadingRecordCount = readingRecordRepository
                .countByUserIdAndReadingBookIdAndEndDateIsNotNull(userId, readingBookId);

        if (remainingCompletedReadingRecordCount < 1) {
            readingBookRepository.findById(readingBookId)
                    .ifPresent(readingBook -> readingBook.updateReadingStatus(ReadingStatus.TO_READ));
        }

        return remainingCompletedReadingRecordCount;
    }

    /**
     * (공통 메서드) UPDATE 로직
     */
    private ReadingRecord update(Long id, ReadingRecordRequest record) {
        ReadingRecord existingReadingRecord = resourceAccessUtil.checkAccessAndRetrieve(id);

        boolean endDateChanged = !Objects.equals(existingReadingRecord.getEndDate(), record.getEndDate());
        if (!Objects.equals(existingReadingRecord.getStartDate(), record.getStartDate())) {
            existingReadingRecord.updateStartDate(record.getStartDate());
        }
        if (endDateChanged) {
            existingReadingRecord.updateEndDate(record.getEndDate());
        }

        validateReadingRecord(record, endDateChanged); // 날짜 유효성 검사

        return existingReadingRecord;
    }

    /**
     * (공통 메서드) 완독일 검증
     */
    private void validateReadingRecord(ReadingRecordRequest readingRecord, boolean endDateChange) {
        // 완독일도 검사
        if (endDateChange) {
            if (readingRecord.getEndDate() == null) {
                throw new IllegalArgumentException("완독일을 입력해 주세요.");
            }
            if (readingRecord.getEndDate().isBefore(readingRecord.getStartDate())) {
                throw new IllegalArgumentException("유효하지 않은 날짜입니다.<br/>시작일 이후 날짜를 선택해 주세요.");
            }
            if (readingRecord.getEndDate().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("유효하지 않은 날짜입니다.<br/>오늘 또는 오늘 이전의 날짜를 선택해 주세요.");
            }
        }
    }

    /**
     * (공통 메서드) 디데이 계산 메서드
     */
    private Long calculateDDay(LocalDate date) {
        return ChronoUnit.DAYS.between(date, LocalDate.now()) + 1;
    }

    /**
     * (공통 메서드) 참조 데이터 유효성 검증 - 독서 책
     */
    private void validateReferenceReadingBook(Long readingBookId) {
        readingBookResourceAccessUtil.checkAccessAndRetrieve(readingBookId);
    }
}
