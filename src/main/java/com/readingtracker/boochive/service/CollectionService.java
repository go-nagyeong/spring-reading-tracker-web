package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.dto.book.BookDto;
import com.readingtracker.boochive.dto.collection.CollectionResponse;
import com.readingtracker.boochive.mapper.BookMapper;
import com.readingtracker.boochive.mapper.CollectionMapper;
import com.readingtracker.boochive.repository.CollectionRepository;
import com.readingtracker.boochive.repository.ReadingBookJpaRepository;
import com.readingtracker.boochive.util.ResourceAccessUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final ResourceAccessUtil<BookCollection> resourceAccessUtil;

    private final ReadingBookJpaRepository readingBookRepository;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public List<CollectionResponse> getCollectionsByUser(Long userId) {
        return collectionRepository.findAllByUserId(userId)
                .stream()
                .map(CollectionMapper.INSTANCE::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<CollectionResponse> getCollectionsWithBooksByUser(Long userId, Pageable pageable) {
        // 1. 컬렉션 조회
        Page<BookCollection> pageableCollectionList = collectionRepository.findAllByUserIdOrderByIdDesc(userId, pageable);

        // 2. 컬렉션 내 책 목록을 별도로 조회 (BookCollection의 관계 데이터 ReadingBook > ReadingBook의 관계 데이터 Book)
        List<Long> collectionIds = pageableCollectionList.stream()
                .map(BookCollection::getId)
                .toList();
        Map<Long, List<BookDto>> readingBookMap = readingBookRepository.findAllByCollectionIdIn(collectionIds)
                .stream()
                .collect(Collectors.groupingBy(
                        readingBook -> readingBook.getCollection().getId(),
                        Collectors.mapping(
                                readingBook -> BookMapper.INSTANCE.toDto(readingBook.getBook()),
                                Collectors.toList()
                        )
                ));;

        // 3. DTO 변환 및 관계 데이터 세팅
        List<CollectionResponse> collectionList = pageableCollectionList.getContent()
                .stream()
                .map(collection -> {
                    CollectionResponse collectionResponse = CollectionMapper.INSTANCE.toDto(collection);
                    // 추가 정보 1. 책 목록
                    collectionResponse.setBooks(readingBookMap.getOrDefault(collection.getId(), Collections.emptyList()));
                    return collectionResponse;
                })
                .toList();

        return new PageImpl<>(collectionList, pageable, pageableCollectionList.getTotalElements());
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public CollectionResponse createCollection(Map<String, String> collection) {
        BookCollection newCollection = BookCollection.builder()
                .collectionName(collection.get("collectionName"))
                .build();

        return CollectionMapper.INSTANCE.toDto(collectionRepository.save(newCollection));
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public CollectionResponse updateCollection(Long id, Map<String, String> collection) {
        BookCollection existingBookCollection = resourceAccessUtil.checkAccessAndRetrieve(id);

        existingBookCollection.updateCollectionName(collection.get("collectionName"));

        return CollectionMapper.INSTANCE.toDto(existingBookCollection);
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteCollectionById(Long id) {
        BookCollection existingBookCollection = resourceAccessUtil.checkAccessAndRetrieve(id);

        // (이전 연계 작업) 해당 컬렉션에 소속된 모든 책의 컬렉션 참조 초기화
        readingBookRepository.nullifyCollectionReferenceByCollectionId(id);

        collectionRepository.delete(existingBookCollection);
    }
}
