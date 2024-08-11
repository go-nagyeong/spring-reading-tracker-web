package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.dto.CollectionDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CollectionDetailMapper {

    CollectionDetailMapper INSTANCE = Mappers.getMapper(CollectionDetailMapper.class);

    @Mapping(target = "books", ignore = true)
    CollectionDetailResponse toDto(BookCollection bookCollection);
}
