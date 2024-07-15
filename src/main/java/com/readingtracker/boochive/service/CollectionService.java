package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.repository.CollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;

    public Optional<BookCollection> findCollectionById(Long id) {
        return collectionRepository.findById(id);
    }

    public List<BookCollection> getCollectionsByUser(Long userId) {
        return collectionRepository.findAllByUserId(userId);
    }

    public BookCollection saveCollection(BookCollection bookCollection) {
        return collectionRepository.save(bookCollection);
    }

    public void deleteCollectionById(Long id) {
        collectionRepository.deleteById(id);
    }
}
