package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.service.ReadingBookService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reading-books")
@RequiredArgsConstructor
@Slf4j
public class ReadingBookController {

    private final ReadingBookService readingListService;

    /**
     * POST - 독서 목록에 추가
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReadingBook>> addReadingBook(@RequestBody ReadingBook readingBook,
                                                                   @AuthenticationPrincipal User user) {
        readingBook.updateUser(user); // 사용자 ID 세팅
        ReadingBook savedReadingBook = readingListService.createReadingBook(readingBook);

        return ApiResponse.success("독서 목록에 추가되었습니다.", savedReadingBook);
    }

    /**
     * PUT - 독서 상태 또는 컬렉션 변경
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReadingBook>> updateReadingBook(@PathVariable Long id,
                                                                      @RequestBody ReadingBook readingBook) {
        ReadingBook savedReadingBook = readingListService.updateReadingBook(id, readingBook);

        return ApiResponse.success("독서 목록이 수정되었습니다.", savedReadingBook);
    }

    /**
     * DELETE - 독서 목록에서 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteReadingBook(@PathVariable Long id) {
        readingListService.deleteReadingBookById(id);

        return ApiResponse.success("독서 목록에서 삭제되었습니다.");
    }
}
