package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.ReadingRecord;
import com.readingtracker.boochive.enums.ReadingStatus;
import com.readingtracker.boochive.dto.common.BatchUpdateRequest;
import com.readingtracker.boochive.repository.ReadingBookJpaRepository;
import com.readingtracker.boochive.repository.ReadingRecordRepository;
import com.readingtracker.boochive.util.ResourceAccessUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReadingRecordService {

    private final ReadingRecordRepository readingRecordRepository;
    private final ReadingBookJpaRepository readingBookRepository;
    private final ResourceAccessUtil<ReadingRecord> resourceAccessUtil;

    /**
     * C[R]UD - READ
     *   1. Record : 전체 독서 이력
     *   2. ReadingRecord : '읽는 중' 상태의 독서 이력 (완독일 Null)
     *   3. ReadRecord : 완독 이력 (완독일 Not Null)
     */
    // 독서시작일 기준으로 독서 이력 찾기 (완독 여부 상관 X)
    @Transactional(readOnly = true)
    public Optional<ReadingRecord> findRecordByUserAndBookAndStartDate(Long userId, String bookIsbn, LocalDate starDate) {
        return readingRecordRepository.findByUserIdAndBookIsbnAndStartDate(userId, bookIsbn, starDate);
    }

    // '읽는 중' 상태의 가장 최근 독서 이력 (최근 = 이력 생성일 기준)
    @Transactional(readOnly = true)
    public Optional<ReadingRecord> findLatestReadingRecordByUserAndBook(Long userId, String bookIsbn) {
        return readingRecordRepository.findFirstByUserIdAndBookIsbnAndEndDateIsNullOrderByIdDesc(userId, bookIsbn);
    }

    // 가장 최근 완독 이력 (최근 = 이력 생성일 기준)
    @Transactional(readOnly = true)
    public Optional<ReadingRecord> findLatestReadRecordByUserAndBook(Long userId, String bookIsbn) {
        return readingRecordRepository.findFirstByUserIdAndBookIsbnAndEndDateIsNotNullOrderByIdDesc(userId, bookIsbn);
    }

    // 전체 독서 이력 (완독 여부 상관 X)
    @Transactional(readOnly = true)
    public List<ReadingRecord> getRecordsByUserAndBook(Long userId, String bookIsbn) {
        return readingRecordRepository.findAllByUserIdAndBookIsbn(userId, bookIsbn);
    }

    // 로그인 유저의 책 완독 수 계산
    @Transactional(readOnly = true)
    public Integer getUserBookReadCount(Long userId, String bookIsbn) {
        return readingRecordRepository.countByUserIdAndBookIsbnAndEndDateIsNotNull(userId, bookIsbn);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public ReadingRecord createReadingRecord(ReadingRecord readingRecord) {
        validateReadingRecord(readingRecord, false); // 날짜 유효성 검사
        return readingRecordRepository.save(readingRecord);
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public ReadingRecord updateReadingRecord(Long id, ReadingRecord readingRecord) {
        return update(id, readingRecord);
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteReadingRecord(Long id) {
        ReadingRecord existingReadingRecord = resourceAccessUtil.checkAccessAndRetrieve(id);

        readingRecordRepository.delete(existingReadingRecord);
    }

    /**
     * CR[U][D] - BATCH UPDATE/DELETE
     */
    @Transactional
    public Integer handleReadingRecords(BatchUpdateRequest<ReadingRecord> request, Long userId) {
        Set<String> bookIsbnSet = new HashSet<>(); // Unique 적용을 위해 HashSet 사용

        for (ReadingRecord record : request.getUpdateList()) {
            ReadingRecord existing = update(record.getId(), record);
            bookIsbnSet.add(existing.getBookIsbn());
        }
        for (Long id : request.getDeleteList()) {
            ReadingRecord existingReadingRecord = resourceAccessUtil.checkAccessAndRetrieve(id);
            bookIsbnSet.add(existingReadingRecord.getBookIsbn());
            readingRecordRepository.delete(existingReadingRecord);
        }

        if (bookIsbnSet.size() > 1) {
            throw new IllegalArgumentException("책 정보가 일치하지 않습니다. 문제가 계속되면 관리자에게 문의해 주세요");
        }

        // (이후 연계 작업) 완독 이력 모두 삭제됐을 시 독서 상태 '읽을 예정' 상태로 자동 변경
        String bookIsbn = bookIsbnSet.iterator().next();
        Integer remainingCompletedReadingRecordCount = readingRecordRepository
                .countByUserIdAndBookIsbnAndEndDateIsNotNull(userId, bookIsbn);

        if (remainingCompletedReadingRecordCount < 1) {
            readingBookRepository.findByUserIdAndBookIsbn(userId, bookIsbn)
                    .ifPresent(readingBook -> readingBook.updateReadingStatus(ReadingStatus.TO_READ));
        }

        return remainingCompletedReadingRecordCount;
    }

    /**
     * (공통 메서드) UPDATE 로직
     */
    private ReadingRecord update(Long id, ReadingRecord record) {
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
     * (공통 메서드) 독서 이력 검증 <- batch update 기능 때문에 예외적으로 서비스 레이어에서 유효성 검사 수행
     */
    private void validateReadingRecord(ReadingRecord readingRecord, boolean endDateChange) {
        if (readingRecord.getStartDate() == null) {
            throw new IllegalArgumentException("날짜를 입력해 주세요.");
        }
        if (readingRecord.getStartDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("유효하지 않은 날짜입니다.<br/>오늘 또는 오늘 이전의 날짜를 선택해 주세요.");
        }

        // 완독일도 검사
        if (endDateChange) {
            if (readingRecord.getEndDate() == null) {
                throw new IllegalArgumentException("날짜를 입력해 주세요.");
            }
            if (readingRecord.getEndDate().isBefore(readingRecord.getStartDate())) {
                throw new IllegalArgumentException("유효하지 않은 날짜입니다.<br/>시작일 이후 날짜를 선택해 주세요.");
            }
            if (readingRecord.getEndDate().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("유효하지 않은 날짜입니다.<br/>오늘 또는 오늘 이전의 날짜를 선택해 주세요.");
            }
        }
    }
}
