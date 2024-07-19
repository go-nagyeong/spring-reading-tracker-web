package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.PurchaseHistory;
import com.readingtracker.boochive.domain.RentalHistory;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.service.PurchaseHistoryService;
import com.readingtracker.boochive.service.RentalHistoryService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/own-histories")
@RequiredArgsConstructor
@Slf4j
public class BookOwnHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;
    private final RentalHistoryService rentalHistoryService;

    /**
     * GET - 로그인 유저의 책 구매/대여 이력 조회
     */
    @GetMapping("/book/{bookIsbn}/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserOwnHistory(@PathVariable String bookIsbn,
                                                                              @AuthenticationPrincipal User user) {
        Map<String, Object> data = new HashMap<>();

        purchaseHistoryService.findHistoryByUserAndBook(user.getId(), bookIsbn)
                .ifPresent(value -> data.put("purchaseHistory", value));
        rentalHistoryService.findHistoryByUserAndBook(user.getId(), bookIsbn)
                .ifPresent(value -> data.put("rentalHistory", value));

        return ApiResponse.success(null, data);
    }

    /**
     * POST - 구매 이력 저장
     */
    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<Object>> createPurchaseHistory(@RequestBody PurchaseHistory purchaseHistory,
                                                                     @AuthenticationPrincipal User user) {
        purchaseHistory.updateUserId(user.getId()); // 사용자 ID 세팅
        purchaseHistoryService.createHistory(purchaseHistory);

        return ApiResponse.success("구매 이력이 저장되었습니다.");
    }

    /**
     * POST - 대여 이력 저장
     */
    @PostMapping("/rental")
    public ResponseEntity<ApiResponse<Object>> createRentalHistory(@RequestBody RentalHistory rentalHistory,
                                                                   @AuthenticationPrincipal User user) {
        rentalHistory.updateUserId(user.getId()); // 사용자 ID 세팅
        rentalHistoryService.createHistory(rentalHistory);

        return ApiResponse.success("대여 이력이 저장되었습니다.");
    }

    /**
     * PUT - 구매 이력 수정
     */
    @PutMapping("/purchase/{id}")
    public ResponseEntity<ApiResponse<Object>> updatePurchaseHistory(@PathVariable Long id,
                                                                     @RequestBody PurchaseHistory purchaseHistory) {
        purchaseHistoryService.updateHistory(id, purchaseHistory);

        return ApiResponse.success("구매 이력이 수정되었습니다.");
    }

    /**
     * PUT - 대여 이력 수정
     */
    @PutMapping("/rental/{id}")
    public ResponseEntity<ApiResponse<Object>> updateRentalHistory(@PathVariable Long id,
                                                                   @RequestBody RentalHistory rentalHistory) {
        rentalHistoryService.updateHistory(id, rentalHistory);

        return ApiResponse.success("대여 이력이 수정되었습니다.");
    }
}
