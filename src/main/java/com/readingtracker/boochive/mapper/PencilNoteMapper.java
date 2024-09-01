package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.dto.PencilNoteRequest;
import com.readingtracker.boochive.dto.PencilNoteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PencilNoteMapper {

    PencilNoteMapper INSTANCE = Mappers.getMapper(PencilNoteMapper.class);

    ReadingNote toEntity(PencilNoteRequest pencilNoteRequest);

    @Mapping(target = "bookInfo", ignore = true)
    PencilNoteResponse toDto(ReadingNote readingNote);
}
