package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.enums.NoteType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingNoteRepository extends JpaRepository<ReadingNote, Long> {

    Page<ReadingNote> findAllByUserIdAndNoteTypeOrderByIdDesc(Long userId, NoteType noteType, Pageable pageable);
}
