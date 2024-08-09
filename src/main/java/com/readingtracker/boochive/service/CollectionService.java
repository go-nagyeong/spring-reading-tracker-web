package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.dto.BookCollectionDto;
import com.readingtracker.boochive.dto.BookDto;
import com.readingtracker.boochive.mapper.BookCollectionMapper;
import com.readingtracker.boochive.mapper.BookMapper;
import com.readingtracker.boochive.repository.CollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final ReadingBookService readingBookService;
    private final BookService bookService;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<BookCollectionDto> findCollectionById(Long id) {
        return collectionRepository.findById(id)
                .map(BookCollectionMapper.INSTANCE::toDto);
    }

    @Transactional(readOnly = true)
    public List<BookCollectionDto> getCollectionsByUser(Long userId) {
        return collectionRepository.findAllByUserId(userId)
                .stream()
                .map(BookCollectionMapper.INSTANCE::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookCollectionDto> getCollectionsByUserWithBooks(Long userId) {
        List<BookCollectionDto> collectionList = collectionRepository.findAllByUserIdOrderByIdDesc(userId)
                .stream()
                .map(BookCollectionMapper.INSTANCE::toDto)
                .toList();

        // 모든 컬렉션의 책 목록을 한 번에 가져오기
        Map<Long, List<ReadingBook>> readingBooksByCollection = readingBookService.getReadingListByUser(userId)
                .stream()
                .filter(book -> book.getCollectionId() != null)
                .collect(Collectors.groupingBy(ReadingBook::getCollectionId));

        // 각 컬렉션에 책 목록을 설정
        collectionList.forEach(collection -> {
            List<BookDto> bookList = readingBooksByCollection.getOrDefault(collection.getId(), Collections.emptyList())
                    .stream()
                    .map(readingBook -> bookService.findBookByIsbn(readingBook.getBookIsbn()))
                    .flatMap(Optional::stream)  // 비어있는 경우 건너뛰기
                    .map(BookMapper.INSTANCE::toDto)
                    .toList();

            collection.setBooks(bookList);
        });

        return collectionList;
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public BookCollection createCollection(BookCollection bookCollection) {
        return collectionRepository.save(bookCollection);
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public BookCollection updateCollection(Long id, BookCollection bookCollection) {
        BookCollection existingBookCollection = collectionRepository.findById(id).orElseThrow();

        existingBookCollection.updateCollectionName(bookCollection.getCollectionName());

        return existingBookCollection;
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteCollectionById(Long id) {
        // (이전 연계 작업) 해당 컬렉션에 소속된 모든 책의 컬렉션 참조 초기화
        readingBookService.nullifyCollectionReference(id);

        collectionRepository.deleteById(id);
    }
}
