package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.Review;
import com.readingtracker.boochive.dto.ReviewDto;
import com.readingtracker.boochive.mapper.ReviewMapper;
import com.readingtracker.boochive.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
