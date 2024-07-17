package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.repository.ReadingBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReadingBookService {

    private final ReadingBookRepository readingListRepository;

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

    @Transactional(readOnly = true)
    public List<ReadingBook> getReadingListByBook(String bookIsbn) {
        return readingListRepository.findAllByBookIsbn(bookIsbn);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public ReadingBook createReadingBook(ReadingBook readingBook) {
        return readingListRepository.save(readingBook);
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public ReadingBook updateReadingBook(Long id, ReadingBook readingBook) {
        ReadingBook existingReadingBook = readingListRepository.findById(id).orElseThrow();

        if (!existingReadingBook.getReadingStatus().equals(readingBook.getReadingStatus())) {
            existingReadingBook.updateReadingStatus(readingBook.getReadingStatus());
        }
        if (!Objects.equals(existingReadingBook.getCollectionId(), readingBook.getCollectionId())) {
            existingReadingBook.updateCollectionId(readingBook.getCollectionId());
        }

        return existingReadingBook;
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteReadingBookById(Long id) {
        readingListRepository.deleteById(id);
    }
}
