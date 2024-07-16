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
     * [C]R[U]D - CREATE/UPDATE
     */
    @Transactional
    public BookCollection saveCollection(BookCollection bookCollection) {
        if (bookCollection.getId() != null) { // update
            BookCollection existingBookCollection = collectionRepository.findById(bookCollection.getId()).orElseThrow();

            existingBookCollection.updateCollectionName(bookCollection.getCollectionName());

            return existingBookCollection;
        }

        return collectionRepository.save(bookCollection); // insert
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteCollectionById(Long id) {
        collectionRepository.deleteById(id);
    }
}
