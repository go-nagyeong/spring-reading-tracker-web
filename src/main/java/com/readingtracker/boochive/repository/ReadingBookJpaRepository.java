package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.enums.NoteType;
import com.readingtracker.boochive.enums.ReadingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReadingBookJpaRepository extends JpaRepository<ReadingBook, Long> {

    Optional<ReadingBook> findByUserIdAndBookIsbn13(Long userId, String bookIsbn);

    @Query("SELECT rb FROM ReadingBook rb " +
            "JOIN FETCH rb.book b " +
            "JOIN FETCH rb.user u " +
            "WHERE u.id = :userId ")
    List<ReadingBook> findAllByUserId(Long userId);

    List<ReadingBook> findAllByUserIdAndBookIsbn13In(Long userId, List<String> bookIsbnList);

    @Query("SELECT rb FROM ReadingBook rb " +
            "JOIN FETCH rb.book b " +
            "WHERE rb.collection.id IN :collectionIdList ")
    List<ReadingBook> findAllByCollectionIdIn(List<Long> collectionIdList);

    @Query("SELECT rb FROM ReadingBook rb " +
            "JOIN FETCH rb.book b " +
            "JOIN FETCH rb.user u " +
            "WHERE u.id = :userId " +
            "AND rb.readingStatus = :readingStatus")
    List<ReadingBook> findAllByUserIdAndReadingStatus(Long userId, ReadingStatus readingStatus);

    Integer countByBookIsbn13AndReadingStatus(String bookIsbnList, ReadingStatus readingStatus);

    @Modifying
    @Query("UPDATE ReadingBook rb SET rb.collection.id = null WHERE rb.collection.id = :collectionId")
    void nullifyCollectionReferenceByCollectionId(@Param("collectionId") Long collectionId);

    @Query("SELECT rb FROM ReadingBook rb " +
            "JOIN FETCH rb.book b " +
            "JOIN FETCH rb.readingNotes n " +
            "JOIN FETCH rb.user u " +
            "WHERE u.id = :userId " +
            "AND n.noteType = :noteType " +
            "ORDER BY n.id DESC")
    Page<ReadingBook> findAllByUserIdAndNoteTypeOrderByIdDesc(Long userId, NoteType noteType, Pageable pageable);
}
