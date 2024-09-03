package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.dto.HighlightNoteRequest;
import com.readingtracker.boochive.dto.HighlightNoteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HighlightNoteMapper {

    HighlightNoteMapper INSTANCE = Mappers.getMapper(HighlightNoteMapper.class);

    ReadingNote toEntity(HighlightNoteRequest highlightNoteRequest);

    @Mapping(target = "bookInfo", ignore = true)
    HighlightNoteResponse toDto(ReadingNote readingNote);
}
