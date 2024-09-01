package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.enums.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReadingBookJpaRepository extends JpaRepository<ReadingBook, Long> {

    Optional<ReadingBook> findByUserIdAndBookIsbn(Long userId, String bookIsbn);

    List<ReadingBook> findAllByUserId(Long userId);

    List<ReadingBook> findAllByUserIdAndBookIsbnIn(Long userId, List<String> bookIsbnList);

    List<ReadingBook> findAllByUserIdAndCollectionIdIn(Long userId, List<Long> collectionIdList);

    List<ReadingBook> findAllByUserIdAndReadingStatus(Long userId, ReadingStatus readingStatus);

    Integer countByBookIsbnAndReadingStatus(String bookIsbnList, ReadingStatus readingStatus);

    @Modifying
    @Query("UPDATE ReadingBook rb SET rb.collection.id = null WHERE rb.collection.id = :collectionId")
    void nullifyCollectionReferenceByCollectionId(@Param("collectionId") Long collectionId);
}
