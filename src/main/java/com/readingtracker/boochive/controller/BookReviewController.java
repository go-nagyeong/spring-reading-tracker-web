package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.Review;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.service.ReviewService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookReviewController {

    private final ReviewService reviewService;

    /**
     * 책의 전체 리뷰 목록 가져오기
     */
    @GetMapping("/{bookIsbn}/reviews")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllReviews(@PathVariable String bookIsbn,
                                                                                @AuthenticationPrincipal User user) {
        List<Review> reviewList = reviewService.getReviewsByBook(bookIsbn);
        // 사용자 리뷰
        Optional<Review> userReview = reviewList.stream()
                .filter(review -> review.getUser().getId().equals(user.getId()))
                .findFirst();

        Map<String, Object> data = new HashMap<>();
        data.put("reviewList", reviewList);
        userReview.ifPresent(review -> data.put("userReview", review));

        return ApiResponse.success("", data);
    }

    /**
     * 책의 사용자 리뷰 가져오기
     */
    @GetMapping("/{bookIsbn}/reviews/me")
    public ResponseEntity<ApiResponse<Map<String, Review>>> getUserReview(@PathVariable String bookIsbn,
                                                             @AuthenticationPrincipal User user) {
        Map<String, Review> data = new HashMap<>();

        reviewService.findReviewByUserAndBook(user.getId(), bookIsbn)
                .ifPresent(value -> data.put("review", value));

        return ApiResponse.success("", data);
    }

    /**
     * 책 리뷰 작성
     */
    @PostMapping("/{bookIsbn}/reviews")
    public ResponseEntity<ApiResponse<Map<String, Review>>> saveReview(@PathVariable String bookIsbn,
                                                             @RequestBody Review review,
                                                             @AuthenticationPrincipal User user) {
        // 리뷰 내용 검증 (필수값 체크)
        if (review.getReviewText().isBlank()) {
            return ApiResponse.failure("리뷰 내용을 입력해 주세요.");
        }

        review.updateUser(user); // 사용자 ID 세팅

        Review savedReview = null;
        try {
            savedReview = reviewService.saveReview(review);
        } catch (DataAccessException dae) {
            // 데이터베이스 관련 예외 처리
            return ApiResponse.failure("리뷰를 저장하는 중 오류가 발생했습니다.");
        } catch (Exception e) {
            // 기타 예외 처리
            return ApiResponse.failure("리뷰를 저장하는 중 알 수 없는 오류가 발생했습니다.");
        }

        Map<String, Review> data = new HashMap<>();
        data.put("saveResult", savedReview);

        return ApiResponse.success("리뷰가 저장되었습니다.", data);
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<Map<String, Review>>> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReviewById(id);
        } catch (DataAccessException dae) {
            return ApiResponse.failure("리뷰를 삭제하는 중 오류가 발생했습니다.");
        } catch (Exception e) {
            return ApiResponse.failure("리뷰를 삭제하는 중 알 수 없는 오류가 발생했습니다.");
        }

        return ApiResponse.success("리뷰가 삭제되었습니다.");
    }
}
