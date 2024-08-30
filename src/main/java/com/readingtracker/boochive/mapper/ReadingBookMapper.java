package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.dto.ReadingBookRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReadingBookMapper {

    ReadingBookMapper INSTANCE = Mappers.getMapper(ReadingBookMapper.class);

    @Mapping(target = "collection.id", source = "collectionId")
    ReadingBook toEntity(ReadingBookRequest readingBookRequest);
}
