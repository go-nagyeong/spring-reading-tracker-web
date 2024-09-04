package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.dto.CollectionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CollectionMapper {

    CollectionMapper INSTANCE = Mappers.getMapper(CollectionMapper.class);

    @Mapping(target = "books", ignore = true)
    CollectionResponse toDto(BookCollection bookCollection);
}
