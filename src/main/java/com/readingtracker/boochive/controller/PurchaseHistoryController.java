package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.*;
import com.readingtracker.boochive.service.PurchaseHistoryService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-histories")
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;

    /**
     * GET - 로그인 유저의 책 구매 이력 조회
     */
    @GetMapping("/book/{bookIsbn}/me")
    public ResponseEntity<ApiResponse<PurchaseHistoryParameter>> getBookPurchaseHistory(@PathVariable String bookIsbn,
                                                                                        @AuthenticationPrincipal User user) {
        return purchaseHistoryService.findHistoryByUserAndBook(user.getId(), bookIsbn)
                .map(history -> ApiResponse.success(null, history))
                .orElseGet(() -> ApiResponse.success(null));
    }

    /**
     * POST - 구매 이력 저장
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createPurchaseHistory(@RequestBody PurchaseHistoryParameter history) {
        purchaseHistoryService.createHistory(history);

        return ApiResponse.success("구매 이력이 저장되었습니다.");
    }

    /**
     * PUT - 구매 이력 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updatePurchaseHistory(@PathVariable Long id,
                                                                     @RequestBody PurchaseHistoryParameter history) {
        purchaseHistoryService.updateHistory(id, history);

        return ApiResponse.success("구매 이력이 수정되었습니다.");
    }

    /**
     * DELETE - 구매 이력 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePurchaseHistory(@PathVariable Long id) {
        purchaseHistoryService.deleteHistoryById(id);

        return ApiResponse.success("구매 이력이 삭제되었습니다.");
    }
}
