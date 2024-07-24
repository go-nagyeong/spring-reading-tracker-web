package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.ReadingRecord;
import com.readingtracker.boochive.domain.ReadingStatus;
import com.readingtracker.boochive.dto.ReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookService {

    private final ReviewService reviewService;
    private final ReadingBookService readingBookService;
    private final ReadingRecordService readingRecordService;

    /**
     * C[R]UD - READ (책 관련 정보 > 리뷰 개수, 평균 평점, 독자 수)
     */
    public Map<String, Object> getBookSubInfo(String bookIsbn) {
        List<ReviewDto> reviewList = reviewService.getReviewsByBook(bookIsbn);
        List<ReadingBook> readerList = readingBookService.getReadingListByBookAndReadingStatus(bookIsbn, ReadingStatus.READ);

        Map<String, Object> data = new HashMap<>();

        // 리뷰 개수
        data.put("reviewCount", reviewList.size());

        // 평균 평점 = 0을 제외한 평점들의 평균
        double averageRating = reviewList.stream()
                .mapToDouble(ReviewDto::getRating)
                .filter(rating -> rating != 0)
                .average()
                .orElse(0);
        data.put("averageRating", BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP));

        // 독자 수 = Unique(리뷰 작성자 + 해당 책을 '읽음' 상태로 가지고 있는 사용자)
        Set<Long> uniqueReaderList = new HashSet<>(); // HashSet => unique
        reviewList.forEach(review -> uniqueReaderList.add(review.getReviewerId()));
        readerList.forEach(readingBook -> uniqueReaderList.add(readingBook.getUser().getId()));
        data.put("readerCount", uniqueReaderList.size());

        return data;
    }

    /**
     * C[R]UD - READ (사용자 독서 책 관련 정보 > 작성 평점, 완독 수, 노트 수)
     */
    public Map<String, Integer> getUserBookSubInfo(Long userId, String bookIsbn) {
        Optional<ReviewDto> userReview =  reviewService.findReviewByUserAndBook(userId, bookIsbn);
        List<ReadingRecord> completedReadingRecordList = readingRecordService
                .getCompletedReadingRecordsByUserAndBook(userId, bookIsbn);

        Map<String, Integer> data = new HashMap<>();

        // 작성 평점
        userReview.ifPresent(review -> data.put("userRating", review.getRating()));

        // 완독 수
        data.put("userReadCount", completedReadingRecordList.size());

        // TODO: 노트 수
        data.put("userNoteCount", 0);

        return data;
    }

}