package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.RentalHistoryParameter;
import com.readingtracker.boochive.service.RentalHistoryService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rental-histories")
@RequiredArgsConstructor
public class RentalHistoryController {

    private final RentalHistoryService rentalHistoryService;

    /**
     * GET - 로그인 유저의 책 대여 이력 조회
     */
    @GetMapping("/book/{bookIsbn}/me")
    public ResponseEntity<ApiResponse<RentalHistoryParameter>> getBookRentalHistory(@PathVariable String bookIsbn,
                                                                                    @AuthenticationPrincipal User user) {
        return rentalHistoryService.findHistoryByUserAndBook(user.getId(), bookIsbn)
                .map(history -> ApiResponse.success(null, history))
                .orElseGet(() -> ApiResponse.success(null));
    }

    /**
     * POST - 대여 이력 저장
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createRentalHistory(@RequestBody RentalHistoryParameter history) {
        rentalHistoryService.createHistory(history);

        return ApiResponse.success("대여 이력이 저장되었습니다.");
    }

    /**
     * PUT - 대여 이력 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateRentalHistory(@PathVariable Long id,
                                                                   @RequestBody RentalHistoryParameter history) {
        rentalHistoryService.updateHistory(id, history);

        return ApiResponse.success("대여 이력이 수정되었습니다.");
    }

    /**
     * DELETE - 대여 이력 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteRentalHistory(@PathVariable Long id) {
        rentalHistoryService.deleteHistoryById(id);

        return ApiResponse.success("대여 이력이 삭제되었습니다.");
    }
}
