package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.common.BatchOperationRequest;
import com.readingtracker.boochive.dto.record.ReadingRecordRequest;
import com.readingtracker.boochive.dto.record.ReadingRecordResponse;
import com.readingtracker.boochive.exception.CustomArgumentNotValidException;
import com.readingtracker.boochive.service.ReadingRecordService;
import com.readingtracker.boochive.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reading-records")
@RequiredArgsConstructor
public class ReadingRecordController {

    private final ReadingRecordService readingRecordService;

    /**
     * GET - 로그인 유저의 / 특정 책 / 전체 독서 이력
     */
    @GetMapping("/books/{readingId}/me")
    public ResponseEntity<ApiResponse<List<ReadingRecordResponse>>> getReadingRecords(@PathVariable Long readingId,
                                                                                      @AuthenticationPrincipal User user
    ) {
        List<ReadingRecordResponse> readingRecordList = readingRecordService
                .getRecordsByUserAndBook(user.getId(), readingId);

        return ApiResponse.success(null, readingRecordList);
    }

    /**
     * GET - 로그인 유저의 / 특정 책 / 아직 읽는 중인 가장 최근 독서 이력
     */
    @GetMapping("/books/{readingId}/me/latest")
    public ResponseEntity<ApiResponse<ReadingRecordResponse>> getLatestReadingRecord(@PathVariable Long readingId,
                                                                                     @AuthenticationPrincipal User user
    ) {
        return readingRecordService.findLatestReadingRecordByUserAndBook(user.getId(), readingId)
                .map(record -> ApiResponse.success(null, record))
                .orElseGet(() -> ApiResponse.success(null));
    }

    /**
     * GET - 로그인 유저의 / 특정 책 / 가장 최근 완독 이력
     */
    @GetMapping("/books/{readingId}/me/latest/completed")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLatestCompletedReadingRecord(@PathVariable Long readingId,
                                                                                            @AuthenticationPrincipal User user
    ) {
        Map<String, Object> data = new HashMap<>();

        // 최근 완독 이력
        readingRecordService.findLatestReadRecordByUserAndBook(user.getId(), readingId)
                .ifPresent(record -> data.put("latestReadRecord", record));

        // 총 완독 수
        data.put("readCount", readingRecordService.getUserBookReadCount(user.getId(), readingId));

        return ApiResponse.success(null, data);
    }

    /**
     * POST - 독서 이력 Batch Update (일괄 수정 및 삭제)
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<Integer>> handleReadingRecords(
            @Valid @RequestBody BatchOperationRequest<ReadingRecordRequest> request,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user
    ) throws CustomArgumentNotValidException {

        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        Integer remainingCompletedReadingRecordCount = readingRecordService.handleReadingRecords(request, user.getId());

        return ApiResponse.success("독서 이력이 수정되었습니다.", remainingCompletedReadingRecordCount);
    }

    /**
     * PUT - 독서 이력 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReadingRecordResponse>> updateReadingRecord(
            @PathVariable Long id,
            @Valid @RequestBody ReadingRecordRequest readingRecord,
            BindingResult bindingResult
    ) throws CustomArgumentNotValidException {

        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        ReadingRecordResponse savedRecord = readingRecordService.updateReadingRecord(id, readingRecord);

        return ApiResponse.success("독서 이력이 수정되었습니다.", savedRecord);
    }

    /**
     * (공통 메서드) 유효성 검사 결과의 우선 순위 정의
     */
    private Map<String, Integer> createValidationPriorityMap() {
        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("readingBookId", 1);
        priorityMap.put("startDate", 2);

        return priorityMap;
    }
}
