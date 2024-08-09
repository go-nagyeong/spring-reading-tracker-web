package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.dto.BookCollectionDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookCollectionMapper {

    BookCollectionMapper INSTANCE = Mappers.getMapper(BookCollectionMapper.class);

    BookCollectionDto toDto(BookCollection bookCollection);
}
