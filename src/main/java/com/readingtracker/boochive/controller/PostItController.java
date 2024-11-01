package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.common.BatchOperationRequest;
import com.readingtracker.boochive.dto.note.BookWithPostItsResponse;
import com.readingtracker.boochive.dto.note.PostItRequest;
import com.readingtracker.boochive.exception.CustomArgumentNotValidException;
import com.readingtracker.boochive.service.PostItService;
import com.readingtracker.boochive.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reading-notes/post-it")
@RequiredArgsConstructor
public class PostItController {

    private final PostItService postItService;

    // 한 페이지에 표시할 아이템 수 (NOTE: 반응형 그리드 셀 개수 3,5,6의 공배수)
    private final static int POST_IT_BOOK_SIZE_PER_PAGE = 30;

    /**
     * GET - 로그인 유저의 포스트잇 책 목록 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPostItBookList(
            @AuthenticationPrincipal User user,
            @PageableDefault(value = POST_IT_BOOK_SIZE_PER_PAGE) Pageable pageable
    ) {
        Page<BookWithPostItsResponse> bookList = postItService.findBooksWithPostItsByUser(user.getId(), pageable);
        Long postItCount = postItService.getUserPostItCount(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("bookList", bookList);
        data.put("postItCount", postItCount);

        return ApiResponse.success(null, data);
    }

    /**
     * GET - 로그인 유저의 특정 책의 포스트잇 목록 조회
     */
    @GetMapping("/books/{readingId}/me")
    public ResponseEntity<ApiResponse<BookWithPostItsResponse>> getPostItBookDetail(@PathVariable Long readingId) {

        return ApiResponse.success(null, postItService.getBookWithPostIts(readingId));
    }

    /**
     * POST - 포스트잇 Batch Create/Update/Delete (일괄 생성, 수정, 삭제)
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<Object>> handlePostIts(
            @Valid @RequestBody BatchOperationRequest<PostItRequest> request,
            BindingResult bindingResult
    ) throws CustomArgumentNotValidException {

        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        postItService.handlePostIts(request);

        return ApiResponse.success("포스트잇 목록이 저장되었습니다.");
    }

    /**
     * (공통 메서드) 유효성 검사 결과의 우선 순위 정의
     */
    private Map<String, Integer> createValidationPriorityMap() {
        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("readingBookId", 1);
        priorityMap.put("backgroundColor", 2);
        priorityMap.put("noteText", 3);
        priorityMap.put("pageNumber", 4);

        return priorityMap;
    }
}
