package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.dto.book.BookDto;
import com.readingtracker.boochive.dto.common.BatchOperationRequest;
import com.readingtracker.boochive.dto.reading.ReadingBookCondition;
import com.readingtracker.boochive.dto.reading.ReadingBookRequest;
import com.readingtracker.boochive.dto.reading.ReadingBookResponse;
import com.readingtracker.boochive.dto.statistics.ReadingBookStatisticsDto;
import com.readingtracker.boochive.enums.ReadingStatus;
import com.readingtracker.boochive.mapper.BookMapper;
import com.readingtracker.boochive.mapper.ReadingBookMapper;
import com.readingtracker.boochive.repository.*;
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

    private final BookRepository bookRepository;
    private final ReadingRecordRepository readingRecordRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;

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
                    // 추가 정보 1. 책 상세 정보
                    readingBookResponse.setBookInfo(BookMapper.INSTANCE.toDto(readingBook.getBook()));
                    return readingBookResponse;
                })
                .toList();

        // Reading Status를 기준으로 그룹화하고, 정렬
        return readingList.stream()
                .collect(Collectors.groupingBy(ReadingBookResponse::getReadingStatus)) // 독서 상태가 key
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(
                        Comparator.comparingInt(ReadingStatus::getPriority) // 독서 상태 우선순위에 따라 정렬
                ))
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
    public List<ReadingBookResponse> getIncompleteReadingListWithBookDetailByUser(Long userId) {
        return readingBookRepository
                .findAllByUserIdAndReadingStatus(userId, ReadingStatus.READING)
                .stream()
                .map(readingBook -> {
                    ReadingBookResponse readingBookResponse = ReadingBookMapper.INSTANCE.toDto(readingBook);
                    // 추가 정보 1. 책 상세 정보
                    readingBookResponse.setBookInfo(BookMapper.INSTANCE.toDto(readingBook.getBook()));
                    // 추가 정보 2. 독서 시작일
                    readingBookResponse.setReadingStartDate(
                            readingBook.getReadingRecords()
                                    .stream()
                                    .max(Comparator.comparing(ReadingRecord::getId))
                                    .map(ReadingRecord::getStartDate)
                                    .orElse(null)
                    );
                    return readingBookResponse;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ReadingBookResponse> getReadingListWithBookDetailByUserAndFilters(User user, ReadingBookCondition condition, Pageable pageable) {
        Page<ReadingBook> pageableReadingList = readingBookDslRepository.findAllByUserAndFilters(user, condition, pageable);

        List<Long> readingBookIds = pageableReadingList.getContent().stream().map(ReadingBook::getId).toList();

        // 1. 사용자 독서 통계 정보 가져오기
        Map<Long, ReadingBookStatisticsDto> readingStatistics = readingBookRepository
                .getReadingBookStatistics(user.getId(), readingBookIds)
                .stream()
                .collect(Collectors.toMap(ReadingBookStatisticsDto::getReadingBookId, statistics -> statistics));

        // 2. 책 소장 여부 정보를 위해 사용자의 전체 구매 이력 가져오기
        List<String> ownedBooks = purchaseHistoryRepository.findAllByUserId(user.getId())
                .stream()
                .map(PurchaseHistory::getBookIsbn)
                .toList();

        // 3. DTO 변환 및 관계 데이터 세팅
        List<ReadingBookResponse> readingList = pageableReadingList.getContent()
                .stream()
                .map(readingBook -> {
                    ReadingBookResponse readingBookResponse = ReadingBookMapper.INSTANCE.toDto(readingBook);
                    // 추가 정보 1. 책 상세 정보
                    readingBookResponse.setBookInfo(BookMapper.INSTANCE.toDto(readingBook.getBook()));
                    // 추가 정보 2. 책 소장 여부
                    readingBookResponse.setIsOwned(ownedBooks.contains(readingBookResponse.getBookIsbn()));
                    // 추가 정보 3. 사용자 독서 통계 정보
                    readingBookResponse.setStatistics(readingStatistics.get(readingBookResponse.getId()));
                    return readingBookResponse;
                })
                .toList();

        return new PageImpl<>(readingList, pageable, pageableReadingList.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ReadingBookStatisticsDto getReadingBookStatisticsById(Long userId, Long readingBookId) {
        return readingBookRepository.getReadingBookStatistics(userId, Collections.singletonList(readingBookId)).get(0);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public ReadingBookResponse createReadingBook(ReadingBookRequest readingBook) {
        ReadingBook newReadingBook = ReadingBookMapper.INSTANCE.toEntity(readingBook);

        // (이전 연계 작업) 책 데이터 생성 및 변경
        saveBook(readingBook.getBookIsbn());

        // 저장 전 참조 데이터(컬렉션) 유효성 검증
        BookCollection collection = validateReferenceBookCollection(readingBook.getCollectionId());
        newReadingBook.updateCollection(collection);

        ReadingBook createdReadingBook = readingBookRepository.save(newReadingBook);

        // (이후 연계 작업) 독서 상태에 따라 독서 이력 데이터 자동 생성 및 변경
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
        BookCollection collection = validateReferenceBookCollection(readingBook.getCollectionId());

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
    public void batchDeleteReadingBooks(BatchOperationRequest<?> request) {
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
    private BookCollection validateReferenceBookCollection(Long collectionId) {
        BookCollection collection = null;
        if (collectionId != null) {
            collection = collectionResourceAccessUtil.checkAccessAndRetrieve(collectionId);
        }
        return collection;
    }

    /**
     * (공통 메서드) 독서 목록 신규 추가 시 책 데이터 자동 생성 또는 변경
     */
    private void saveBook(String isbn) {
        Optional<Book> existingBook = bookRepository.findByIsbn13(isbn);

        if (existingBook.isEmpty()) {
            BookDto newBook = aladdinOpenAPIHandler.lookupBook(isbn);
            bookRepository.save(BookMapper.INSTANCE.toEntity(newBook));
        }
    }

    /**
     * (공통 메서드) 독서 상태에 따라 독서 이력 데이터 자동 생성 및 변경
     */
    private void saveReadingRecord(ReadingBook updatedReadingBook) {
        // 읽는 중 > 독서 이력 생성 / 읽음 > 완독일 세팅
        Long readingBookId = updatedReadingBook.getId();
        Long userId = updatedReadingBook.getUser().getId();
        LocalDate today = LocalDate.now();

        if (updatedReadingBook.getReadingStatus().equals(ReadingStatus.READING)) { // 읽는 중
            // 금일자가 독서시작일인 이력 찾기
            readingRecordRepository
                    .findByUserIdAndReadingBookIdAndStartDate(userId, readingBookId, today)
                    .ifPresentOrElse(
                            record -> {
                                // 이미 있는 경우, 완독일 지우기
                                record.updateEndDate(null);
                            },
                            () -> {
                                // 없는 경우, 금일자를 독서시작일로 새로 생성
                                ReadingRecord newReadingRecord = ReadingRecord.builder()
                                        .user(updatedReadingBook.getUser())
                                        .readingBook(updatedReadingBook)
                                        .startDate(today)
                                        .build();
                                readingRecordRepository.save(newReadingRecord);
                            }
                    );

        } else if (updatedReadingBook.getReadingStatus().equals(ReadingStatus.READ)) { // 읽음
            // 가장 최근 독서 이력 찾기 (완독일이 없는 이력)
            readingRecordRepository
                    .findFirstByUserIdAndReadingBookIdAndEndDateIsNullOrderByIdDesc(userId, readingBookId)
                    .ifPresentOrElse(
                            record -> {
                                // 있는 경우, 완독일을 금일자로 업데이트
                                record.updateEndDate(today);
                            },
                            () -> {
                                // 없는 경우, 금일자를 독서시작일/완독일로 새로 생성
                                ReadingRecord newReadingRecord = ReadingRecord.builder()
                                        .user(updatedReadingBook.getUser())
                                        .readingBook(updatedReadingBook)
                                        .startDate(today)
                                        .endDate(today)
                                        .build();
                                readingRecordRepository.save(newReadingRecord);
                            }
                    );
        }
    }
}
