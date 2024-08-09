package com.readingtracker.boochive.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookCollectionDto {

    private Long id;
    private String collectionName;
    private List<BookDto> books;
}
