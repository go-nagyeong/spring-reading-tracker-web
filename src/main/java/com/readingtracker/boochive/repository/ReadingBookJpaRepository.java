package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingBookJpaRepository extends JpaRepository<ReadingBook, Long> {

    Optional<ReadingBook> findByUserIdAndBookIsbn(Long userId, String bookIsbn);

    List<ReadingBook> findAllByUserIdAndBookIsbnIn(Long userId, List<String> bookIsbnList);

    List<ReadingBook> findAllByBookIsbnAndReadingStatus(String bookIsbnList, ReadingStatus readingStatus);
}
