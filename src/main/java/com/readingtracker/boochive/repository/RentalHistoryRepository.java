package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.RentalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Long> {

    Optional<RentalHistory> findByUserIdAndBookIsbn(Long userId, String bookIsbn);
}
