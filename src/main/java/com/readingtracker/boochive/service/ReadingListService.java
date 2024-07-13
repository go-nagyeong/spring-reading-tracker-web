package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.repository.ReadingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReadingListService {

    private final ReadingListRepository readingListRepository;

    public Optional<ReadingBook> findReadingBookById(Long id) {
        return readingListRepository.findById(id);
    }

    public Optional<ReadingBook> findReadingBook(Long userId, String bookIsbn) {
        return readingListRepository.findByUserIdAndBookIsbn(userId, bookIsbn);
    }

    public List<ReadingBook> getReadingList(Long userId) {
        return readingListRepository.findAllByUserId(userId);
    }

    public ReadingBook saveReadingBook(ReadingBook readingBook) {
        Optional<ReadingBook> existReadingBook = readingListRepository.findByUserIdAndBookIsbn(
                readingBook.getUserId(), readingBook.getBookIsbn());

        if (existReadingBook.isPresent()) {
            ReadingBook readingBookToUpdate = existReadingBook.get();
            readingBookToUpdate.setReadingStatus(readingBook.getReadingStatus());
            readingBookToUpdate.setCollectionId(readingBook.getCollectionId());
            return readingListRepository.save(readingBookToUpdate);
        }

        return readingListRepository.save(readingBook);
    }

    public void deleteReadingBook(Long id) {
        readingListRepository.deleteById(id);
    }
}
