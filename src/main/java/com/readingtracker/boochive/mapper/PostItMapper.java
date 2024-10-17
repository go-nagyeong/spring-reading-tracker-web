package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.dto.note.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostItMapper {

    PostItMapper INSTANCE = Mappers.getMapper(PostItMapper.class);

    @Mapping(target = "readingBook.id", source = "readingBookId")
    ReadingNote toEntity(PostItRequest postItRequest);

    PostItResponse toDto(ReadingNote readingNote);
}
