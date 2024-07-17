package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.PurchaseHistory;
import com.readingtracker.boochive.domain.RentalHistory;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.OwnHistoryDto;
import com.readingtracker.boochive.service.PurchaseHistoryService;
import com.readingtracker.boochive.service.RentalHistoryService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookOwnHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;
    private final RentalHistoryService rentalHistoryService;

    /**
     * 사용자의 책 구매/대여 이력 가져오기
     */
    @GetMapping("/{bookIsbn}/own-hist/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOwnHistory(@PathVariable String bookIsbn,
                                                                  @AuthenticationPrincipal User user) {
        Map<String, Object> data = new HashMap<>();

        purchaseHistoryService.findHistoryByUserAndBook(user.getId(), bookIsbn)
                .ifPresent(value -> data.put("purchaseHistory", value));
        rentalHistoryService.findHistoryByUserAndBook(user.getId(), bookIsbn)
                .ifPresent(value -> data.put("rentalHistory", value));

        return ApiResponse.success("", data);
    }

    /**
     * 구매/대여 이력 저장
     */
    @PostMapping("/{bookIsbn}/own-hist")
    public ResponseEntity<ApiResponse<Object>> saveOwnHistory(@PathVariable String bookIsbn,
                                                              @RequestBody OwnHistoryDto ownHistoryDto,
                                                              @AuthenticationPrincipal User user) {
        PurchaseHistory purchaseHistory = ownHistoryDto.getPurchase();
        RentalHistory rentalHistory = ownHistoryDto.getRental();

        // 사용자 ID 세팅
        purchaseHistory.updateUserId(user.getId());
        rentalHistory.updateUserId(user.getId());

        try {
            purchaseHistoryService.saveHistory(purchaseHistory);
            rentalHistoryService.saveHistory(rentalHistory);
        } catch (DataAccessException dae) {
            // 데이터베이스 관련 예외 처리
            return ApiResponse.failure("이력을 저장하는 중 오류가 발생했습니다.");
        } catch (Exception e) {
            // 기타 예외 처리
            return ApiResponse.failure("이력을 저장하는 중 알 수 없는 오류가 발생했습니다.");
        }

        return ApiResponse.success("구매/대여 이력이 저장되었습니다.");
    }
}
