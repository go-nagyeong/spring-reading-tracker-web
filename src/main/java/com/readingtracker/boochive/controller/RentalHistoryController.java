package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.RentalHistoryRequest;
import com.readingtracker.boochive.dto.RentalHistoryResponse;
import com.readingtracker.boochive.exception.CustomArgumentNotValidException;
import com.readingtracker.boochive.service.RentalHistoryService;
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
@RequestMapping("/api/rental-histories")
@RequiredArgsConstructor
public class RentalHistoryController {

    private final RentalHistoryService rentalHistoryService;

    /**
     * GET - 로그인 유저의 책 대여 이력 조회
     */
    @GetMapping("/book/{bookIsbn}/me")
    public ResponseEntity<ApiResponse<RentalHistoryResponse>> getBookRentalHistory(@PathVariable String bookIsbn,
                                                                                   @AuthenticationPrincipal User user) {
        return rentalHistoryService.findHistoryByUserAndBook(user.getId(), bookIsbn)
                .map(history -> ApiResponse.success(null, history))
                .orElseGet(() -> ApiResponse.success(null));
    }

    /**
     * POST - 대여 이력 저장
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createRentalHistory(@Valid @RequestBody RentalHistoryRequest history,
                                                                   BindingResult bindingResult) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        rentalHistoryService.createHistory(history);

        return ApiResponse.success("대여 이력이 저장되었습니다.");
    }

    /**
     * PUT - 대여 이력 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateRentalHistory(@PathVariable Long id,
                                                                   @Valid @RequestBody RentalHistoryRequest history,
                                                                   BindingResult bindingResult) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

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

    /**
     * (공통 메서드) 유효성 검사 결과의 우선 순위 정의
     */
    private Map<String, Integer> createValidationPriorityMap() {
        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("rentalDate", 1);
        priorityMap.put("returnDate", 2);
        priorityMap.put("rentalFrom", 3);
        priorityMap.put("memo", 4);

        return priorityMap;
    }
}
