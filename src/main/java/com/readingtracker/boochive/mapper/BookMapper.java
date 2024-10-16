package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.Book;
import com.readingtracker.boochive.dto.book.BookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(target = "isbn10", source = "isbn")
    @Mapping(target = "author", source = "formatAuthor")
    @Mapping(target = "pages", source = "subInfo.itemPage")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toEntity(BookDto parameter);

    @Mapping(target = "isbn", source = "isbn10")
    @Mapping(target = "formatAuthor", source = "author")
    @Mapping(target = "subInfo.itemPage", source = "pages")
    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "categoryList", ignore = true)
    @Mapping(target = "statistics", ignore = true)
    BookDto toDto(Book book);
}
