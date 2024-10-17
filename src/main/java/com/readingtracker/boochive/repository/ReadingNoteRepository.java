package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.enums.NoteType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReadingNoteRepository extends JpaRepository<ReadingNote, Long> {

    @Query("SELECT n FROM ReadingNote n " +
            "JOIN FETCH n.readingBook rb " +
            "JOIN FETCH rb.book b " +
            "WHERE n.id = :id ")
    Optional<ReadingNote> findById(Long id);

    @Query("SELECT n FROM ReadingNote n " +
            "JOIN FETCH n.readingBook rb " +
            "JOIN FETCH rb.book b " +
            "JOIN FETCH n.user u " +
            "WHERE u.id = :userId " +
            "AND n.noteType = :noteType " +
            "ORDER BY n.id DESC")
    Page<ReadingNote> findAllByUserIdAndNoteTypeOrderByIdDesc(Long userId, NoteType noteType, Pageable pageable);

    Long countByReadingBookIdAndNoteType(Long readingBookId, NoteType noteType);
}
