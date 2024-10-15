package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.purchase.PurchaseHistoryRequest;
import com.readingtracker.boochive.dto.purchase.PurchaseHistoryResponse;
import com.readingtracker.boochive.exception.CustomArgumentNotValidException;
import com.readingtracker.boochive.service.PurchaseHistoryService;
import com.readingtracker.boochive.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-histories")
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;

    /**
     * GET - 로그인 유저의 책 구매 이력 조회
     */
    @GetMapping("/books/{bookIsbn}/me")
    public ResponseEntity<ApiResponse<PurchaseHistoryResponse>> getBookPurchaseHistory(@PathVariable String bookIsbn,
                                                                                       @AuthenticationPrincipal User user) {
        return purchaseHistoryService.findHistoryByUserAndBook(user.getId(), bookIsbn)
                .map(history -> ApiResponse.success(null, history))
                .orElseGet(() -> ApiResponse.success(null));
    }

    /**
     * POST - 구매 이력 저장
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createPurchaseHistory(@Valid @RequestBody PurchaseHistoryRequest history,
                                                                     BindingResult bindingResult) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        purchaseHistoryService.createHistory(history);

        return ApiResponse.success("구매 이력이 저장되었습니다.");
    }

    /**
     * PUT - 구매 이력 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updatePurchaseHistory(@PathVariable Long id,
                                                                     @Valid @RequestBody PurchaseHistoryRequest history,
                                                                     BindingResult bindingResult) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

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

    /**
     * (공통 메서드) 유효성 검사 결과의 우선 순위 정의
     */
    private Map<String, Integer> createValidationPriorityMap() {
        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("purchaseDate", 1);
        priorityMap.put("purchaseFrom", 2);
        priorityMap.put("price", 3);
        priorityMap.put("memo", 4);

        return priorityMap;
    }
}
