package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.PurchaseHistory;
import com.readingtracker.boochive.dto.purchase.PurchaseHistoryRequest;
import com.readingtracker.boochive.dto.purchase.PurchaseHistoryResponse;
import com.readingtracker.boochive.mapper.PurchaseHistoryMapper;
import com.readingtracker.boochive.repository.PurchaseHistoryRepository;
import com.readingtracker.boochive.util.ResourceAccessUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final ResourceAccessUtil<PurchaseHistory> resourceAccessUtil;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseHistoryResponse> findHistoryByUserAndBook(Long userId, String bookIsbn) {
        return purchaseHistoryRepository.findByUserIdAndBookIsbn(userId, bookIsbn)
                .map(PurchaseHistoryMapper.INSTANCE::toDto);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public PurchaseHistoryResponse createHistory(PurchaseHistoryRequest history) {
        PurchaseHistory newHistory = PurchaseHistoryMapper.INSTANCE.toEntity(history);

        return PurchaseHistoryMapper.INSTANCE.toDto(purchaseHistoryRepository.save(newHistory));
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public PurchaseHistoryResponse updateHistory(Long id, PurchaseHistoryRequest history) {
        PurchaseHistory existingPurchaseHistory = resourceAccessUtil.checkAccessAndRetrieve(id);

        existingPurchaseHistory.updateHistory(
                history.getPurchaseDate(),
                history.getPurchaseFrom(),
                history.getPrice(),
                history.getMemo()
        );

        return PurchaseHistoryMapper.INSTANCE.toDto(existingPurchaseHistory);
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteHistoryById(Long id) {
        PurchaseHistory existingPurchaseHistory = resourceAccessUtil.checkAccessAndRetrieve(id);

        purchaseHistoryRepository.delete(existingPurchaseHistory);
    }
}
