package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.PurchaseHistory;
import com.readingtracker.boochive.repository.PurchaseHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseHistory> findHistoryById(Long id) {
        return purchaseHistoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<PurchaseHistory> findHistoryByUserAndBook(Long userId, String bookIsbn) {
        return purchaseHistoryRepository.findByUserIdAndBookIsbn(userId, bookIsbn);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public PurchaseHistory createHistory(PurchaseHistory history) {
        return purchaseHistoryRepository.save(history);
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public PurchaseHistory updateHistory(Long id, PurchaseHistory history) {
        PurchaseHistory existingPurchaseHistory = purchaseHistoryRepository.findById(id).orElseThrow();

        existingPurchaseHistory.updateHistory(
                history.getPurchaseDate(),
                history.getPurchaseFrom(),
                history.getPrice(),
                history.getMemo()
        );

        return existingPurchaseHistory;
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteHistoryById(Long id) {
        purchaseHistoryRepository.deleteById(id);
    }
}
