package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.repository.ReadingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReadingListService {

    private final ReadingListRepository readingListRepository;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<ReadingBook> findReadingBookById(Long id) {
        return readingListRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ReadingBook> findReadingBookByUserAndBook(Long userId, String bookIsbn) {
        return readingListRepository.findByUserIdAndBookIsbn(userId, bookIsbn);
    }

    @Transactional(readOnly = true)
    public List<ReadingBook> getReadingListByUser(Long userId) {
        return readingListRepository.findAllByUserId(userId);
    }

    /**
     * [C]R[U]D - CREATE/UPDATE
     */
    @Transactional
    public ReadingBook saveReadingBook(ReadingBook readingBook) {
        if (readingBook.getId() != null) { // update
            ReadingBook existingReadingBook = readingListRepository.findById(readingBook.getId()).orElseThrow();

            existingReadingBook.updateReadingStatus(readingBook.getReadingStatus());
            existingReadingBook.updateCollectionId(readingBook.getCollectionId());

            return existingReadingBook;
        }

        return readingListRepository.save(readingBook); // insert
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteReadingBookById(Long id) {
        readingListRepository.deleteById(id);
    }
}
