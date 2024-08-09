package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<Book> findBookByIsbn(String isbn) {
        return bookRepository.findByIsbn13(isbn);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public Book createBook(Book Book) {
        return bookRepository.save(Book);
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public Book updateBook(String isbn, Book Book) {
        Book existingBook = bookRepository.findByIsbn13(isbn).orElseThrow();

        //

        return existingBook;
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteBookById(String isbn) {
        bookRepository.deleteByIsbn13(isbn);
    }
}