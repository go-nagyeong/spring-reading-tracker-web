package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.dto.AladdinAPIResponseDto;
import com.readingtracker.boochive.dto.BatchUpdateDto;
import com.readingtracker.boochive.dto.ReadingBookFilterDto;
import com.readingtracker.boochive.service.BookService;
import com.readingtracker.boochive.service.ReadingBookService;
import com.readingtracker.boochive.util.AladdinOpenAPIHandler;
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
    private final AladdinOpenAPIHandler aladdinOpenAPIHandler;

    /**
     * GET - 사용자 독서 목록 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserReadingBookList(ReadingBookFilterDto filterDto,
                                                                                   @PageableDefault(value = 3) Pageable pageable,
                                                                                   @AuthenticationPrincipal User user) {
        // 사용자의 독서 책 목록 (페이지네이션 적용)
        Page<ReadingBook> pagingBookList = readingBookService
                .getReadingListByUserAndOtherFilter(user.getId(), filterDto, pageable);

        // TODO: 성능 문제 -> 조회할 때 API로 매번 불러오지 말고 책장에 책 추가할 때마다 백그라운드에서 DB에 데이터 저장하는 방식 사용하기
        List<AladdinAPIResponseDto.Item> bookList = new ArrayList<>();
        pagingBookList.forEach(readingBook -> {
            AladdinAPIResponseDto lookupResult = aladdinOpenAPIHandler.lookupBook(readingBook.getBookIsbn());
            bookList.add(lookupResult.getItem().get(0));
        });
        AladdinAPIResponseDto getListResult = new AladdinAPIResponseDto();
        getListResult.setItem(bookList);

        // 페이지네이션 정보 세팅
        setPaginationInfo(getListResult, pagingBookList);

        // 책 관련 통계 (평점, 완독 수, 노트 수)
        for (AladdinAPIResponseDto.Item book : getListResult.getItem()) {
            Map<String, Integer> bookSubInfo = bookService.getUserBookSubInfo(user.getId(), book.getIsbn13());
            book.setUserRating(bookSubInfo.get("userRating"));
            book.setUserReadCount(bookSubInfo.get("userReadCount"));
            book.setUserNoteCount(bookSubInfo.get("userNoteCount"));
        }

        // 사용자의 독서 상태 정보
        Map<String, ReadingBook> readingInfoList = pagingBookList
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
     * (공통 메서드) 페이지네이션 정보 세팅
     */
    private void setPaginationInfo(AladdinAPIResponseDto result, Page<ReadingBook> readingBookPage) {
        result.setStartIndex(readingBookPage.getNumber() + 1);
        result.setItemsPerPage(readingBookPage.getSize());
        result.setTotalResults((int) readingBookPage.getTotalElements());
        result.setTotalPages(readingBookPage.getTotalPages());
    }

    /**
     * (공통 메서드) 독서 목록 업데이트 시 관련 데이터 리로드 (독자 수, 완독 수)
     */
    private void onUpdateReadingBook(Map<String, Object> data, String bookIsbn, Long userId) {
        // 독자 수
        Map<String, Object> bookSubInfo = bookService.getBookSubInfo(bookIsbn);
        Map<String, Integer> userBookSubInfo = bookService.getUserBookSubInfo(userId, bookIsbn);

        data.put("readerCount", bookSubInfo.get("readerCount"));
        data.put("userReadCount", userBookSubInfo.get("userReadCount"));
    }
}
