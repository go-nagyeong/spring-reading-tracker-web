package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.CollectionDetailResponse;
import com.readingtracker.boochive.service.CollectionService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    /**
     * GET - 사용자 컬렉션 목록 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<CollectionDetailResponse>>> getUserCollectionList(@AuthenticationPrincipal User user) {
        List<CollectionDetailResponse> collectionList = collectionService.getCollectionsByUser(user.getId());

        return ApiResponse.success(null, collectionList);
    }

    /**
     * GET - 사용자 컬렉션 목록 조회 (컬렉션 책 목록 포함)
     */
    @GetMapping("/with-books/me")
    public ResponseEntity<ApiResponse<List<CollectionDetailResponse>>> getUserCollectionListWithBooks(@AuthenticationPrincipal User user) {
        List<CollectionDetailResponse> collectionList = collectionService.getCollectionsWithBooksByUser(user.getId());

        return ApiResponse.success(null, collectionList);
    }

    /**
     * POST - 컬렉션 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CollectionDetailResponse>> createCollection(@RequestBody BookCollection bookCollection) {
        validateCollection(bookCollection);

        CollectionDetailResponse savedCollection = collectionService.createCollection(bookCollection);;

        return ApiResponse.success("컬렉션이 생성되었습니다.", savedCollection);
    }

    /**
     * PUT - 컬렉션 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CollectionDetailResponse>> updateCollection(@PathVariable Long id,
                                                                                  @RequestBody BookCollection bookCollection) {
        validateCollection(bookCollection);

        CollectionDetailResponse savedCollection = collectionService.updateCollection(id, bookCollection);

        return ApiResponse.success("컬렉션이 수정되었습니다.", savedCollection);
    }

    /**
     * DELETE - 컬렉션 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCollection(@PathVariable Long id) {
        collectionService.deleteCollectionById(id);

        return ApiResponse.success("컬렉션이 삭제되었습니다.");
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
