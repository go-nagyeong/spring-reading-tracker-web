package com.readingtracker.boochive.dto.note;

import com.readingtracker.boochive.dto.book.BookDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PencilNoteResponse implements NoteResponse {

    private final Long id;

    private final String noteText;

    private final Integer isPublic;

    private final LocalDate createDate;

    @Setter
    private BookDto bookInfo; // ManyToOne 관계 Book 데이터 조인
}
