package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.ReadingBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingListRepository extends JpaRepository<ReadingBook, Long> {

    Optional<ReadingBook> findByUserIdAndBookIsbn(Long userId, String bookIsbn);

    List<ReadingBook> findAllByUserId(Long userId);

    List<ReadingBook> findAllByBookIsbn(String bookIsbn);
}
