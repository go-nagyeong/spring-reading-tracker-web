package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.ReadingBookCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReadingBookDslRepository {

    Page<ReadingBook> findAllByUserAndFilters(User user, ReadingBookCondition condition, Pageable pageable);
}
