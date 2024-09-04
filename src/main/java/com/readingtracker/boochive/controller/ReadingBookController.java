package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.dto.*;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reading-books")
@RequiredArgsConstructor
@Slf4j
public class ReadingBookController {

    private final ReadingBookService readingBookService;
    private final ReviewService reviewService;
    private final ReadingRecordService readingRecordService;

    /**
     * GET - 사용자 독서 목록 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserReadingBookList(@Valid ReadingBookCondition condition,
                                                                                   BindingResult bindingResult,
                                                                                   @PageableDefault(value = 3) Pageable pageable,
                                                                                   @AuthenticationPrincipal User user) throws CustomArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new CustomArgumentNotValidException("single", new HashMap<>(), bindingResult);
        }

        // 사용자의 독서 책 목록
        Page<ReadingBookResponse> readingList = readingBookService
                .getReadingListWithBookDetailByUserAndFilters(user, condition, pageable);

        // 데이터 전처리 (Page<ReadingBookDetailResponse> -> PageableBookListResponse)
        PageableBookListResponse getListResult = createPageableBookList(readingList, user.getId());

        // 사용자의 독서 상태 정보
        Map<String, ReadingBookResponse> readingInfoList = readingList.stream()
                .collect(Collectors.toMap(ReadingBookResponse::getBookIsbn, readingBook -> readingBook));

        Map<String, Object> data = new HashMap<>();
        data.put("getListResult", getListResult);
        data.put("readingInfoList", readingInfoList);

        return ApiResponse.success(null, data);
    }

    /**
     * GET - 사용자가 현재 읽고 있는 독서 목록 조회
     */
    @GetMapping("/me/reading")
    public ResponseEntity<ApiResponse<List<ReadingBookResponse>>> getUserIncompleteReadingBookList(@AuthenticationPrincipal User user) {
        // 사용자의 독서 책 목록
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

        onUpdateReadingBook(data, savedReadingBook.getBookIsbn(), user.getId());

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

        onUpdateReadingBook(data, savedReadingBook.getBookIsbn(), user.getId());

        return ApiResponse.success("독서 목록이 수정되었습니다.", data);
    }

    /**
     * DELETE - 독서 목록에서 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteReadingBook(@PathVariable Long id,
                                                                              @AuthenticationPrincipal User user) {
        ResourceName resourceName = ResourceName.fromClassName(ReadingBook.class.getSimpleName());
        ReadingBookResponse existing = readingBookService.findReadingBookById(id)
                .orElseThrow(() -> new ResourceNotFoundException(resourceName.getName()));
        String bookIsbn = existing.getBookIsbn();

        readingBookService.deleteReadingBookById(id);

        Map<String, Object> data = new HashMap<>();
        onUpdateReadingBook(data, bookIsbn, user.getId());

        return ApiResponse.success("독서 목록에서 삭제되었습니다.", data);
    }

    /**
     * POST - 독서 목록에서 Batch Delete (일괄 삭제)
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<Object>> batchDeleteReadingBooks(@RequestBody BatchUpdateRequest<ReadingBook> request) {
        readingBookService.batchDeleteReadingBooks(request);

        return ApiResponse.success("독서 목록에서 삭제되었습니다.");
    }

    /**
     * (공통 메서드) 책 목록 데이터 전처리 > DTO 변환
     */
    private PageableBookListResponse createPageableBookList(Page<ReadingBookResponse> readingList, Long userId) {
        PageableBookListResponse pageableBookList = new PageableBookListResponse();

        // 페이지네이션 정보 세팅
        pageableBookList.setStartIndex(readingList.getNumber() + 1); // 현재 페이지
        pageableBookList.setItemsPerPage(readingList.getSize()); // 페이지당 책 개수
        pageableBookList.setTotalResults((int) readingList.getTotalElements()); // 전체 책 개수
        pageableBookList.setTotalPages(readingList.getTotalPages()); // 전체 페이지 수

        // 책 목록 세팅
        List<BookDto> bookList = readingList.stream()
                .map(readingBook -> {
                    BookDto book = readingBook.getBookInfo();
                    setReadingBookStatistics(book, userId); // 책 통계 정보 세팅
                    return book;
                })
                .toList();
        pageableBookList.setItem(bookList);

        return pageableBookList;
    }

    /**
     * (공통 메서드) 책 통계 정보 세팅 - 리뷰 개수, 평균 평점, 독자 수
     */
    private void setReadingBookStatistics(BookDto book, Long userId) {
        BookDto.SubInfo subInfo = book.getSubInfo();

        reviewService.findReviewByUserAndBook(userId, book.getIsbn13())
                .ifPresent(review -> subInfo.setUserRating(review.getRating()));
        subInfo.setUserReadCount(readingRecordService.getUserBookReadCount(userId, book.getIsbn13()));
        subInfo.setUserNoteCount(0); // TODO: 노트 수 추가 로직
    }

    /**
     * (공통 메서드) 독서 목록 업데이트 시 관련 데이터 리로드 (독자 수, 완독 수)
     */
    private void onUpdateReadingBook(Map<String, Object> data, String bookIsbn, Long userId) {
        data.put("readerCount", readingBookService.getBookReaderCount(bookIsbn)); // 책의 독자 수
        data.put("userReadCount", readingRecordService.getUserBookReadCount(userId, bookIsbn)); // 유저의 완독 수
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
