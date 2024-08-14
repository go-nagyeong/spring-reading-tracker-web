package com.readingtracker.boochive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BatchUpdateRequest<T> {

    private final List<T> updateList;
    private final List<Long> deleteList;

    public boolean hasUpdateList() {
        return !updateList.isEmpty();
    }

    public boolean hasDeleteList() {
        return !deleteList.isEmpty();
    }
}
