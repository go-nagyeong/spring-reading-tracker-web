package com.readingtracker.boochive.dto.note;

import com.readingtracker.boochive.dto.book.BookDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class HighlightNoteResponse implements NoteResponse {

    private final Long id;

    private final String bookIsbn;

    private final String noteText;

    private final String attachmentImage;

    private final String backgroundColor;

    private final Integer pageNumber;

    private final Integer isPublic;

    private final LocalDate createDate;

    @Setter
    private BookDto bookInfo; // 매핑 이후 조회 변수
}
