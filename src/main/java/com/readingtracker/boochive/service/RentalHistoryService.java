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
     * [C]R[U]D - CREATE/UPDATE
     */
    @Transactional
    public RentalHistory saveHistory(RentalHistory history) {
        if (history.getId() != null) { // update
            RentalHistory existingRentalHistory = rentalHistoryRepository.findById(history.getId()).orElseThrow();

            existingRentalHistory.updateHistory(
                    history.getRentalDate(),
                    history.getReturnDate(),
                    history.getRentalFrom(),
                    history.getMemo()
            );

            return existingRentalHistory;
        }
        return rentalHistoryRepository.save(history); // insert
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteHistoryById(Long id) {
        rentalHistoryRepository.deleteById(id);
    }
}
