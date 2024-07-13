package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.BookCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionRepository extends JpaRepository<BookCollection, Long> {

    List<BookCollection> findAllByUserId(Long userId);
}
