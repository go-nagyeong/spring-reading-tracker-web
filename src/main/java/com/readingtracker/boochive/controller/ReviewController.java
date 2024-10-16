package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.review.ReviewRequest;
import com.readingtracker.boochive.dto.review.ReviewResponse;
import com.readingtracker.boochive.exception.CustomArgumentNotValidException;
import com.readingtracker.boochive.service.ReviewService;
import com.readingtracker.boochive.util.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Review", description = "Review API")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * GET - 특정 책의 전체 리뷰 목록 조회
     */
    @GetMapping("/books/{bookIsbn}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllReviews(@PathVariable String bookIsbn,
                                                                          @AuthenticationPrincipal User user) {
        List<ReviewResponse> reviewList = reviewService.getLatestReviewsByBook(bookIsbn);
        // 사용자 리뷰
        Optional<ReviewResponse> userReview = reviewList.stream()
                .filter(review -> review.getReviewerId().equals(user.getId()))
                .findFirst();

        Map<String, Object> data = new HashMap<>();
        data.put("reviewList", reviewList);
        userReview.ifPresent(review -> data.put("userReview", review));

        return ApiResponse.success(null, data);
    }

    /**
     * GET - 특정 책의 로그인 유저 리뷰 조회
     */
    @GetMapping("/books/{bookIsbn}/me")
    public ResponseEntity<ApiResponse<ReviewResponse>> getUserReview(@PathVariable String bookIsbn,
                                                                     @AuthenticationPrincipal User user) {
        return reviewService.findReviewByUserAndBook(user.getId(), bookIsbn)
                .map(review -> ApiResponse.success(null, review))
                .orElseGet(() -> ApiResponse.success(null));
    }

    /**
     * POST - 리뷰 등록
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(@Valid @RequestBody ReviewRequest review,
                                                                    BindingResult bindingResult) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        ReviewResponse savedReview = reviewService.createReview(review);

        return ApiResponse.success("리뷰가 등록되었습니다.", savedReview);
    }

    /**
     * PUT - 리뷰 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(@PathVariable Long id,
                                                                    @Valid @RequestBody ReviewRequest review,
                                                                    BindingResult bindingResult) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        ReviewResponse savedReview = reviewService.updateReview(id, review);

        return ApiResponse.success("리뷰가 수정되었습니다.", savedReview);
    }

    /**
     * DELETE - 리뷰 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReviewById(id);

        return ApiResponse.success("리뷰가 삭제되었습니다.");
    }

    /**
     * (공통 메서드) 유효성 검사 결과의 우선 순위 정의
     */
    private Map<String, Integer> createValidationPriorityMap() {
        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("bookIsbn", 1);
        priorityMap.put("rating", 2);
        priorityMap.put("reviewText", 3);

        return priorityMap;
    }
}
