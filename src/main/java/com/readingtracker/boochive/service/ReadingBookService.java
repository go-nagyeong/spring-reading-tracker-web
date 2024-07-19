package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.ReadingRecord;
import com.readingtracker.boochive.domain.ReadingStatus;
import com.readingtracker.boochive.repository.ReadingBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReadingBookService {

    private final ReadingBookRepository readingListRepository;
    private final ReadingRecordService readingRecordService;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<ReadingBook> findReadingBookById(Long id) {
        return readingListRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ReadingBook> findReadingBookByUserAndBook(Long userId, String bookIsbn) {
        return readingListRepository.findByUserIdAndBookIsbn(userId, bookIsbn);
    }

    @Transactional(readOnly = true)
    public List<ReadingBook> getReadingListByUser(Long userId) {
        return readingListRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<ReadingBook> getReadingListByBook(String bookIsbn) {
        return readingListRepository.findAllByBookIsbn(bookIsbn);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public ReadingBook createReadingBook(ReadingBook readingBook) {
        ReadingBook createdReadingBook = readingListRepository.save(readingBook);

        // (이후 연계 작업) 독서 상태에 따라 독서 이력 데이터 자동 생성 및 변경
        saveReadingRecord(createdReadingBook);

        return createdReadingBook;
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public ReadingBook updateReadingBook(Long id, ReadingBook readingBook) {
        ReadingBook existingReadingBook = readingListRepository.findById(id).orElseThrow();

        boolean readingStatusChanged = !existingReadingBook.getReadingStatus().equals(readingBook.getReadingStatus());
        if (readingStatusChanged) {
            existingReadingBook.updateReadingStatus(readingBook.getReadingStatus());
        }
        if (!Objects.equals(existingReadingBook.getCollectionId(), readingBook.getCollectionId())) {
            existingReadingBook.updateCollectionId(readingBook.getCollectionId());
        }

        // (이후 연계 작업) 독서 상태에 따라 독서 이력 데이터 자동 생성 및 변경
        if (readingStatusChanged) {
            saveReadingRecord(existingReadingBook);
        }

        return existingReadingBook;
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteReadingBookById(Long id) {
        readingListRepository.deleteById(id);
    }

    /**
     * (공통 메서드) 독서 상태에 따라 독서 이력 데이터 자동 생성 및 변경
     */
    private void saveReadingRecord(ReadingBook readingBook) {
        // 읽는 중 > 독서시작일, 읽음 > 완독일 세팅 (독서시작일 기준 unique)
        LocalDate today = LocalDate.now();

        if (readingBook.getReadingStatus().equals(ReadingStatus.READING)) { // 읽는 중
            // 금일자가 독서시작일인 이력 찾기
            readingRecordService
                    .findReadingRecordByUserAndBookAndStartDate(readingBook.getUserId(), readingBook.getBookIsbn(), today)
                    .ifPresentOrElse(
                            record -> {
                                // 이미 있는 경우, 완독일만 초기화
                                record.updateEndDate(null);
                                readingRecordService.updateReadingRecord(record.getId(), record);
                            },
                            () -> {
                                // 없는 경우, 금일자를 독서시작일로 새로 생성
                                ReadingRecord newReadingRecord = ReadingRecord.builder()
                                        .userId(readingBook.getUserId())
                                        .bookIsbn(readingBook.getBookIsbn())
                                        .startDate(today)
                                        .build();
                                readingRecordService.createReadingRecord(newReadingRecord);
                            }
                    );

        } else if (readingBook.getReadingStatus().equals(ReadingStatus.READ)) { // 읽음
            // 가장 최근 독서 이력 찾기
            readingRecordService
                    .findLatestReadingRecordByUserAndBook(readingBook.getUserId(), readingBook.getBookIsbn())
                    .ifPresentOrElse(
                            record -> {
                                // 있는 경우, 완독일을 금일자로 업데이트
                                record.updateEndDate(today);
                                readingRecordService.updateReadingRecord(record.getId(), record);
                            },
                            () -> {
                                // 없는 경우, 금일자를 독서시작일/완독일로 새로 생성
                                ReadingRecord newReadingRecord = ReadingRecord.builder()
                                        .userId(readingBook.getUserId())
                                        .bookIsbn(readingBook.getBookIsbn())
                                        .startDate(today)
                                        .endDate(today)
                                        .build();
                                readingRecordService.createReadingRecord(newReadingRecord);
                            }
                    );
        }
    }
}
