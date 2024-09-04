package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.ReadingRecord;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.common.BatchUpdateRequest;
import com.readingtracker.boochive.service.ReadingRecordService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    @GetMapping("/book/{bookIsbn}/me")
    public ResponseEntity<ApiResponse<List<ReadingRecord>>> getReadingRecords(@PathVariable String bookIsbn,
                                                                              @AuthenticationPrincipal User user) {
        List<ReadingRecord> readingRecordList = readingRecordService.getRecordsByUserAndBook(user.getId(), bookIsbn);

        return ApiResponse.success(null, readingRecordList);
    }

    /**
     * GET - 로그인 유저의 / 특정 책 / 아직 읽는 중인 가장 최근 독서 이력
     */
    @GetMapping("/book/{bookIsbn}/me/latest_reading")
    public ResponseEntity<ApiResponse<ReadingRecord>> getLatestReadingRecord(@PathVariable String bookIsbn,
                                                                             @AuthenticationPrincipal User user) {
        return readingRecordService.findLatestReadingRecordByUserAndBook(user.getId(), bookIsbn)
                .map(record -> {
                    record.updateDDay(calculateDDay(record.getStartDate()));
                    return ApiResponse.success(null, record);
                })
                .orElseGet(() -> ApiResponse.success(null));
    }

    /**
     * GET - 로그인 유저의 / 특정 책 / 가장 최근 완독 이력
     */
    @GetMapping("/book/{bookIsbn}/me/latest_read")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLatestCompletedReadingRecord(@PathVariable String bookIsbn,
                                                                                            @AuthenticationPrincipal User user) {
        Map<String, Object> data = new HashMap<>();

        // 최근 완독 이력
        readingRecordService.findLatestReadRecordByUserAndBook(user.getId(), bookIsbn)
                .ifPresent(record -> data.put("latestReadRecord", record));

        // 총 완독 수
        data.put("readCount", readingRecordService.getUserBookReadCount(user.getId(), bookIsbn));

        return ApiResponse.success(null, data);
    }

    /**
     * POST - 독서 이력 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReadingRecord>> createReadingRecord(@RequestBody ReadingRecord readingRecord) {
        ReadingRecord savedRecord = readingRecordService.createReadingRecord(readingRecord);

        return ApiResponse.success("독서 이력이 생성되었습니다.", savedRecord);
    }

    /**
     * PUT - 독서 이력 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReadingRecord>> updateReadingRecord(@PathVariable Long id,
                                                                          @RequestBody ReadingRecord readingRecord) {
        ReadingRecord savedRecord = readingRecordService.updateReadingRecord(id, readingRecord);

        return ApiResponse.success("독서 이력이 수정되었습니다.", savedRecord);
    }

    /**
     * POST - 독서 이력 Batch Update (일괄 수정 및 삭제)
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<Integer>> handleReadingRecords(@RequestBody BatchUpdateRequest<ReadingRecord> request,
                                                                     @AuthenticationPrincipal User user) {
        Integer remainingCompletedReadingRecordCount = readingRecordService.handleReadingRecords(request, user.getId());

        return ApiResponse.success("독서 이력이 수정되었습니다.", remainingCompletedReadingRecordCount);
    }

    /**
     * 디데이 계산 메서드
     */
    private Long calculateDDay(LocalDate date) {
        return ChronoUnit.DAYS.between(date, LocalDate.now()) + 1;
    }
}
