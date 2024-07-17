package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUserIdAndBookIsbn(Long userId, String bookIsbn);

    List<Review> findAllByUserId(Long userId);

    List<Review> findAllByBookIsbnOrderByIdDesc(String bookIsbn);
}
