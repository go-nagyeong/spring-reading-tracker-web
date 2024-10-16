package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.service.*;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    /**
     * 책 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchBooks(@RequestParam String query,
                                                                        @RequestParam(defaultValue = "1") Integer page,
                                                                        @AuthenticationPrincipal User user) {
        Map<String, Object> data = bookService.searchBooks(query, page, user);

        return ApiResponse.success(null, data);
    }

    /**
     * 특정 책 상세
     */
    @GetMapping("/detail/{isbn}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBookDetails(@PathVariable String isbn,
                                                                           @AuthenticationPrincipal User user) {
        Map<String, Object> data = bookService.getBookDetails(isbn, user);

        return ApiResponse.success(null, data);
    }
}
