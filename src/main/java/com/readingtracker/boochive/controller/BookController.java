package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.dto.AladdinAPIResponseDto;
import com.readingtracker.boochive.util.AladdinOpenAPIHandler;
import com.readingtracker.boochive.util.ApiResponse;
import com.readingtracker.boochive.util.QueryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final AladdinOpenAPIHandler aladdinOpenAPIHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> searchBooks(@RequestParam String query,
                                                           @RequestParam(defaultValue = "1") Integer page) {

        AladdinAPIResponseDto books = aladdinOpenAPIHandler.searchBooks(page, query, QueryType.TITLE);

        // TODO: DB에 있는 책과 합치기 (리뷰 정보)

        return ApiResponse.success("", books);
    }
}
