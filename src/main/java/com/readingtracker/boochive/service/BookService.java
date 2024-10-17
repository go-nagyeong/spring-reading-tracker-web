package com.readingtracker.boochive.service;

import com.readingtracker.boochive.config.AppConstants;
import com.readingtracker.boochive.domain.Book;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.book.BookDto;
import com.readingtracker.boochive.dto.book.PageableBookListResponse;
import com.readingtracker.boochive.dto.reading.ReadingBookResponse;
import com.readingtracker.boochive.dto.statistics.BookStatisticsDto;
import com.readingtracker.boochive.enums.QueryType;
import com.readingtracker.boochive.exception.AladinApiException;
import com.readingtracker.boochive.repository.BookRepository;
import com.readingtracker.boochive.util.AladdinOpenAPIHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AladdinOpenAPIHandler aladdinOpenAPIHandler;

    private final ReadingBookService readingBookService;

    /**
     * C[R]UD - READ
     *   책 검색
     */
    @Transactional(readOnly = true)
    public Map<String, Object> searchBooks(String query, Integer page, User user) {
        // 1. 검색 키워드 검증
        validateSearchQuery(query);

        // 2. 외부 API에서 책 검색 결과 가져오기
        PageableBookListResponse searchResult = aladdinOpenAPIHandler.searchBooks(page, query, QueryType.TITLE);
        List<BookDto> books = searchResult.getItem();

        List<String> bookIsbnList = searchResult.getItem().stream().map(BookDto::getIsbn13).toList();

        // 3. 책 통계 정보 세팅
        Map<String, BookStatisticsDto> bookStatistics = bookRepository.getBookStatistics(bookIsbnList)
                .stream()
                .map(result -> new BookStatisticsDto(
                        (String) result[0],
                        (Long) result[1],
                        (BigDecimal) result[2],
                        (Long) result[3]
                ))
                .collect(Collectors.toMap(BookStatisticsDto::getBookIsbn, statistics -> statistics));
        books.forEach(book ->
            book.setStatistics(bookStatistics.getOrDefault(
                    book.getIsbn13(), new BookStatisticsDto(book.getIsbn13(), 0L, BigDecimal.ZERO, 0L)
            ))
        );

        // 4. 사용자의 독서 상태 및 컬렉션 정보 가져오기
        Map<String, ReadingBookResponse> readingInfoList = readingBookService
                .getReadingListByUserAndBookList(user.getId(), bookIsbnList)
                .stream()
                .collect(Collectors.toMap(ReadingBookResponse::getBookIsbn, readingBook -> readingBook));

        Map<String, Object> data = new HashMap<>();
        data.put("searchResult", searchResult);
        data.put("readingInfoList", readingInfoList);

        return data;
    }

    /**
     * C[R]UD - READ
     *   책 상세 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getBookDetails(String isbn, User user) {
        // 1. 외부 API에서 책 상세 정보 가져오기
        PageableBookListResponse lookupResult = aladdinOpenAPIHandler.lookupBook(isbn);
        if (lookupResult.getErrorCode() != null) { // 잘못된 ISBN
            throw new AladinApiException(lookupResult.getErrorMessage());
        }
        BookDto bookDetail = lookupResult.getItem().get(0);

        // 2. 책 통계 정보 세팅
        Map<String, BookStatisticsDto> bookStatistics = bookRepository
                .getBookStatistics(Collections.singletonList(bookDetail.getIsbn13()))
                .stream()
                .map(result -> new BookStatisticsDto(
                        (String) result[0],
                        (Long) result[1],
                        (BigDecimal) result[2],
                        (Long) result[3]
                ))
                .collect(Collectors.toMap(BookStatisticsDto::getBookIsbn, statistics -> statistics));
        bookDetail.setStatistics(bookStatistics.getOrDefault(
                bookDetail.getIsbn13(), new BookStatisticsDto(bookDetail.getIsbn13(), 0L, BigDecimal.ZERO, 0L)
        ));

        // 4. 사용자의 독서 상태 및 컬렉션 정보 가져오기
        Optional<ReadingBookResponse> readingInfo = readingBookService
                .findReadingBookByUserAndBook(user.getId(), isbn);

        Map<String, Object> data = new HashMap<>();
        data.put("bookDetail", bookDetail);
        readingInfo.ifPresent(info -> data.put("readingInfo", info));

        return data;
    }

    @Transactional(readOnly = true)
    public BookStatisticsDto getBookStatisticsById(String bookIsbn) {
        return bookRepository.getBookStatistics(Collections.singletonList(bookIsbn))
                .stream()
                .map(result -> new BookStatisticsDto(
                        (String) result[0],
                        (Long) result[1],
                        (BigDecimal) result[2],
                        (Long) result[3]
                ))
                .findFirst()
                .orElseGet(() -> new BookStatisticsDto(bookIsbn, 0L, BigDecimal.ZERO, 0L));
    }

    /**
     * (공통 유틸 메서드) 책 ISBN 유효성 검증
     */
    public void validateBookIsbn(String bookIsbn) {
        Optional<Book> existingBook = bookRepository.findByIsbn13(bookIsbn);
        if (existingBook.isEmpty()) {
            PageableBookListResponse lookupResult = aladdinOpenAPIHandler.lookupBook(bookIsbn);
            if (lookupResult.getErrorCode() != null) { // 존재하지 않는 책
                throw new IllegalArgumentException("도서 정보가" + AppConstants.UNKNOWN_INVALID_ARG_ERROR_MSG);
            }
        }
    }

    /**
     * (공통 메서드) 검색 키워드 검증
     */
    private void validateSearchQuery(String query) {
        if (query == null || query.isEmpty() || !query.matches("^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣\\s.,!?'\"(){}\\[\\]/\\\\-]+$")) {
            throw new IllegalArgumentException("유효하지 않은 검색어입니다.");
        }
    }
}