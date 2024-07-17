package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.repository.CollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<BookCollection> findCollectionById(Long id) {
        return collectionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<BookCollection> getCollectionsByUser(Long userId) {
        return collectionRepository.findAllByUserId(userId);
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
        collectionRepository.deleteById(id);
    }
}
