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
import java.util.Map;

@RestController
@RequestMapping("/api/collection")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveCollection(@RequestBody BookCollection bookCollection,
                                                                @AuthenticationPrincipal User user) {
        bookCollection.setUserId(user.getId()); // 사용자 ID 세팅

        BookCollection newCollection = null;
        if (bookCollection.getId() != null) {
            collectionService.findCollectionById(bookCollection.getId())
                    .ifPresent(collectionService::saveCollection);
        } else {
            newCollection = collectionService.saveCollection(bookCollection);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("newCollection", newCollection);

        return ApiResponse.success("컬렉션이 성공적으로 생성되었습니다.", data);
    }
}
