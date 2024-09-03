package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.*;
import com.readingtracker.boochive.enums.ResourceName;
import com.readingtracker.boochive.exception.ResourceNotFoundException;
import com.readingtracker.boochive.service.ReadingNoteService;
import com.readingtracker.boochive.service.ReadingBookService;
import com.readingtracker.boochive.util.ApiResponse;
import com.readingtracker.boochive.util.FileStorageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/reading-notes")
@RequiredArgsConstructor
@Slf4j
public class ReadingNoteController {

    private final ReadingNoteService readingNoteService;
    private final FileStorageUtil fileStorageUtil;

    private final ReadingBookService readingBookService;

    private final String uploadDir = "images/notes/";

    /**
     * GET - 로그인 유저의 특정 노트 상세 조회
     */
    @GetMapping("/pencil/{id}")
    public ResponseEntity<ApiResponse<PencilNoteResponse>> getPencilNoteDetail(@PathVariable Long id) {
        ResourceName resourceName = ResourceName.fromClassName(ReadingNote.class.getSimpleName());

        return readingNoteService.findPencilNoteById(id)
                .map(note -> ApiResponse.success(null, note))
                .orElseThrow(() -> new ResourceNotFoundException(resourceName.getName()));
    }
    @GetMapping("/highlight/{id}")
    public ResponseEntity<ApiResponse<HighlightNoteResponse>> getHighlightNoteDetail(@PathVariable Long id) {
        ResourceName resourceName = ResourceName.fromClassName(ReadingNote.class.getSimpleName());

        return readingNoteService.findHighlightNoteById(id)
                .map(note -> ApiResponse.success(null, note))
                .orElseThrow(() -> new ResourceNotFoundException(resourceName.getName()));
    }

    /**
     * GET - 로그인 유저의 노트 목록 조회
     */
    @GetMapping("/pencil/me")
    public ResponseEntity<ApiResponse<Page<PencilNoteResponse>>> getPencilNoteList(@AuthenticationPrincipal User user,
                                                                                   @PageableDefault Pageable pageable) {
        Page<PencilNoteResponse> noteList = readingNoteService.getPencilNotesByUserAndNoteType(user.getId(), pageable);

        return ApiResponse.success(null, noteList);
    }
    @GetMapping("/highlight/me")
    public ResponseEntity<ApiResponse<Page<HighlightNoteResponse>>> getHighlightNoteList(@AuthenticationPrincipal User user,
                                                                                         @PageableDefault(value = 12) Pageable pageable) {
        Page<HighlightNoteResponse> noteList = readingNoteService.getHighlightNotesByUserAndNoteType(user.getId(), pageable);

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
     * POST - 이미지 첨부 파일 업로드
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImageFile(@RequestParam(required = false) Long noteId,
                                                               @RequestParam MultipartFile file) throws IOException {

        String directory = uploadDir;
        if (noteId == null) {
            directory += "temp";
        } else {
            directory += noteId;
            fileStorageUtil.clearDirectory(directory); // 업로드 전 기존 이미지 삭제
        }

        String saveUrl = fileStorageUtil.upload(directory, file);

        return ApiResponse.success(null, saveUrl);
    }

    /**
     * POST - [연필 노트] 생성
     */
    @PostMapping("/pencil")
    public ResponseEntity<ApiResponse<PencilNoteResponse>> createPencilNote(@Valid @RequestBody PencilNoteRequest pencilNote,
                                                                            BindingResult bindingResult) {
        handleValidationErrors(bindingResult); // 유효성 검사

        PencilNoteResponse savedNote = readingNoteService.createPencilNote(pencilNote);

        return ApiResponse.success("노트가 작성되었습니다.", savedNote);
    }
    @PostMapping("/highlight")
    public ResponseEntity<ApiResponse<HighlightNoteResponse>> createHighlightNote(@Valid @RequestBody HighlightNoteRequest highlightNote,
                                                                                  BindingResult bindingResult) throws IOException {
        handleValidationErrors(bindingResult); // 유효성 검사

        HighlightNoteResponse savedNote = readingNoteService.createHighlightNote(highlightNote);

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

        PencilNoteResponse savedNote = readingNoteService.updatePencilNote(id, pencilNote);

        return ApiResponse.success("노트가 수정되었습니다.", savedNote);
    }
    @PutMapping("/highlight/{id}")
    public ResponseEntity<ApiResponse<HighlightNoteResponse>> updateHighlightNote(@PathVariable Long id,
                                                                                  @Valid @RequestBody HighlightNoteRequest highlightNote,
                                                                                  BindingResult bindingResult) {
        handleValidationErrors(bindingResult); // 유효성 검사

        HighlightNoteResponse savedNote = readingNoteService.updateHighlightNote(id, highlightNote);

        return ApiResponse.success("노트가 작성되었습니다.", savedNote);
    }

    /**
     * DELETE - [연필 노트] 수정
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePencilNote(@PathVariable Long id) throws IOException {
        readingNoteService.deleteNoteById(id);

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

            List<ObjectError> errors = new ArrayList<>(bindingResult.getAllErrors());
            errors.forEach(error -> {
                log.info("error object name: {}", error.getObjectName());
            });
            errors.sort(Comparator
                    .comparing(error -> priorityMap.getOrDefault(error.getObjectName(), Integer.MAX_VALUE)));

            // throw IllegalArgumentException
            throw new IllegalArgumentException(errors.get(0).getDefaultMessage());
        }
    }
}
