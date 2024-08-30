package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.dto.ReadingBookDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReadingBookDetailMapper {

    ReadingBookDetailMapper INSTANCE = Mappers.getMapper(ReadingBookDetailMapper.class);

    @Mapping(target = "collectionId", source = "collection.id")
    @Mapping(target = "readingStartDate", ignore = true)
    @Mapping(target = "bookInfo", ignore = true)
    @Mapping(target = "isOwned", ignore = true)
    ReadingBookDetailResponse toDto(ReadingBook readingBook);
}
