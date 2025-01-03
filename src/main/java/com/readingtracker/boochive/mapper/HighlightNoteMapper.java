package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.dto.note.HighlightNoteRequest;
import com.readingtracker.boochive.dto.note.HighlightNoteResponse;
import com.readingtracker.boochive.domain.ReadingNote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HighlightNoteMapper extends NoteMapper {

    HighlightNoteMapper INSTANCE = Mappers.getMapper(HighlightNoteMapper.class);

    @Mapping(target = "readingBook.id", source = "readingBookId")
    ReadingNote toEntity(HighlightNoteRequest highlightNoteRequest);

    @Mapping(target = "bookInfo", ignore = true)
    HighlightNoteResponse toDto(ReadingNote readingNote);
}
