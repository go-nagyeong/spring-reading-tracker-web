package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.dto.BookDto;
import com.readingtracker.boochive.dto.PageableBookListDto;
import com.readingtracker.boochive.dto.BatchUpdateDto;
import com.readingtracker.boochive.dto.ReadingBookFilterDto;
import com.readingtracker.boochive.mapper.BookMapper;
import com.readingtracker.boochive.service.BookService;
import com.readingtracker.boochive.service.ReadingBookService;
import com.readingtracker.boochive.service.ReadingRecordService;
import com.readingtracker.boochive.service.ReviewService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reading-books")
@RequiredArgsConstructor
@Slf4j
public class ReadingBookController {

    private final ReadingBookService readingBookService;
    private final BookService bookService;
    private final ReviewService reviewService;
    private final ReadingRecordService readingRecordService;

    /**
     * GET - 사용자 독서 목록 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserReadingBookList(ReadingBookFilterDto filterDto,
                                                                                   @PageableDefault(value = 3) Pageable pageable,
                                                                                   @AuthenticationPrincipal User user) {
        // 사용자의 독서 책 목록 (페이지네이션 적용)
        Page<ReadingBook> readingList = readingBookService
                .getReadingListByUserAndOtherFilter(user.getId(), filterDto, pageable);

        // 첵 목록 데이터 전처리
        PageableBookListDto getListResult = createPageableBookListDto(readingList, user.getId());

        // 사용자의 독서 상태 정보
        Map<String, ReadingBook> readingInfoList = readingList
                .stream()
                .collect(Collectors.toMap(ReadingBook::getBookIsbn, readingBook -> readingBook));

        Map<String, Object> data = new HashMap<>();
        data.put("getListResult", getListResult);
        data.put("readingInfoList", readingInfoList);

        return ApiResponse.success(null, data);
    }

    /**
     * POST - 독서 목록에 추가
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> addReadingBook(@RequestBody ReadingBook readingBook,
                                                                           @AuthenticationPrincipal User user) {
        readingBook.updateUser(user); // 사용자 ID 세팅
        ReadingBook savedReadingBook = readingBookService.createReadingBook(readingBook);

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
                                                                              @RequestBody ReadingBook readingBook,
                                                                              @AuthenticationPrincipal User user) {
        ReadingBook savedReadingBook = readingBookService.updateReadingBook(id, readingBook);

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
        ReadingBook existing = readingBookService.findReadingBookById(id).orElseThrow();
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
    public ResponseEntity<ApiResponse<Object>> batchDeleteReadingBooks(@RequestBody BatchUpdateDto<Long> batchUpdateDto) {
        readingBookService.batchDeleteReadingBooks(batchUpdateDto);

        return ApiResponse.success("독서 목록에서 삭제되었습니다.");
    }

    /**
     * (공통 메서드) 책 목록 데이터 전처리 > 페이지네이션 정보 세팅 및 DTO 변환
     */
    private PageableBookListDto createPageableBookListDto(Page<ReadingBook> readingList, Long userId) {
        // DTO 변환
        List<BookDto> bookList = readingList.stream()
                .map(readingBook -> bookService.findBookByIsbn(readingBook.getBookIsbn())
                        .map(BookMapper.INSTANCE::toDto)
                        .orElse(null))
                .filter(Objects::nonNull)
                .peek(book -> setReadingBookStatistics(book, userId)) // 책 통계 정보 세팅
                .toList();

        PageableBookListDto bookListDto = new PageableBookListDto();
        bookListDto.setItem(bookList);

        // 페이지네이션 정보 세팅
        setPaginationInfo(bookListDto, readingList);

        return bookListDto;
    }

    /**
     * (공통 메서드) 페이지네이션 정보 세팅
     */
    private void setPaginationInfo(PageableBookListDto result, Page<ReadingBook> readingBookPage) {
        result.setStartIndex(readingBookPage.getNumber() + 1); // 현재 페이지
        result.setItemsPerPage(readingBookPage.getSize()); // 페이지당 책 개수
        result.setTotalResults((int) readingBookPage.getTotalElements()); // 전체 책 개수
        result.setTotalPages(readingBookPage.getTotalPages()); // 전체 페이지 수
    }

    /**
     * (공통 메서드) 책 통계 정보 세팅 - 리뷰 개수, 평균 평점, 독자 수
     */
    private void setReadingBookStatistics(BookDto book, Long userId) {
        reviewService.findReviewByUserAndBook(userId, book.getIsbn13())
                .ifPresent(review -> book.setUserRating(review.getRating()));
        book.setUserReadCount(readingRecordService.getUserBookReadCount(userId, book.getIsbn13()));
        book.setUserNoteCount(0); // TODO: 노트 수 추가 로직
    }

    /**
     * (공통 메서드) 독서 목록 업데이트 시 관련 데이터 리로드 (독자 수, 완독 수)
     */
    private void onUpdateReadingBook(Map<String, Object> data, String bookIsbn, Long userId) {
        data.put("readerCount", readingBookService.getBookReaderCount(bookIsbn)); // 책의 독자 수
        data.put("userReadCount", readingRecordService.getUserBookReadCount(userId, bookIsbn)); // 유저의 완독 수
    }
}
