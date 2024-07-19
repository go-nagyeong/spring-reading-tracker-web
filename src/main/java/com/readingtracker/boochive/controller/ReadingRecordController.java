package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.ReadingRecord;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.BatchUpdateDto;
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
     * GET - 로그인 유저의 / 특정 책 / 완독 이력
     */
    @GetMapping("/book/{bookIsbn}/me/completed")
    public ResponseEntity<ApiResponse<Map<String, List<ReadingRecord>>>> getCompletedReadingRecords(@PathVariable String bookIsbn,
                                                                                                    @AuthenticationPrincipal User user) {
        List<ReadingRecord> completedReadingRecordList =
                readingRecordService.getCompletedReadingRecordsByUserAndBook(user.getId(), bookIsbn);

        Map<String, List<ReadingRecord>> data = new HashMap<>();
        data.put("completedReadingRecordList", completedReadingRecordList);

        return ApiResponse.success(null, data);
    }

    /**
     * GET - 로그인 유저의 / 특정 책 / 아직 읽는 중인 가장 최근 독서 이력
     */
    @GetMapping("/book/{bookIsbn}/me/latest")
    public ResponseEntity<ApiResponse<Map<String, ReadingRecord>>> getLatestReadingRecord(@PathVariable String bookIsbn,
                                                                                          @AuthenticationPrincipal User user) {
        Map<String, ReadingRecord> data = new HashMap<>();

        readingRecordService.findLatestReadingRecordByUserAndBook(user.getId(), bookIsbn)
                .ifPresent(record -> {
                    record.updateDDay(calculateDDay(record.getStartDate()));
                    data.put("latestReadingRecord", record);
                });

        return ApiResponse.success(null, data);
    }

    /**
     * POST - 독서 이력 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReadingRecord>> createReadingRecord(@RequestBody ReadingRecord readingRecord,
                                                                          @AuthenticationPrincipal User user) {
        readingRecord.updateUserId(user.getId()); // 사용자 ID 세팅
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
     * POST - 독서 이력 Batch Update (여러 데이터를 한 번에 수정 및 삭제)
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<ReadingRecord>>> handleReadingRecords(@RequestBody BatchUpdateDto<ReadingRecord> batchUpdateDto,
                                                                                 @AuthenticationPrincipal User user) {
        List<ReadingRecord> remainingReadingRecords = readingRecordService.handleReadingRecords(batchUpdateDto, user.getId());

        return ApiResponse.success("독서 이력이 수정되었습니다.", remainingReadingRecords);
    }

    /**
     * 디데이 계산 메서드
     */
    private Long calculateDDay(LocalDate date) {
        return ChronoUnit.DAYS.between(date, LocalDate.now()) + 1;
    }
}