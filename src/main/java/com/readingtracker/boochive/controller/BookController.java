package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.dto.AladdinAPIResponseDto;
import com.readingtracker.boochive.service.*;
import com.readingtracker.boochive.util.AladdinOpenAPIHandler;
import com.readingtracker.boochive.util.ApiResponse;
import com.readingtracker.boochive.util.QueryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final AladdinOpenAPIHandler aladdinOpenAPIHandler;
    private final BookService bookService;
    private final ReadingBookService readingBookService;
    private final CollectionService collectionService;

    /**
     * 책 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchBooks(@RequestParam String query,
                                                                        @RequestParam(defaultValue = "1") Integer page,
                                                                        @AuthenticationPrincipal User user) {
        Map<String, Object> data = new HashMap<>();

        // 책 검색 결과
        AladdinAPIResponseDto searchResult = aladdinOpenAPIHandler.searchBooks(page, query, QueryType.TITLE);

        // 책 관련 통계 (리뷰 개수, 평균 평점, 독자 수)
        List<String> bookIsbnList = new ArrayList<>();
        for (AladdinAPIResponseDto.Item book : searchResult.getItem()) {
            Map<String, Object> bookSubInfo = bookService.getBookSubInfo(book.getIsbn13());
            book.setReviewCount((Integer) bookSubInfo.get("reviewCount"));
            book.setAverageRating((BigDecimal) bookSubInfo.get("averageRating"));
            book.setReaderCount((Integer) bookSubInfo.get("readerCount"));

            bookIsbnList.add(book.getIsbn13());
        }

        // 사용자의 컬렉션 목록
        List<BookCollection> collectionList = collectionService.getCollectionsByUser(user.getId());

        // 사용자의 독서 상태 및 컬렉션 정보
        Map<String, ReadingBook> readingInfoList = readingBookService
                .getReadingListByUserAndBookList(user.getId(), bookIsbnList)
                .stream()
                .collect(Collectors.toMap(ReadingBook::getBookIsbn, readingBook -> readingBook));

        data.put("searchResult", searchResult);
        data.put("collectionList", collectionList);
        data.put("readingInfoList", readingInfoList);

        return ApiResponse.success(null, data);
    }

    /**
     * 특정 책 상세
     */
    @GetMapping("/detail/{isbn}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBookDetails(@PathVariable String isbn,
                                                                           @AuthenticationPrincipal User user) {
        Map<String, Object> data = new HashMap<>();

        // 책 조회 결과
        AladdinAPIResponseDto lookupResult = aladdinOpenAPIHandler.lookupBook(isbn);
        AladdinAPIResponseDto.Item book = lookupResult.getItem().get(0);

        // 책 관련 통계 (리뷰 개수, 평균 평점, 독자 수)
        Map<String, Object> bookSubInfo = bookService.getBookSubInfo(book.getIsbn13());
        book.setReviewCount((Integer) bookSubInfo.get("reviewCount"));
        book.setAverageRating((BigDecimal) bookSubInfo.get("averageRating"));
        book.setReaderCount((Integer) bookSubInfo.get("readerCount"));

        // 사용자의 컬렉션 목록
        List<BookCollection> collectionList = collectionService.getCollectionsByUser(user.getId());

        // 사용자의 독서 상태 및 컬렉션 정보
        Optional<ReadingBook> readingInfo = readingBookService.findReadingBookByUserAndBook(user.getId(), isbn);

        data.put("lookupResult", lookupResult);
        data.put("collectionList", collectionList);
        if (readingInfo.isPresent()) {
            data.put("readingInfo", readingInfo);
        }

        return ApiResponse.success(null, data);
    }
}
