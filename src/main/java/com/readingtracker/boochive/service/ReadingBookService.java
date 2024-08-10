package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.dto.*;
import com.readingtracker.boochive.repository.ReadingBookDslRepositoryImpl;
import com.readingtracker.boochive.repository.ReadingBookJpaRepository;
import com.readingtracker.boochive.util.AladdinOpenAPIHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReadingBookService {

    private final ReadingBookJpaRepository readingBookRepository;
    private final ReadingBookDslRepositoryImpl readingBookDslRepository;

    private final BookService bookService;
    private final ReadingRecordService readingRecordService;
    private final AladdinOpenAPIHandler aladdinOpenAPIHandler;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<ReadingBook> findReadingBookById(Long id) {
        return readingBookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ReadingBook> findReadingBookByUserAndBook(Long userId, String bookIsbn) {
        return readingBookRepository.findByUserIdAndBookIsbn(userId, bookIsbn);
    }

    @Transactional(readOnly = true)
    public List<ReadingBook> getReadingListByUser(Long userId) {
        return readingBookRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<ReadingBook> getReadingListByUserAndBookList(Long userId, List<String> bookIsbnList) {
        return readingBookRepository.findAllByUserIdAndBookIsbnIn(userId, bookIsbnList);
    }

    @Transactional(readOnly = true)
    public Page<ReadingBook> getReadingListByUserAndOtherFilter(ReadingBookFilterDto filterDto, Pageable pageable,
                                                                User user) {
        return readingBookDslRepository.getReadingBooksWithFilter(filterDto, pageable, user);
    }

    // 책 독자 수 계산
    @Transactional(readOnly = true)
    public Integer getBookReaderCount(String bookIsbn) {
        return readingBookRepository.countByBookIsbnAndReadingStatus(bookIsbn, ReadingStatus.READ);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public ReadingBook createReadingBook(ReadingBook readingBook) {
        ReadingBook createdReadingBook = readingBookRepository.save(readingBook);

        // 이후 연계 작업
        // 1. 책 데이터 저장
        saveBook(readingBook.getBookIsbn());

        // 2. 독서 상태에 따라 독서 이력 데이터 자동 생성 및 변경
        saveReadingRecord(createdReadingBook);

        return createdReadingBook;
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public ReadingBook updateReadingBook(Long id, ReadingBook readingBook) {
        ReadingBook existingReadingBook = readingBookRepository.findById(id).orElseThrow();

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

    @Transactional
    public void nullifyCollectionReference(Long collectionId) {
        readingBookRepository.nullifyCollectionReferenceByCollectionId(collectionId);
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteReadingBookById(Long id) {
        readingBookRepository.deleteById(id);
    }

    /**
     * CRU[D] - BATCH DELETE
     */
    @Transactional
    public void batchDeleteReadingBooks(BatchUpdateDto<Long> batchUpdateDto) {
        for (Long id : batchUpdateDto.getDeleteList()) {
            readingBookRepository.deleteById(id);
        }
    }

    /**
     * (공통 메서드) 책 데이터 생성 및 변경
     */
    private void saveBook(String isbn) {
        Optional<Book> existingbook = bookService.findBookByIsbn(isbn);

        if (existingbook.isEmpty()) {
            PageableBookListDto lookupResult = aladdinOpenAPIHandler.lookupBook(isbn);
            BookDto book = lookupResult.getItem().get(0);

            LocalDate parsedPubDate = LocalDate.parse(book.getPubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            Book newBook = Book.builder()
                    .isbn13(book.getIsbn13())
                    .isbn10(book.getIsbn())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .translator(book.getTranslator())
                    .publisher(book.getPublisher())
                    .pubDate(parsedPubDate)
                    .description(book.getDescription())
                    .cover(book.getCover())
                    .pages(Integer.valueOf(book.getSubInfo().getItemPage()))
                    .build();

            bookService.createBook(newBook);
        }
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
                    .findReadingRecordByUserAndBookAndStartDate(readingBook.getUser().getId(), readingBook.getBookIsbn(), today)
                    .ifPresentOrElse(
                            record -> {
                                // 이미 있는 경우, 완독일만 초기화
                                record.updateEndDate(null);
                                readingRecordService.updateReadingRecord(record.getId(), record);
                            },
                            () -> {
                                // 없는 경우, 금일자를 독서시작일로 새로 생성
                                ReadingRecord newReadingRecord = ReadingRecord.builder()
                                        .user(readingBook.getUser())
                                        .bookIsbn(readingBook.getBookIsbn())
                                        .startDate(today)
                                        .build();
                                readingRecordService.createReadingRecord(newReadingRecord);
                            }
                    );

        } else if (readingBook.getReadingStatus().equals(ReadingStatus.READ)) { // 읽음
            // 가장 최근 독서 이력 찾기
            readingRecordService
                    .findLatestReadingRecordByUserAndBook(readingBook.getUser().getId(), readingBook.getBookIsbn())
                    .ifPresentOrElse(
                            record -> {
                                // 있는 경우, 완독일을 금일자로 업데이트
                                record.updateEndDate(today);
                                readingRecordService.updateReadingRecord(record.getId(), record);
                            },
                            () -> {
                                // 없는 경우, 금일자를 독서시작일/완독일로 새로 생성
                                ReadingRecord newReadingRecord = ReadingRecord.builder()
                                        .user(readingBook.getUser())
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
