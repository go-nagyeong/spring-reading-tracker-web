package com.readingtracker.boochive.dto.note;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostItResponse {

    private final Long id;

    private final String noteText;

    private final Integer pageNumber;

    private final String backgroundColor;
}
