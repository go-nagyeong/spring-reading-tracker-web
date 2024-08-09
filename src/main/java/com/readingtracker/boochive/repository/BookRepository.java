package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn13(String isbn13);

    void deleteByIsbn13(String isbn13);
}
