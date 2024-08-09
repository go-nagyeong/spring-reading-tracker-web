package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.Review;
import com.readingtracker.boochive.dto.ReviewDto;
import com.readingtracker.boochive.mapper.ReviewMapper;
import com.readingtracker.boochive.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<ReviewDto> findReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(ReviewMapper.INSTANCE::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ReviewDto> findReviewByUserAndBook(Long userId, String bookIsbn) {
        return reviewRepository.findByUserIdAndBookIsbn(userId, bookIsbn)
                .map(ReviewMapper.INSTANCE::toDto);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsByUser(Long userId) {
        return reviewRepository.findAllByUserId(userId)
                .stream()
                .map(ReviewMapper.INSTANCE::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsByBook(String bookIsbn) {
        return reviewRepository.findAllByBookIsbnOrderByIdDesc(bookIsbn)
                .stream()
                .map(ReviewMapper.INSTANCE::toDto)
                .toList();
    }

    // 책 리뷰 개수 및 평균 평점 계산
    @Transactional(readOnly = true)
    public Map<String, Object> getBookReviewInfo(String bookIsbn) {
        List<Review> reviewList = reviewRepository.findAllByBookIsbnOrderByIdDesc(bookIsbn);

        Map<String, Object> data = new HashMap<>();

        // 리뷰 개수
        data.put("reviewCount", reviewList.size());

        // 평균 평점 = 0을 제외한 평점들의 평균
        double averageRating = reviewList.stream()
                .mapToDouble(Review::getRating)
                .filter(rating -> rating != 0)
                .average()
                .orElse(0);
        data.put("averageRating", BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP));

        return data;
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public ReviewDto createReview(Review review) {
        return ReviewMapper.INSTANCE.toDto(reviewRepository.save(review));
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public ReviewDto updateReview(Long id, Review review) {
        Review existingReview = reviewRepository.findById(id).orElseThrow();

        existingReview.updateReviewRatingAndText(review.getRating(), review.getReviewText());

        return ReviewMapper.INSTANCE.toDto(existingReview);
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }
}
