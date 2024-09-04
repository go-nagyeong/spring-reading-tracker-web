package com.readingtracker.boochive.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponse {

    private final Long id;

    private final Integer rating;

    private final String reviewText;

    private final Long reviewerId;

    private final String reviewerName;

    private final String reviewerProfile;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDateTime createdDate;
}
