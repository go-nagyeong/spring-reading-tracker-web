package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.service.CollectionService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    /**
     * GET - 사용자 컬렉션 목록 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, List<BookCollection>>>> getUserCollectionList(@AuthenticationPrincipal User user) {
        List<BookCollection> collectionList = collectionService.getCollectionsByUser(user.getId());

        Map<String, List<BookCollection>> data = new HashMap<>();
        data.put("collectionList", collectionList);

        return ApiResponse.success(null, data);
    }


    /**
     * POST - 컬렉션 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookCollection>> createCollection(@RequestBody BookCollection bookCollection,
                                                                        @AuthenticationPrincipal User user) {
        validateCollection(bookCollection);

        bookCollection.updateUser(user); // 사용자 ID 세팅
        BookCollection savedCollection = collectionService.createCollection(bookCollection);;

        return ApiResponse.success("컬렉션이 생성되었습니다.", savedCollection);
    }

    /**
     * PUT - 컬렉션 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookCollection>> updateCollection(@PathVariable Long id,
                                                                        @RequestBody BookCollection bookCollection) {
        validateCollection(bookCollection);

        BookCollection savedCollection = collectionService.updateCollection(id, bookCollection);

        return ApiResponse.success("컬렉션이 수정되었습니다.", savedCollection);
    }

    /**
     * (공통 메서드) 컬렉션 내용 검증
     */
    private void validateCollection(BookCollection bookCollection) {
        if (bookCollection.getCollectionName().isBlank()) {
            throw new IllegalArgumentException("컬렉션 이름을 입력해 주세요.");
        }
    }
}
