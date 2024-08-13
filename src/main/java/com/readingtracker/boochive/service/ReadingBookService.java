package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.dto.*;
import com.readingtracker.boochive.enums.ReadingStatus;
import com.readingtracker.boochive.mapper.ReadingBookDetailMapper;
import com.readingtracker.boochive.repository.ReadingBookDslRepositoryImpl;
import com.readingtracker.boochive.repository.ReadingBookJpaRepository;
import com.readingtracker.boochive.util.AladdinOpenAPIHandler;
import com.readingtracker.boochive.util.ResourceAccessUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadingBookService {

    private final ReadingBookJpaRepository readingBookRepository;
    private final ReadingBookDslRepositoryImpl readingBookDslRepository;
    private final ResourceAccessUtil<ReadingBook> resourceAccessUtil;

    private final BookService bookService;
    private final ReadingRecordService readingRecordService;
    private final PurchaseHistoryService purchaseHistoryService;
    private final AladdinOpenAPIHandler aladdinOpenAPIHandler;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<ReadingBookDetailResponse> findReadingBookById(Long id) {
        return readingBookRepository.findById(id)
                .map(ReadingBookDetailMapper.INSTANCE::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ReadingBookDetailResponse> findReadingBookByUserAndBook(Long userId, String bookIsbn) {
        return readingBookRepository.findByUserIdAndBookIsbn(userId, bookIsbn)
                .map(ReadingBookDetailMapper.INSTANCE::toDto);
    }

    @Transactional(readOnly = true)
    public List<ReadingBookDetailResponse> getReadingListByUserAndBookList(Long userId, List<String> bookIsbnList) {
        return readingBookRepository.findAllByUserIdAndBookIsbnIn(userId, bookIsbnList)
                .stream()
                .map(ReadingBookDetailMapper.INSTANCE::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReadingBookDetailResponse> getReadingListByUserAndCollectionList(Long userId, List<Long> collectionIdList) {
        return readingBookRepository.findAllByUserIdAndCollectionIdIn(userId, collectionIdList)
                .stream()
                .map(ReadingBookDetailMapper.INSTANCE::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ReadingBookDetailResponse> getReadingListWithBookDetailByUserAndFilters(User user, ReadingBookCondition condition, Pageable pageable) {
        Page<ReadingBook> pageableReadingList = readingBookDslRepository.findAllByUserAndFilters(user, condition, pageable);

        // 1. Page<ReadingBook>를 List<ReadingBookDetailResponse>로 변환
        List<String> isbnList = new ArrayList<>();

        List<ReadingBookDetailResponse> readingList = pageableReadingList.getContent()
                .stream()
                .map(readingBook -> {
                    isbnList.add(readingBook.getBookIsbn());
                    return ReadingBookDetailMapper.INSTANCE.toDto(readingBook);
                })
                .toList();

        // 2. ISBN을 기반으로 책 상세 정보, 구매 이력(소장 여부)을 한 번에 조회
        Map<String, BookParameter> bookInfoMap = bookService.getBooksByIsbnList(isbnList)
                .stream()
                .collect(Collectors.toMap(BookParameter::getIsbn13, bookInfo -> bookInfo));
        List<String> ownedBookList = purchaseHistoryService
                .getHistoriesByUserAndBookList(user.getId(), isbnList)
                .stream()
                .map(PurchaseHistoryParameter::getBookIsbn)
                .toList();

        // 3. 각 독서 정보에 책 상세 정보, 소장 여부 정보 설정
        readingList.forEach(readingBook -> {
            readingBook.setBookInfo(bookInfoMap.get(readingBook.getBookIsbn()));
            readingBook.setIsOwned(ownedBookList.contains(readingBook.getBookIsbn()));
        });

        return new PageImpl<>(readingList, pageable, pageableReadingList.getTotalElements());
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
    public ReadingBookDetailResponse createReadingBook(ReadingBook readingBook) {
        // (이전 연계 작업) 책 데이터 저장
        saveBook(readingBook.getBookIsbn());

        ReadingBook createdReadingBook = readingBookRepository.save(readingBook);

        // (이후 연계 작업) 독서 상태에 따라 독서 이력 데이터 자동 생성 및 변경
        saveReadingRecord(createdReadingBook);

        return ReadingBookDetailMapper.INSTANCE.toDto(createdReadingBook);
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public ReadingBookDetailResponse updateReadingBook(Long id, ReadingBook readingBook) {
        ReadingBook existingReadingBook = resourceAccessUtil.checkAccessAndRetrieve(id, ReadingBook.class);

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

        return ReadingBookDetailMapper.INSTANCE.toDto(existingReadingBook);
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
        ReadingBook existingReadingBook = resourceAccessUtil.checkAccessAndRetrieve(id, ReadingBook.class);

        readingBookRepository.delete(existingReadingBook);
    }

    /**
     * CRU[D] - BATCH DELETE
     */
    @Transactional
    public void batchDeleteReadingBooks(BatchUpdateRequest<Long> request) {
        for (Long id : request.getDeleteList()) {
            ReadingBook existingReadingBook = resourceAccessUtil.checkAccessAndRetrieve(id, ReadingBook.class);

            readingBookRepository.delete(existingReadingBook);
        }
    }

    /**
     * (공통 메서드) 책 데이터 생성 및 변경
     */
    private void saveBook(String isbn) {
        Optional<BookParameter> existingBook = bookService.findBookByIsbn(isbn);

        if (existingBook.isEmpty()) {
            PageableBookListResponse lookupResult = aladdinOpenAPIHandler.lookupBook(isbn);
            BookParameter book = lookupResult.getItem().get(0);

            bookService.createBook(book);
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
