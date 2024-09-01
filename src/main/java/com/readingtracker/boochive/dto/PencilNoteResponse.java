package com.readingtracker.boochive.dto;

import com.readingtracker.boochive.enums.NoteType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PencilNoteResponse {

    private final Long id;
    private final String bookIsbn;
    private final NoteType noteType;
    private final String noteText;
    private final Integer isPublic;
    private final LocalDate createDate;
    @Setter
    private BookParameter bookInfo; // 매핑 이후 조회 변수
}
