package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.Review;
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
    public Optional<Review> findReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Review> findReviewByUserAndBook(Long userId, String bookIsbn) {
        return reviewRepository.findByUserIdAndBookIsbn(userId, bookIsbn);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByBook(String bookIsbn) {
        return reviewRepository.findAllByBookIsbn(bookIsbn);
    }

    /**
     * [C]R[U]D - CREATE/UPDATE
     */
    @Transactional
    public Review saveReview(Review review) {
        if (review.getId() != null) { // update
            Review existingReview = reviewRepository.findById(review.getId()).orElseThrow();

            existingReview.updateReviewRatingAndText(review.getRating(), review.getReviewText());

            return existingReview;
        }
        return reviewRepository.save(review); // insert
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }
}
