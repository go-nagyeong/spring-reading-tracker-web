package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.dto.CollectionDetailResponse;
import com.readingtracker.boochive.dto.BookParameter;
import com.readingtracker.boochive.dto.ReadingBookDetailResponse;
import com.readingtracker.boochive.mapper.CollectionDetailMapper;
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
    public Optional<CollectionDetailResponse> findCollectionById(Long id) {
        return collectionRepository.findById(id)
                .map(CollectionDetailMapper.INSTANCE::toDto);
    }

    @Transactional(readOnly = true)
    public List<CollectionDetailResponse> getCollectionsByUser(Long userId) {
        return collectionRepository.findAllByUserId(userId)
                .stream()
                .map(CollectionDetailMapper.INSTANCE::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CollectionDetailResponse> getCollectionsWithBooksByUser(Long userId) {
        List<Long> collectionIdList = new ArrayList<>(); //

        List<CollectionDetailResponse> collectionList = collectionRepository.findAllByUserIdOrderByIdDesc(userId)
                .stream()
                .map(collection -> {
                    collectionIdList.add(collection.getId());
                    return CollectionDetailMapper.INSTANCE.toDto(collection);
                })
                .toList();

        // 1. 각 컬렉션에 속한 독서 정보 조회
        List<ReadingBookDetailResponse> readingInfoList = readingBookService.getReadingListByUserAndCollectionList(userId, collectionIdList);

        // 2. 독서 정보를 기반으로 책 목록을 한 번에 조회
        List<String> isbnList = readingInfoList.stream()
                .map(ReadingBookDetailResponse::getBookIsbn)
                .toList();

        Map<String, BookParameter> bookInfoMap = bookService.getBooksByIsbnList(isbnList)
                .stream()
                .collect(Collectors.toMap(BookParameter::getIsbn13, bookInfo -> bookInfo));

        // 3. 각 컬렉션에 책 목록을 설정
        collectionList.forEach(collection -> {
            List<BookParameter> books = readingInfoList.stream()
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
    public CollectionDetailResponse createCollection(BookCollection collection) {
        return CollectionDetailMapper.INSTANCE.toDto(collectionRepository.save(collection));
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public CollectionDetailResponse updateCollection(Long id, BookCollection bookCollection) {
        BookCollection existingBookCollection = resourceAccessUtil.checkAccessAndRetrieve(id, BookCollection.class);

        existingBookCollection.updateCollectionName(bookCollection.getCollectionName());

        return CollectionDetailMapper.INSTANCE.toDto(existingBookCollection);
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteCollectionById(Long id) {
        BookCollection existingBookCollection = resourceAccessUtil.checkAccessAndRetrieve(id, BookCollection.class);

        // (이전 연계 작업) 해당 컬렉션에 소속된 모든 책의 컬렉션 참조 초기화
        readingBookService.nullifyCollectionReference(id);

        collectionRepository.delete(existingBookCollection);
    }
}
