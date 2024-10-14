package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.dto.note.PencilNoteRequest;
import com.readingtracker.boochive.dto.note.PencilNoteResponse;
import com.readingtracker.boochive.domain.ReadingNote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PencilNoteMapper extends NoteMapper {

    PencilNoteMapper INSTANCE = Mappers.getMapper(PencilNoteMapper.class);

    ReadingNote toEntity(PencilNoteRequest pencilNoteRequest);

    @Mapping(target = "bookInfo", ignore = true)
    PencilNoteResponse toDto(ReadingNote readingNote);
}
