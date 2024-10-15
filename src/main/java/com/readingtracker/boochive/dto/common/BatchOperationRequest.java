package com.readingtracker.boochive.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BatchOperationRequest<T> {

    private final List<T> createList;

    private final List<T> updateList;

    private final List<Long> deleteList;
}
