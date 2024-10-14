package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.dto.note.NoteResponse;

public interface NoteMapper {

    <T extends NoteResponse> T toDto(ReadingNote note);
}
