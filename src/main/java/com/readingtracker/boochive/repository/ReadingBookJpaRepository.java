package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.dto.statistics.ReadingBookStatisticsDto;
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

    /**
     * 단일 조회 쿼리
     */
    Optional<ReadingBook> findByUserIdAndBookIsbn13(Long userId, String bookIsbn);


    /**
     * 범위 조회 쿼리
     */
    List<ReadingBook> findAllByUserIdAndBookIsbn13In(Long userId, List<String> bookIsbnList);

    @Query("SELECT rb FROM ReadingBook rb " +
            "JOIN FETCH rb.book b " +
            "JOIN FETCH rb.user u " +
            "WHERE u.id = :userId ")
    List<ReadingBook> findAllByUserId(Long userId);

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

    @Query("SELECT rb FROM ReadingBook rb " +
            "JOIN FETCH rb.book b " +
            "JOIN FETCH rb.readingNotes n " +
            "JOIN FETCH rb.user u " +
            "WHERE u.id = :userId " +
            "AND n.noteType = :noteType " +
            "ORDER BY n.id DESC")
    Page<ReadingBook> findAllByUserIdAndNoteTypeOrderByIdDesc(Long userId, NoteType noteType, Pageable pageable);


    /**
     * 통계/카운트 쿼리
     */
    // 사용자 리뷰 평점, 완독 수, 독서 노트 수
    @Query("SELECT new com.readingtracker.boochive.dto.statistics.ReadingBookStatisticsDto(" +
                "rb.id, r.rating, COUNT(rr), COUNT(n)) " +
            "FROM ReadingBook rb " +
            "JOIN rb.user u " +
            "LEFT JOIN Review r ON r.bookIsbn = rb.book.isbn13 AND r.user.id = :userId " +
            "LEFT JOIN rb.readingNotes n " +
            "LEFT JOIN rb.readingRecords rr ON rr.endDate IS NOT NULL " +
            "WHERE u.id = :userId " +
            "AND rb.id IN :idList " +
            "GROUP BY rb.id, r.id")
    List<ReadingBookStatisticsDto> getReadingBookStatistics(Long userId, List<Long> idList);


    /**
     * 수정/삭제 쿼리
     */
    @Modifying
    @Query("UPDATE ReadingBook rb SET rb.collection.id = null WHERE rb.collection.id = :collectionId")
    void nullifyCollectionReferenceByCollectionId(@Param("collectionId") Long collectionId);
}
