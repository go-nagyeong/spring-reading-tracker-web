package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.dto.reading.ReadingBookResponse;
import com.readingtracker.boochive.dto.reading.ReadingBookRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReadingBookMapper {

    ReadingBookMapper INSTANCE = Mappers.getMapper(ReadingBookMapper.class);

    @Mapping(target = "book.isbn13", source = "bookIsbn")
    @Mapping(target = "collection.id", source = "collectionId")
    ReadingBook toEntity(ReadingBookRequest readingBookRequest);

    @Mapping(target = "bookIsbn", source = "book.isbn13")
    @Mapping(target = "collectionId", source = "collection.id")
    @Mapping(target = "readingStartDate", ignore = true)
    @Mapping(target = "bookInfo", ignore = true)
    @Mapping(target = "isOwned", ignore = true)
    ReadingBookResponse toDto(ReadingBook readingBook);
}
