package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.BookCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionRepository extends JpaRepository<BookCollection, Long> {

    List<BookCollection> findAllByUserId(Long userId);

    Page<BookCollection> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);
}
