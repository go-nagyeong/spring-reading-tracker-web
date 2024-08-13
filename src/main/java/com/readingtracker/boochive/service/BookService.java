package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.dto.BookParameter;
import com.readingtracker.boochive.mapper.BookMapper;
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
    public Optional<BookParameter> findBookByIsbn(String isbn) {
        return bookRepository.findByIsbn13(isbn)
                .map(BookMapper.INSTANCE::toDto);
    }

    @Transactional(readOnly = true)
    public List<BookParameter> getBooksByIsbnList(List<String> isbnList) {
        return bookRepository.findAllByIsbn13In(isbnList)
                .stream()
                .map(BookMapper.INSTANCE::toDto)
                .toList();
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public BookParameter createBook(BookParameter book) {
        Book newBook = BookMapper.INSTANCE.toEntity(book);

        return BookMapper.INSTANCE.toDto(bookRepository.save(newBook));
    }
}