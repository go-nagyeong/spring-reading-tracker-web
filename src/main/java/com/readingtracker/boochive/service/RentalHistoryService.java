package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.RentalHistory;
import com.readingtracker.boochive.repository.RentalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentalHistoryService {

    private final RentalHistoryRepository rentalHistoryRepository;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<RentalHistory> findHistoryById(Long id) {
        return rentalHistoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<RentalHistory> findHistoryByUserAndBook(Long userId, String bookIsbn) {
        return rentalHistoryRepository.findByUserIdAndBookIsbn(userId, bookIsbn);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public RentalHistory createHistory(RentalHistory history) {
        return rentalHistoryRepository.save(history);
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public RentalHistory updateHistory(Long id, RentalHistory history) {
        RentalHistory existingRentalHistory = rentalHistoryRepository.findById(id).orElseThrow();

        existingRentalHistory.updateHistory(
                history.getRentalDate(),
                history.getReturnDate(),
                history.getRentalFrom(),
                history.getMemo()
        );

        return existingRentalHistory;
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteHistoryById(Long id) {
        rentalHistoryRepository.deleteById(id);
    }
}
