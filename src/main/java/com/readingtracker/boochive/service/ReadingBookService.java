package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.dto.book.BookDto;
import com.readingtracker.boochive.dto.book.PageableBookListResponse;
import com.readingtracker.boochive.dto.common.BatchUpdateRequest;
import com.readingtracker.boochive.dto.reading.ReadingBookCondition;
import com.readingtracker.boochive.dto.reading.ReadingBookRequest;
import com.readingtracker.boochive.dto.reading.ReadingBookResponse;
import com.readingtracker.boochive.enums.ReadingStatus;
import com.readingtracker.boochive.mapper.BookMapper;
import com.readingtracker.boochive.mapper.ReadingBookMapper;
import com.readingtracker.boochive.repository.PurchaseHistoryRepository;
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
    private final ResourceAccessUtil<BookCollection> collectionResourceAccessUtil;

    private final PurchaseHistoryRepository purchaseHistoryRepository;

    private final BookService bookService;
    private final ReadingRecordService readingRecordService;
    private final AladdinOpenAPIHandler aladdinOpenAPIHandler;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<ReadingBookResponse> findReadingBookById(Long id) {
        return readingBookRepository.findById(id)
                .map(ReadingBookMapper.INSTANCE::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ReadingBookResponse> findReadingBookByUserAndBook(Long userId, String bookIsbn) {
        return readingBookRepository.findByUserIdAndBookIsbn13(userId, bookIsbn)
                .map(ReadingBookMapper.INSTANCE::toDto);
    }

    @Transactional(readOnly = true)
    public Map<String, List<ReadingBookResponse>> getBookListGroupByReadingStatus(Long userId) {
        List<ReadingBookResponse> readingList = readingBookRepository.findAllByUserId(userId)
                .stream()
                .map(readingBook -> {
                    ReadingBookResponse readingBookResponse = ReadingBookMapper.INSTANCE.toDto(readingBook);
                    readingBookResponse.setBookInfo(BookMapper.INSTANCE.toDto(readingBook.getBook()));
                    return readingBookResponse;
                })
                .toList();

        // Reading Status를 기준으로 그룹화하고, 정렬
        return readingList.stream()
                .collect(Collectors.groupingBy(ReadingBookResponse::getReadingStatus)) // 독서 상태가 key
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparingInt(ReadingStatus::getPriority))) // 독서 상태 우선순위에 따라 정렬
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(), // Enum 코드 값에서 name으로 변환
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new // 최종 결과도 LinkedHashMap으로 정렬 순서 유지
                ));
    }

    @Transactional(readOnly = true)
    public List<ReadingBookResponse> getReadingListByUserAndBookList(Long userId, List<String> bookIsbnList) {
        return readingBookRepository.findAllByUserIdAndBookIsbn13In(userId, bookIsbnList)
                .stream()
                .map(ReadingBookMapper.INSTANCE::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReadingBookResponse> getReadingListByUserAndCollectionList(Long userId, List<Long> collectionIdList) {
        return readingBookRepository.findAllByUserIdAndCollectionIdIn(userId, collectionIdList)
                .stream()
                .map(ReadingBookMapper.INSTANCE::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReadingBookResponse> getIncompleteReadingListWithBookDetailByUser(Long userId) {
        List<ReadingBookResponse> readingList = readingBookRepository
                .findAllByUserIdAndReadingStatus(userId, ReadingStatus.READING)
                .stream()
                .map(ReadingBookMapper.INSTANCE::toDto)
                .toList();

        // 2. ISBN을 기반으로 책 상세 정보, 독서 이력(독서 시작일 추출)을 한 번에 조회
        List<String> isbnList = readingList.stream()
                .map(ReadingBookResponse::getBookIsbn)
                .toList();

        Map<String, BookDto> bookInfoMap = getReadingBookDetailData(isbnList);

        // 3. 각 독서 레코드에 추가 정보 세팅
        readingList.forEach(readingBook -> {
            readingBook.setBookInfo(bookInfoMap.get(readingBook.getBookIsbn()));
            readingRecordService.findLatestReadingRecordByUserAndBook(userId, readingBook.getBookIsbn())
                    .ifPresent(record -> readingBook.setReadingStartDate(record.getStartDate()));
        });

        return readingList;
    }

    @Transactional(readOnly = true)
    public Page<ReadingBookResponse> getReadingListWithBookDetailByUserAndFilters(User user, ReadingBookCondition condition, Pageable pageable) {
        Page<ReadingBook> pageableReadingList = readingBookDslRepository.findAllByUserAndFilters(user, condition, pageable);

        // 1. Page<Domain>를 List<Dto>로 변환
        List<ReadingBookResponse> readingList = pageableReadingList.getContent()
                .stream()
                .map(ReadingBookMapper.INSTANCE::toDto)
                .toList();

        // 2. ISBN을 기반으로 책 상세 정보, 책 구매 이력(소장 여부 추출)을 한 번에 조회
        List<String> isbnList = readingList.stream()
                .map(ReadingBookResponse::getBookIsbn)
                .toList();

        Map<String, BookDto> bookInfoMap = getReadingBookDetailData(isbnList);
        List<String> ownedBookList = purchaseHistoryRepository
                .findAllByUserIdAndBookIsbnIn(user.getId(), isbnList)
                .stream()
                .map(PurchaseHistory::getBookIsbn)
                .toList();

        // 3. 각 독서 레코드에 추가 정보 세팅
        readingList.forEach(readingBook -> {
            readingBook.setBookInfo(bookInfoMap.get(readingBook.getBookIsbn()));
            readingBook.setIsOwned(!ownedBookList.isEmpty() && ownedBookList.contains(readingBook.getBookIsbn()));
        });

        return new PageImpl<>(readingList, pageable, pageableReadingList.getTotalElements());
    }

    // 책 독자 수 계산
    @Transactional(readOnly = true)
    public Integer getBookReaderCount(String bookIsbn) {
        return readingBookRepository.countByBookIsbn13AndReadingStatus(bookIsbn, ReadingStatus.READ);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public ReadingBookResponse createReadingBook(ReadingBookRequest readingBook) {
        ReadingBook newReadingBook = ReadingBookMapper.INSTANCE.toEntity(readingBook);

        // 저장 전 참조 데이터(컬렉션) 유효성 검증
        validateBookCollection(readingBook.getCollectionId());

        ReadingBook createdReadingBook = readingBookRepository.save(newReadingBook);

        // (이후 연계 작업)
        // 책 데이터 저장
        saveBook(readingBook.getBookIsbn());
        // 독서 상태에 따라 독서 이력 데이터 자동 생성 및 변경
        saveReadingRecord(createdReadingBook);

        return ReadingBookMapper.INSTANCE.toDto(createdReadingBook);
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public ReadingBookResponse updateReadingBook(Long id, ReadingBookRequest readingBook) {
        ReadingBook existingReadingBook = resourceAccessUtil.checkAccessAndRetrieve(id);

        // 저장 전 참조 데이터(컬렉션) 유효성 검증
        BookCollection collection = validateBookCollection(readingBook.getCollectionId());

        ReadingStatus newReadingStatus = ReadingStatus.valueOf(readingBook.getReadingStatus());
        boolean readingStatusChanged = !existingReadingBook.getReadingStatus().equals(newReadingStatus);
        if (readingStatusChanged) {
            existingReadingBook.updateReadingStatus(newReadingStatus);
        }
        if (!Objects.equals(existingReadingBook.getCollection(), collection)) {
            existingReadingBook.updateCollection(collection);
        }

        // (이후 연계 작업) 독서 상태에 따라 독서 이력 데이터 자동 생성 및 변경
        if (readingStatusChanged) {
            saveReadingRecord(existingReadingBook);
        }

        return ReadingBookMapper.INSTANCE.toDto(existingReadingBook);
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
        delete(id);
    }

    /**
     * CRU[D] - BATCH DELETE
     */
    @Transactional
    public void batchDeleteReadingBooks(BatchUpdateRequest<ReadingBook> request) {
        for (Long id : request.getDeleteList()) {
            delete(id);
        }
    }

    /**
     * (공통 메서드) DELETE 로직
     */
    private void delete(Long id) {
        ReadingBook existingReadingBook = resourceAccessUtil.checkAccessAndRetrieve(id);

        readingBookRepository.delete(existingReadingBook);
    }

    /**
     * (공통 메서드) 참조 데이터 유효성 검증 - 컬렉션
     */
    private BookCollection validateBookCollection(Long collectionId) {
        BookCollection collection = null;
        if (collectionId != null) {
            collection = collectionResourceAccessUtil.checkAccessAndRetrieve(collectionId);
        }
        return collection;
    }

    /**
     * (공통 메서드) 책 데이터 생성 및 변경
     */
    private void saveBook(String isbn) {
        Optional<BookDto> existingBook = bookService.findBookByIsbn(isbn);

        if (existingBook.isEmpty()) {
            PageableBookListResponse lookupResult = aladdinOpenAPIHandler.lookupBook(isbn);
            BookDto book = lookupResult.getItem().get(0);

            bookService.createBook(book);
        }
    }

    /**
     * (공통 메서드) 독서 상태에 따라 독서 이력 데이터 자동 생성 및 변경
     */
    private void saveReadingRecord(ReadingBook readingBook) {
        // 읽는 중 > 독서 이력 생성 / 읽음 > 완독일 세팅
        Long userId = readingBook.getUser().getId();
        String bookIsbn = readingBook.getBook().getIsbn13();
        LocalDate today = LocalDate.now();

        if (readingBook.getReadingStatus().equals(ReadingStatus.READING)) { // 읽는 중
            // 금일자가 독서시작일인 이력 찾기
            readingRecordService
                    .findRecordByUserAndBookAndStartDate(userId, bookIsbn, today)
                    .ifPresentOrElse(
                            record -> {
                                // 이미 있는 경우, 완독일 지우기
                                record.updateEndDate(null);
                            },
                            () -> {
                                // 없는 경우, 금일자를 독서시작일로 새로 생성
                                ReadingRecord newReadingRecord = ReadingRecord.builder()
                                        .user(readingBook.getUser())
                                        .bookIsbn(bookIsbn)
                                        .startDate(today)
                                        .build();
                                readingRecordService.createReadingRecord(newReadingRecord);
                            }
                    );

        } else if (readingBook.getReadingStatus().equals(ReadingStatus.READ)) { // 읽음
            // 가장 최근 독서 이력 찾기 (완독일이 없는 이력)
            readingRecordService
                    .findLatestReadingRecordByUserAndBook(userId, bookIsbn)
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
                                        .bookIsbn(bookIsbn)
                                        .startDate(today)
                                        .endDate(today)
                                        .build();
                                readingRecordService.createReadingRecord(newReadingRecord);
                            }
                    );
        }
    }

    /**
     * (공통 메서드) 책 상세 정보 데이터 조회 (DTO 데이터 전처리)
     */
    private Map<String, BookDto> getReadingBookDetailData(List<String> isbnList) {
        return bookService.getBooksByIsbnList(isbnList)
                .stream()
                .collect(Collectors.toMap(BookDto::getIsbn13, bookInfo -> bookInfo));
    }
}
