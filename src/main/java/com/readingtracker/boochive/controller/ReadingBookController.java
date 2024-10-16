package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.dto.common.BatchOperationRequest;
import com.readingtracker.boochive.dto.reading.ReadingBookCondition;
import com.readingtracker.boochive.dto.reading.ReadingBookRequest;
import com.readingtracker.boochive.dto.reading.ReadingBookResponse;
import com.readingtracker.boochive.dto.statistics.BookStatisticsDto;
import com.readingtracker.boochive.dto.statistics.ReadingBookStatisticsDto;
import com.readingtracker.boochive.enums.ResourceName;
import com.readingtracker.boochive.exception.CustomArgumentNotValidException;
import com.readingtracker.boochive.exception.ResourceNotFoundException;
import com.readingtracker.boochive.service.*;
import com.readingtracker.boochive.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reading-books")
@RequiredArgsConstructor
@Slf4j
public class ReadingBookController {

    private final ReadingBookService readingBookService;

    private final BookService bookService;

    /**
     * GET - 사용자 독서 목록 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<ReadingBookResponse>>> getUserReadingBookList(@Valid ReadingBookCondition condition,
                                                                                   BindingResult bindingResult,
                                                                                   @PageableDefault(value = 3) Pageable pageable,
                                                                                   @AuthenticationPrincipal User user) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", new HashMap<>(), bindingResult);
        }

        Page<ReadingBookResponse> readingList = readingBookService
                .getReadingListWithBookDetailByUserAndFilters(user, condition, pageable);

        return ApiResponse.success(null, readingList);
    }

    /**
     * GET - 사용자가 현재 읽고 있는 독서 목록 조회
     */
    @GetMapping("/me/reading")
    public ResponseEntity<ApiResponse<List<ReadingBookResponse>>> getUserIncompleteReadingBookList(@AuthenticationPrincipal User user) {
        List<ReadingBookResponse> readingList = readingBookService
                .getIncompleteReadingListWithBookDetailByUser(user.getId());

        return ApiResponse.success(null, readingList);
    }

    /**
     * POST - 독서 목록에 추가
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> addReadingBook(@Valid @RequestBody ReadingBookRequest readingBook,
                                                                           BindingResult bindingResult,
                                                                           @AuthenticationPrincipal User user) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        ReadingBookResponse savedReadingBook = readingBookService.createReadingBook(readingBook);

        Map<String, Object> data = new HashMap<>();
        data.put("saveResult", savedReadingBook);

        // 독서 목록 업데이트 후 독서 통계 정보 갱신
        onUpdateReadingBook(data, savedReadingBook.getId(), savedReadingBook.getBookIsbn(), user.getId());

        return ApiResponse.success("독서 목록에 추가되었습니다.", data);
    }

    /**
     * PUT - 독서 상태 또는 컬렉션 변경
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateReadingBook(@PathVariable Long id,
                                                                              @Valid @RequestBody ReadingBookRequest readingBook,
                                                                              BindingResult bindingResult,
                                                                              @AuthenticationPrincipal User user) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        ReadingBookResponse savedReadingBook = readingBookService.updateReadingBook(id, readingBook);

        Map<String, Object> data = new HashMap<>();
        data.put("saveResult", savedReadingBook);

        // 독서 목록 업데이트 후 독서 통계 정보 갱신
        onUpdateReadingBook(data, savedReadingBook.getId(), savedReadingBook.getBookIsbn(), user.getId());

        return ApiResponse.success("독서 목록이 수정되었습니다.", data);
    }

    /**
     * DELETE - 독서 목록에서 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteReadingBook(@PathVariable Long id,
                                                                              @AuthenticationPrincipal User user) {
        ResourceName resourceName = ResourceName.fromClassName(ReadingBook.class.getSimpleName());
        ReadingBookResponse existingReadingBook = readingBookService.findReadingBookById(id)
                .orElseThrow(() -> new ResourceNotFoundException(resourceName.getName()));

        readingBookService.deleteReadingBookById(id);

        // 독서 목록 업데이트 후 책 통계 정보 갱신
        Map<String, Object> data = new HashMap<>();
        onUpdateReadingBook(data, null, existingReadingBook.getBookIsbn(), user.getId());

        return ApiResponse.success("독서 목록에서 삭제되었습니다.", data);
    }

    /**
     * POST - 독서 목록에서 Batch Delete (일괄 삭제)
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<Object>> batchDeleteReadingBooks(@RequestBody BatchOperationRequest<?> request) {
        readingBookService.batchDeleteReadingBooks(request);

        return ApiResponse.success("독서 목록에서 삭제되었습니다.");
    }

    /**
     * (공통 메서드) 독서 목록 업데이트 시 통계 데이터 리로드 (독자 수, 완독 횟수)
     */
    private void onUpdateReadingBook(Map<String, Object> data, Long readingBookId, String bookIsbn, Long userId) {
        // 독서 목록 업데이트 시 독서 관련 통계 데이터 리로드 (독자 수, 완독 횟수)
        if (readingBookId != null) {
            ReadingBookStatisticsDto readingStat = readingBookService
                    .getReadingBookStatisticsById(userId, readingBookId);
            data.put("userReadCount", readingStat.getUserReadCount()); // 사용자 완독 횟수
        }
        if (bookIsbn != null) {
            BookStatisticsDto bookState = bookService.getBookStatisticsById(bookIsbn);
            data.put("readerCount", bookState.getReaderCount()); // 책의 독자 수
        }
    }

    /**
     * (공통 메서드) 유효성 검사 결과의 우선 순위 정의
     */
    private Map<String, Integer> createValidationPriorityMap() {
        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("bookIsbn", 1);
        priorityMap.put("collectionId", 2);
        priorityMap.put("readingStatus", 3);

        return priorityMap;
    }
}
