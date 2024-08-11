package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.dto.BookParameter;
import com.readingtracker.boochive.dto.PageableBookListResponse;
import com.readingtracker.boochive.dto.ReadingBookDetailResponse;
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
    private final ReviewService reviewService;
    private final ReadingBookService readingBookService;

    /**
     * 책 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchBooks(@RequestParam String query,
                                                                        @RequestParam(defaultValue = "1") Integer page,
                                                                        @AuthenticationPrincipal User user) {
        // 책 검색 결과
        PageableBookListResponse searchResult = aladdinOpenAPIHandler.searchBooks(page, query, QueryType.TITLE);

        // 책 통계 정보 세팅
        List<String> bookIsbnList = searchResult.getItem().stream()
                .map(book -> {
                    setBookStatistics(book);
                    return book.getIsbn13();
                })
                .toList();

        // 사용자의 독서 상태 및 컬렉션 정보
        Map<String, ReadingBookDetailResponse> readingInfoList = readingBookService
                .getReadingListByUserAndBookList(user.getId(), bookIsbnList)
                .stream()
                .collect(Collectors.toMap(ReadingBookDetailResponse::getBookIsbn, readingBook -> readingBook));

        Map<String, Object> data = new HashMap<>();
        data.put("searchResult", searchResult);
        data.put("readingInfoList", readingInfoList);

        return ApiResponse.success(null, data);
    }

    /**
     * 특정 책 상세
     */
    @GetMapping("/detail/{isbn}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBookDetails(@PathVariable String isbn,
                                                                           @AuthenticationPrincipal User user) {
        // 책 조회 결과
        PageableBookListResponse lookupResult = aladdinOpenAPIHandler.lookupBook(isbn);
        BookParameter book = lookupResult.getItem().get(0);

        // 책 통계 정보 세팅
        setBookStatistics(book);

        // 사용자의 독서 상태 및 컬렉션 정보
        Optional<ReadingBookDetailResponse> readingInfo = readingBookService
                .findReadingBookByUserAndBook(user.getId(), isbn);

        Map<String, Object> data = new HashMap<>();
        data.put("lookupResult", lookupResult);
        readingInfo.ifPresent(info -> data.put("readingInfo", info));

        return ApiResponse.success(null, data);
    }

    /**
     * (공통 메서드) 책 통계 정보 세팅 - 리뷰 개수, 평균 평점, 독자 수
     */
    private void setBookStatistics(BookParameter book) {
        BookParameter.SubInfo subInfo = book.getSubInfo();

        Map<String, Object> reviewInfo = reviewService.getBookReviewInfo(book.getIsbn13());

        subInfo.setReviewCount((Integer) reviewInfo.get("reviewCount"));
        subInfo.setAverageRating((BigDecimal) reviewInfo.get("averageRating"));
        subInfo.setReaderCount(readingBookService.getBookReaderCount(book.getIsbn13()));
    }
}
