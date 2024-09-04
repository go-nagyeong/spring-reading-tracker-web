package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.CollectionResponse;
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
    public ResponseEntity<ApiResponse<List<CollectionResponse>>> getUserCollectionList(@AuthenticationPrincipal User user) {
        List<CollectionResponse> collectionList = collectionService.getCollectionsByUser(user.getId());

        return ApiResponse.success(null, collectionList);
    }

    /**
     * GET - 사용자 컬렉션 목록 조회 (컬렉션 책 목록 포함)
     */
    @GetMapping("/with-books/me")
    public ResponseEntity<ApiResponse<List<CollectionResponse>>> getUserCollectionListWithBooks(@AuthenticationPrincipal User user) {
        List<CollectionResponse> collectionList = collectionService.getCollectionsWithBooksByUser(user.getId());

        return ApiResponse.success(null, collectionList);
    }

    /**
     * POST - 컬렉션 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CollectionResponse>> createCollection(@RequestBody Map<String, String> collection) {
        validateCollection(collection);

        CollectionResponse savedCollection = collectionService.createCollection(collection);;

        return ApiResponse.success("컬렉션이 생성되었습니다.", savedCollection);
    }

    /**
     * PUT - 컬렉션 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CollectionResponse>> updateCollection(@PathVariable Long id,
                                                                            @RequestBody Map<String, String> collection) {
        validateCollection(collection);

        CollectionResponse savedCollection = collectionService.updateCollection(id, collection);

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
     * (공통 메서드) 컬렉션 내용 검증 (NOTE: 필드가 1개 뿐이라 DTO 사용 안함)
     */
    private void validateCollection(Map<String, String> collection) {
        String collectionName = collection.get("collectionName");

        if (collectionName == null || collectionName.isBlank()) {
            throw new IllegalArgumentException("컬렉션 이름을 입력해 주세요.");
        }

        if (collectionName.length() > 100) {
            throw new IllegalArgumentException("컬렉션 이름은 최대 100자까지 입력할 수 있습니다.");
        }
    }
}
