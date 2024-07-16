package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.service.CollectionService;
import com.readingtracker.boochive.service.ReadingListService;
import com.readingtracker.boochive.service.ReviewService;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserReadingController {

    private final ReadingListService readingListService;

    /**
     * 사용자 독서 목록에 추가
     */
    @PostMapping("/reading-list")
    public ResponseEntity<ApiResponse<Map<String, ReadingBook>>> addReadingBook(@RequestBody ReadingBook readingBook,
                                                                    @AuthenticationPrincipal User user) {
        // 사용자 ID 세팅
        readingBook.updateUserId(user.getId());

        ReadingBook savedReadingBook = null;
        try {
            savedReadingBook = readingListService.saveReadingBook(readingBook);
        } catch (DataAccessException dae) {
            // 데이터베이스 관련 예외 처리
            return ApiResponse.failure("독서 목록을 수정하는 중 오류가 발생했습니다.");
        } catch (Exception e) {
            // 기타 예외 처리
            return ApiResponse.failure("독서 목록을 수정하는 중 알 수 없는 오류가 발생했습니다.");
        }

        Map<String, ReadingBook> data = new HashMap<>();
        data.put("saveResult", savedReadingBook);

        return ApiResponse.success("독서 목록이 수정되었습니다.", data);
    }

    /**
     * 사용자 독서 목록에서 삭제
     */
    @DeleteMapping("/reading-list/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteReadingBook(@PathVariable Long id) {
        try {
            readingListService.deleteReadingBookById(id);
        } catch (DataAccessException dae) {
            return ApiResponse.failure("독서 목록에서 삭제하는 중 오류가 발생했습니다.");
        } catch (Exception e) {
            return ApiResponse.failure("독서 목록에서 삭제하는 중 알 수 없는 오류가 발생했습니다.");
        }

        return ApiResponse.success("독서 목록에서 삭제되었습니다.");
    }
}
