package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.dto.BookDto;
import com.readingtracker.boochive.dto.CollectionResponse;
import com.readingtracker.boochive.dto.ReadingBookResponse;
import com.readingtracker.boochive.mapper.CollectionMapper;
import com.readingtracker.boochive.repository.CollectionRepository;
import com.readingtracker.boochive.util.ResourceAccessUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final ResourceAccessUtil<BookCollection> resourceAccessUtil;

    private final BookService bookService;
    private final ReadingBookService readingBookService;

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
    public List<CollectionResponse> getCollectionsWithBooksByUser(Long userId) {
        List<Long> collectionIdList = new ArrayList<>(); //

        List<CollectionResponse> collectionList = collectionRepository.findAllByUserIdOrderByIdDesc(userId)
                .stream()
                .map(collection -> {
                    collectionIdList.add(collection.getId());
                    return CollectionMapper.INSTANCE.toDto(collection);
                })
                .toList();

        // 1. 각 컬렉션에 속한 독서 정보 조회
        List<ReadingBookResponse> readingInfoList = readingBookService.getReadingListByUserAndCollectionList(userId, collectionIdList);

        // 2. 독서 정보를 기반으로 책 목록을 한 번에 조회
        List<String> isbnList = readingInfoList.stream()
                .map(ReadingBookResponse::getBookIsbn)
                .toList();

        Map<String, BookDto> bookInfoMap = bookService.getBooksByIsbnList(isbnList)
                .stream()
                .collect(Collectors.toMap(BookDto::getIsbn13, bookInfo -> bookInfo));

        // 3. 각 컬렉션에 책 목록을 설정
        collectionList.forEach(collection -> {
            List<BookDto> books = readingInfoList.stream()
                    .filter(readingInfo -> readingInfo.getCollectionId().equals(collection.getId()))
                    .map(readingInfo -> bookInfoMap.get(readingInfo.getBookIsbn()))
                    .collect(Collectors.toList());

            collection.setBooks(books);
        });

        // TODO: 4. 페이지네이션 정보와 함께 반환
        return collectionList;
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
        readingBookService.nullifyCollectionReference(id);

        collectionRepository.delete(existingBookCollection);
    }
}
