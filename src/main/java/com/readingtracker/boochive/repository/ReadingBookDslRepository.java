package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.dto.ReadingBookFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReadingBookDslRepository {

    Page<ReadingBook> getReadingBooksWithFilter(Long userId,
                                                ReadingBookFilterDto filterDto,
                                                Pageable pageable);
}
