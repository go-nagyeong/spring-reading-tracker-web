package com.readingtracker.boochive;

import com.readingtracker.boochive.domain.Review;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.service.ReviewService;
import com.readingtracker.boochive.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
public class ReviewCRUDTest {

    @Autowired
    private ReviewService service;
    @Autowired
    private UserService userService;

    @Test
    void createReview() {
        /* given */
        User user = userService.findUserById(1L).get();

        Review review = Review.builder()
                .user(user)
                .bookIsbn("test")
                .rating(0)
                .reviewText("test")
                .build();

        Review savedReview = service.createReview(review);

        /* when, then */
        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getBookIsbn()).isEqualTo("test");
        assertThat(savedReview.getRating()).isEqualTo(0);
        assertThat(savedReview.getReviewText()).isEqualTo("test");
    }

    @Test
    void updateReview() {
        /* given */
        User user = userService.findUserById(1L).get();

        Review review = Review.builder()
                .user(user)
                .bookIsbn("test")
                .rating(0)
                .reviewText("test")
                .build();

        Review createdReview = service.createReview(review);

        Review newReview = Review.builder()
                .id(createdReview.getId())
                .build();
        newReview.updateReviewRatingAndText(0, "test2");
        service.updateReview(createdReview.getId(), newReview);

        /* when, then */
        assertThat(newReview).isNotNull();
        assertThat(newReview.getBookIsbn()).isEqualTo("test");
        assertThat(newReview.getRating()).isEqualTo(0);
        assertThat(newReview.getReviewText()).isEqualTo("test2");
    }
}
