package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.ReadingBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingListRepository extends JpaRepository<ReadingBook, Long> {

    List<ReadingBook> findAllByUserId(Long userId);

    Optional<ReadingBook> findByUserIdAndBookIsbn(Long userId, String bookIsbn);
}
