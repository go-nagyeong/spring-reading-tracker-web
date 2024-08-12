package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.ReadingRecord;
import com.readingtracker.boochive.domain.ReadingStatus;
import com.readingtracker.boochive.dto.BatchUpdateRequest;
import com.readingtracker.boochive.exception.ResourceNotFoundException;
import com.readingtracker.boochive.repository.ReadingBookJpaRepository;
import com.readingtracker.boochive.repository.ReadingRecordRepository;
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

    /**
     * C[R]UD - READ
     */
    @Transactional
    public Optional<ReadingRecord> findReadingRecordById(Long id) {
        return readingRecordRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ReadingRecord> findReadingRecordByUserAndBookAndStartDate(Long userId, String bookIsbn, LocalDate startDate) {
        return readingRecordRepository.findByUserIdAndBookIsbnAndStartDate(userId, bookIsbn, startDate);
    }

    @Transactional(readOnly = true)
    public Optional<ReadingRecord> findLatestReadingRecordByUserAndBook(Long userId, String bookIsbn) {
        return readingRecordRepository.findFirstByUserIdAndBookIsbnAndEndDateIsNullOrderByStartDateDesc(userId, bookIsbn);
    }

    @Transactional(readOnly = true)
    public List<ReadingRecord> getCompletedReadingRecordsByUserAndBook(Long userId, String bookIsbn) {
        return readingRecordRepository.findAllByUserIdAndBookIsbnAndEndDateIsNotNull(userId, bookIsbn);
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
        delete(id);
    }

    /**
     * CR[U][D] - BATCH UPDATE/DELETE
     */
    @Transactional
    public List<ReadingRecord> handleReadingRecords(BatchUpdateRequest<ReadingRecord> request, Long userId) {
        List<ReadingRecord> allRecords = request.getUpdateList();
        allRecords.addAll(request.getDeleteList());

        String bookIsbn = allRecords.stream()
                .map(ReadingRecord::getBookIsbn)
                .findFirst()
                .orElse(null);

        for (ReadingRecord record : request.getUpdateList()) {
            update(record.getId(), record);
        }
        for (ReadingRecord record : request.getDeleteList()) {
            delete(record.getId());
        }

        List<ReadingRecord> remainingReadingRecords = readingRecordRepository.findAllByUserIdAndBookIsbn(userId, bookIsbn);

        // (이후 연계 작업) 이력 데이터 모두 삭제 시 독서 상태 자동 변경
        if (remainingReadingRecords.isEmpty()) {
            // 독서 이력이 없으니 '읽을 예정' 상태로 복구
            readingBookRepository.findByUserIdAndBookIsbn(userId, bookIsbn)
                    .ifPresent(readingBook -> readingBook.updateReadingStatus(ReadingStatus.TO_READ));
        }

        return remainingReadingRecords;
    }

    /**
     * (공통 메서드) UPDATE 로직
     */
    private ReadingRecord update(Long id, ReadingRecord record) {
        ReadingRecord existingReadingRecord = readingRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("독서 이력"));

        boolean endDateChanged = !Objects.equals(existingReadingRecord.getEndDate(), record.getEndDate());
        if (!Objects.equals(existingReadingRecord.getStartDate(), record.getStartDate())) {
            existingReadingRecord.updateStartDate(record.getStartDate());
        }
        if (endDateChanged) {
            existingReadingRecord.updateEndDate(record.getEndDate());
        }

        validateReadingRecord(record, endDateChanged); // 날짜 유효성 검사

        return record;
    }

    /**
     * (공통 메서드) DELETE 로직
     */
    private void delete(Long id) {
        // 데이터 존재 여부 검사
        ReadingRecord existingReadingRecord = readingRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("독서 이력"));

        readingRecordRepository.delete(existingReadingRecord);
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
