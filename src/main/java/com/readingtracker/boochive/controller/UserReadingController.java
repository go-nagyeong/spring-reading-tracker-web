package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.ReadingStatus;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.service.CollectionService;
import com.readingtracker.boochive.service.ReadingListService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserReadingController {

    private final ReadingListService readingListService;
    private final CollectionService collectionService;

    /**
     * 사용자 독서 목록에 추가
     */
    @PostMapping("/reading-list")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addReadingBook(@RequestBody ReadingBook readingBook,
                                                                    @AuthenticationPrincipal User user) {
        // 사용자 ID 세팅
        readingBook.setUserId(user.getId());

        // 컬렉션에 신규로 추가할 경우, 독서 상태 자동 세팅 (기본값: 읽을 예정)
        if (readingBook.getCollectionId() != null && readingBook.getReadingStatus() == null) {
            readingBook.setReadingStatus(ReadingStatus.TO_READ);
        }

        ReadingBook savedReadingBook = readingListService.saveReadingBook(readingBook);

        Map<String, Object> data = new HashMap<>();
        data.put("saveResult", savedReadingBook);

        return ApiResponse.success("독서 목록이 수정되었습니다.", data);
    }

    /**
     * 사용자 독서 목록에서 삭제
     */
    @DeleteMapping("/reading-list/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteReadingBook(@PathVariable Long id) {
        Optional<ReadingBook> existReadingBook =
                readingListService.findReadingBookById(id);

        if (existReadingBook.isPresent()) {
            ReadingBook readingBookToDelete = existReadingBook.get();
            readingListService.deleteReadingBookById(readingBookToDelete.getId());
        }

        return ApiResponse.success("독서 목록에서 삭제되었습니다.");
    }
}
