package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.Book;
import com.readingtracker.boochive.dto.BookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mappings({
            @Mapping(target = "isbn", source = "isbn10"),
            @Mapping(target = "formatAuthor", source = "author"),
            @Mapping(target = "subInfo.itemPage", source = "pages")
    })
    BookDto toDto(Book book);
}
