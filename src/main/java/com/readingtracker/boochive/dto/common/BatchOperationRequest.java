package com.readingtracker.boochive.dto.common;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BatchOperationRequest<T> {

    @Valid
    private final List<T> createList;

    @Valid
    private final List<T> updateList;

    private final List<Long> deleteList;
}
