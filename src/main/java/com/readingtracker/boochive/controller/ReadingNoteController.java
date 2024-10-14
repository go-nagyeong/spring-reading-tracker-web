package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.book.BookDto;
import com.readingtracker.boochive.enums.ResourceName;
import com.readingtracker.boochive.exception.CustomArgumentNotValidException;
import com.readingtracker.boochive.exception.ResourceNotFoundException;
import com.readingtracker.boochive.dto.note.HighlightNoteRequest;
import com.readingtracker.boochive.dto.note.HighlightNoteResponse;
import com.readingtracker.boochive.dto.note.PencilNoteRequest;
import com.readingtracker.boochive.dto.note.PencilNoteResponse;
import com.readingtracker.boochive.service.ReadingNoteService;
import com.readingtracker.boochive.service.ReadingBookService;
import com.readingtracker.boochive.util.ApiResponse;
import com.readingtracker.boochive.util.FileStorageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/reading-notes")
@RequiredArgsConstructor
public class ReadingNoteController {

    private final ReadingNoteService readingNoteService;
    private final FileStorageUtil fileStorageUtil;

    private final ReadingBookService readingBookService;

    private final String uploadDir = "images/notes/";

    // 한 페이지에 표시할 아이템 수
    private final static int HIGHLIGHT_NOTE_SIZE_PER_PAGE = 12; // 반응형 그리드 셀 개수 2,3,4의 공배수

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
    public ResponseEntity<ApiResponse<Page<HighlightNoteResponse>>> getHighlightNoteList(
            @AuthenticationPrincipal User user,
            @PageableDefault(value = HIGHLIGHT_NOTE_SIZE_PER_PAGE) Pageable pageable
    ) {
        Page<HighlightNoteResponse> noteList = readingNoteService.getHighlightNotesByUserAndNoteType(user.getId(), pageable);

        return ApiResponse.success(null, noteList);
    }

    /**
     * GET - 로그인 유저의 독서 책 목록 조회 (노트 작성에서 책 선택 옵션 세팅)
     */
    @GetMapping("/book-options")
    public ResponseEntity<ApiResponse<Map<String, List<BookDto>>>> getBookOptionsForNoteCreation(@AuthenticationPrincipal User user) {
        Map<String, List<BookDto>> readingList = readingBookService
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
     * POST - 노트 신규 생성
     */
    @PostMapping("/pencil")
    public ResponseEntity<ApiResponse<PencilNoteResponse>> createPencilNote(@Valid @RequestBody PencilNoteRequest pencilNote,
                                                                            BindingResult bindingResult) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        PencilNoteResponse savedNote = readingNoteService.createPencilNote(pencilNote);

        return ApiResponse.success("노트가 작성되었습니다.", savedNote);
    }

    @PostMapping("/highlight")
    public ResponseEntity<ApiResponse<HighlightNoteResponse>> createHighlightNote(@Valid @RequestBody HighlightNoteRequest highlightNote,
                                                                                  BindingResult bindingResult) throws IOException, CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        HighlightNoteResponse savedNote = readingNoteService.createHighlightNote(highlightNote);

        return ApiResponse.success("노트가 작성되었습니다.", savedNote);
    }

    /**
     * PUT - 노트 수정
     */
    @PutMapping("/pencil/{id}")
    public ResponseEntity<ApiResponse<PencilNoteResponse>> updatePencilNote(@PathVariable Long id,
                                                                            @Valid @RequestBody PencilNoteRequest pencilNote,
                                                                            BindingResult bindingResult) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        PencilNoteResponse savedNote = readingNoteService.updatePencilNote(id, pencilNote);

        return ApiResponse.success("노트가 수정되었습니다.", savedNote);
    }

    @PutMapping("/highlight/{id}")
    public ResponseEntity<ApiResponse<HighlightNoteResponse>> updateHighlightNote(@PathVariable Long id,
                                                                                  @Valid @RequestBody HighlightNoteRequest highlightNote,
                                                                                  BindingResult bindingResult) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", createValidationPriorityMap(), bindingResult);
        }

        HighlightNoteResponse savedNote = readingNoteService.updateHighlightNote(id, highlightNote);

        return ApiResponse.success("노트가 수정되었습니다.", savedNote);
    }

    /**
     * DELETE - 노트 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteNote(@PathVariable Long id) throws IOException {
        readingNoteService.deleteNoteById(id);

        return ApiResponse.success("노트가 삭제되었습니다.");
    }

    /**
     * (공통 메서드) 유효성 검사 결과의 우선 순위 정의
     */
    private Map<String, Integer> createValidationPriorityMap() {
        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("noteText", 1);
        priorityMap.put("AtLeastOneNotBlank", 1);
        priorityMap.put("createDate", 2);
        priorityMap.put("isPublic", 3);

        return priorityMap;
    }
}
