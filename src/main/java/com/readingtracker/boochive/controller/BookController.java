package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.AladdinAPIResponseDto;
import com.readingtracker.boochive.dto.ReviewDto;
import com.readingtracker.boochive.service.CollectionService;
import com.readingtracker.boochive.service.ReadingBookService;
import com.readingtracker.boochive.service.ReviewService;
import com.readingtracker.boochive.util.AladdinOpenAPIHandler;
import com.readingtracker.boochive.util.ApiResponse;
import com.readingtracker.boochive.util.QueryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final AladdinOpenAPIHandler aladdinOpenAPIHandler;
    private final ReviewService reviewService;
    private final ReadingBookService readingListService;
    private final CollectionService collectionService;

    /**
     * 책 검색
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchBooks(@RequestParam String query,
                                                                        @RequestParam(defaultValue = "1") Integer page,
                                                                        @AuthenticationPrincipal User user) {
        // 책 검색 결과
        AladdinAPIResponseDto searchResult = aladdinOpenAPIHandler.searchBooks(page, query, QueryType.TITLE);

        // 책 관련 통계 (리뷰 개수, 평균 평점, 독자 수)
        for (AladdinAPIResponseDto.Item book : searchResult.getItem()) {
            setBookStatistics(book);
        }

        // 사용자의 컬렉션 목록
        List<BookCollection> collectionList = collectionService.getCollectionsByUser(user.getId());

        // 사용자의 독서 상태 및 컬렉션 정보
        Map<String, ReadingBook> readingInfoList = readingListService.getReadingListByUser(user.getId())
                .stream()
                .collect(Collectors.toMap(ReadingBook::getBookIsbn, readingBook -> readingBook));

        Map<String, Object> data = new HashMap<>();
        data.put("searchResult", searchResult);
        data.put("collectionList", collectionList);
        data.put("readingInfoList", readingInfoList);

        return ApiResponse.success(null, data);
    }

    /**
     * 특정 책 상세
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBookDetails(@PathVariable String isbn,
                                                                           @AuthenticationPrincipal User user) {
        // 책 조회 결과
        AladdinAPIResponseDto lookupResult = aladdinOpenAPIHandler.lookupBook(isbn);

        // 책 관련 통계 (리뷰 개수, 평균 평점, 독자 수)
        AladdinAPIResponseDto.Item book = lookupResult.getItem().get(0);
        setBookStatistics(book);

        // 사용자의 컬렉션 목록
        List<BookCollection> collectionList = collectionService.getCollectionsByUser(user.getId());

        // 사용자의 독서 상태 및 컬렉션 정보
        Optional<ReadingBook> readingInfo = readingListService.findReadingBookByUserAndBook(user.getId(), isbn);

        Map<String, Object> data = new HashMap<>();
        data.put("lookupResult", lookupResult);
        data.put("collectionList", collectionList);
        if (readingInfo.isPresent()) {
            data.put("readingInfo", readingInfo);
        }

        return ApiResponse.success(null, data);
    }

    /**
     * (공통 메서드) 책 통계 데이터 세팅 - 리뷰 개수, 평균 평점, 독자 수
     */
    private void setBookStatistics(AladdinAPIResponseDto.Item book) {
        List<ReviewDto> reviewList = reviewService.getReviewsByBook(book.getIsbn13());

        // 리뷰 개수
        book.setReviewCount(reviewList.size());
        // 평균 평점 = 0을 제외한 평점들의 평균
        double averageRating = reviewList.stream()
                .mapToDouble(ReviewDto::getRating)
                .filter(rating -> rating != 0)
                .average()
                .orElse(0);
        book.setAverageRating(BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP));

        // 독자 수 = Unique(리뷰를 작성한 사용자 + 해당 책을 '읽음' 상태로 가지고 있는 사용자)
        Set<Long> readerList = new HashSet<>();
        reviewList.forEach(review -> readerList.add(review.getReviewerId()));
        readingListService.getReadingListByBook(book.getIsbn13())
                .forEach(readingBook -> readerList.add(readingBook.getUserId()));

        book.setReaderCount(readerList.size());
    }
}
