package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.*;
import com.readingtracker.boochive.enums.ResourceName;
import com.readingtracker.boochive.exception.ResourceNotFoundException;
import com.readingtracker.boochive.service.PencilNoteService;
import com.readingtracker.boochive.service.ReadingBookService;
import com.readingtracker.boochive.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reading-notes")
@RequiredArgsConstructor
public class ReadingNoteController {

    private final PencilNoteService pencilNoteService;

    private final ReadingBookService readingBookService;

    /**
     * GET - 로그인 유저의 특정 [연필 노트] 조회
     */
    @GetMapping("/pencil/{id}")
    public ResponseEntity<ApiResponse<PencilNoteResponse>> getPencilNoteList(@PathVariable Long id,
                                                                             @AuthenticationPrincipal User user) {
        ResourceName resourceName = ResourceName.fromClassName(ReadingNote.class.getSimpleName());

        return pencilNoteService.findNoteById(id)
                .map(note -> ApiResponse.success(null, note))
                .orElseThrow(() -> new ResourceNotFoundException(resourceName.getName()));
    }

    /**
     * GET - 로그인 유저의 [연필 노트] 목록 조회
     */
    @GetMapping("/pencil/me")
    public ResponseEntity<ApiResponse<Page<PencilNoteResponse>>> getPencilNoteList(@AuthenticationPrincipal User user,
                                                                                   Pageable pageable) {
        Page<PencilNoteResponse> noteList = pencilNoteService.getNotesByUserAndNoteType(user.getId(), pageable);

        return ApiResponse.success(null, noteList);
    }

    /**
     * GET - 로그인 유저의 독서 책 목록 조회 (노트 작성에서 책 선택 옵션 세팅)
     */
    @GetMapping("/book-options")
    public ResponseEntity<ApiResponse<Map<String, List<BookParameter>>>> getBookOptionsForNoteCreation(
            @AuthenticationPrincipal User user) {
        Map<String, List<BookParameter>> readingList = readingBookService
                .getBookListGroupByReadingStatus(user.getId());

        return ApiResponse.success(null, readingList);
    }

    /**
     * POST - [연필 노트] 생성
     */
    @PostMapping("/pencil")
    public ResponseEntity<ApiResponse<PencilNoteResponse>> createPencilNote(@Valid @RequestBody PencilNoteRequest pencilNote,
                                                                            BindingResult bindingResult) {
        handleValidationErrors(bindingResult); // 유효성 검사

        PencilNoteResponse savedNote = pencilNoteService.createPencilNote(pencilNote);

        return ApiResponse.success("노트가 작성되었습니다.", savedNote);
    }

    /**
     * PUT - [연필 노트] 수정
     */
    @PutMapping("/pencil/{id}")
    public ResponseEntity<ApiResponse<PencilNoteResponse>> updatePencilNote(@PathVariable Long id,
                                                                            @Valid @RequestBody PencilNoteRequest pencilNote,
                                                                            BindingResult bindingResult) {
        handleValidationErrors(bindingResult); // 유효성 검사

        PencilNoteResponse savedNote = pencilNoteService.updatePencilNote(id, pencilNote);

        return ApiResponse.success("노트가 수정되었습니다.", savedNote);
    }

    /**
     * DELETE - [연필 노트] 수정
     */
    @DeleteMapping("/pencil/{id}")
    public ResponseEntity<ApiResponse<PencilNoteResponse>> deletePencilNote(@PathVariable Long id) {
        pencilNoteService.deletePencilNote(id);

        return ApiResponse.success("노트가 삭제되었습니다.");
    }

    /**
     * (공통 메서드) 유효성 검사 결과 처리
     */
    private void handleValidationErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 유효성 검사 결과 순서 정렬 (원래는 정렬 없이 랜덤으로 나옴)
            Map<String, Integer> priorityMap = new HashMap<>();
            priorityMap.put("noteText", 1);  // 예: name 필드의 오류가 가장 먼저 나오도록
            priorityMap.put("createDate", 2); // 예: email 필드의 오류가 그 다음으로 나오도록
            priorityMap.put("isPublic", 3); // 예: password 필드의 오류가 마지막에 나오도록

            List<FieldError> fieldErrors = new ArrayList<>(bindingResult.getFieldErrors());
            fieldErrors.sort(Comparator
                    .comparing(fieldError -> priorityMap.getOrDefault(fieldError.getField(), Integer.MAX_VALUE)));

            // throw IllegalArgumentException
            throw new IllegalArgumentException(fieldErrors.get(0).getDefaultMessage());
        }
    }
}
