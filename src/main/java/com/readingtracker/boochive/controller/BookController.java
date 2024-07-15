package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.AladdinAPIResponseDto;
import com.readingtracker.boochive.service.CollectionService;
import com.readingtracker.boochive.service.ReadingListService;
import com.readingtracker.boochive.util.AladdinOpenAPIHandler;
import com.readingtracker.boochive.util.ApiResponse;
import com.readingtracker.boochive.util.QueryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final AladdinOpenAPIHandler aladdinOpenAPIHandler;
    private final ReadingListService readingListService;
    private final CollectionService collectionService;

    /**
     * 책 검색
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") Integer page,
            @AuthenticationPrincipal User user
    ) {
        // 책 검색 결과
        AladdinAPIResponseDto searchResult = aladdinOpenAPIHandler.searchBooks(page, query, QueryType.TITLE);

        // 사용자의 컬렉션 목록
        List<BookCollection> collectionList = collectionService.getCollectionsByUser(user.getId());

        // 사용자의 독서 상태 및 컬렉션 정보
        List<ReadingBook> readingList = readingListService.getReadingListByUser(user.getId());
        Map<String, ReadingBook> readingInfoList = new HashMap<>();
        for (ReadingBook item : readingList) { // entity to map
            readingInfoList.put(item.getBookIsbn(), item);
        }

        // TODO: DB에 있는 책과 합치기 (리뷰 정보)

        Map<String, Object> data = new HashMap<>();
        data.put("searchResult", searchResult);
        data.put("collectionList", collectionList);
        data.put("readingInfoList", readingInfoList);

        return ApiResponse.success("", data);
    }

}
