package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.dto.note.BookWithPostItsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookWithPostItsMapper {

    BookWithPostItsMapper INSTANCE = Mappers.getMapper(BookWithPostItsMapper.class);

    @Mapping(target = "readingBookId", source = "id")
    @Mapping(target = "bookIsbn", source = "book.isbn13")
    @Mapping(target = "title", source = "book.title")
    @Mapping(target = "cover", source = "book.cover")
    BookWithPostItsResponse toDto(ReadingBook readingBook);
}
