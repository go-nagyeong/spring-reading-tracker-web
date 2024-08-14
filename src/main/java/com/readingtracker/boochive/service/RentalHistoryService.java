package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.RentalHistory;
import com.readingtracker.boochive.dto.RentalHistoryParameter;
import com.readingtracker.boochive.mapper.RentalHistoryMapper;
import com.readingtracker.boochive.repository.RentalHistoryRepository;
import com.readingtracker.boochive.util.ResourceAccessUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentalHistoryService {

    private final RentalHistoryRepository rentalHistoryRepository;
    private final ResourceAccessUtil<RentalHistory> resourceAccessUtil;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<RentalHistoryParameter> findHistoryById(Long id) {
        return rentalHistoryRepository.findById(id)
                .map(RentalHistoryMapper.INSTANCE::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<RentalHistoryParameter> findHistoryByUserAndBook(Long userId, String bookIsbn) {
        return rentalHistoryRepository.findByUserIdAndBookIsbn(userId, bookIsbn)
                .map(RentalHistoryMapper.INSTANCE::toDto);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public RentalHistoryParameter createHistory(RentalHistoryParameter history) {
        RentalHistory newHistory = RentalHistoryMapper.INSTANCE.toEntity(history);

        return RentalHistoryMapper.INSTANCE.toDto(rentalHistoryRepository.save(newHistory));
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public RentalHistoryParameter updateHistory(Long id, RentalHistoryParameter history) {
        RentalHistory existingRentalHistory = resourceAccessUtil.checkAccessAndRetrieve(id);

        existingRentalHistory.updateHistory(
                history.getRentalDate(),
                history.getReturnDate(),
                history.getRentalFrom(),
                history.getMemo()
        );

        return RentalHistoryMapper.INSTANCE.toDto(existingRentalHistory);
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteHistoryById(Long id) {
        RentalHistory existingRentalHistory = resourceAccessUtil.checkAccessAndRetrieve(id);

        rentalHistoryRepository.delete(existingRentalHistory);
    }
}
