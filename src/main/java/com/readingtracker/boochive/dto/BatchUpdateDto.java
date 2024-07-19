package com.readingtracker.boochive.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class BatchUpdateDto<T> {

    private List<T> updateList;
    private List<T> deleteList;
}
