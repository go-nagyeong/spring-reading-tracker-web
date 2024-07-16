package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.service.CollectionService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/collection")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, BookCollection>>> saveCollection(@RequestBody BookCollection bookCollection,
                                                                @AuthenticationPrincipal User user) {
        // 컬렉션 내용 검증 (필수값 체크)
        if (bookCollection.getCollectionName().isBlank()) {
            return ApiResponse.failure("컬렉션 이름을 입력해 주세요.");
        }

        bookCollection.updateUserId(user.getId()); // 사용자 ID 세팅

        BookCollection savedCollection = null;
        try {
            savedCollection = collectionService.saveCollection(bookCollection);
        } catch (DataAccessException dae) {
            // 데이터베이스 관련 예외 처리
            return ApiResponse.failure("컬렉션을 생성하는 중 오류가 발생했습니다.");
        } catch (Exception e) {
            // 기타 예외 처리
            return ApiResponse.failure("컬렉션을 생성하는 중 알 수 없는 오류가 발생했습니다.");
        }

        Map<String, BookCollection> data = new HashMap<>();
        data.put("saveResult", savedCollection);

        return ApiResponse.success("컬렉션이 생성되었습니다.", data);
    }
}
