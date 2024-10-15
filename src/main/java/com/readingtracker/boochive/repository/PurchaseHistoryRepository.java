package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {

    Optional<PurchaseHistory> findByUserIdAndBookIsbn(Long userId, String bookIsbn);

    List<PurchaseHistory> findAllByUserId(Long userId);
}
