package com.readingtracker.boochive.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewDto {

    private Long id;
    private Integer rating;
    private String reviewText;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    private Long reviewerId;
    private String reviewerName;
    private String reviewerProfile;

    public ReviewDto(Long id, Integer rating, String reviewText, LocalDateTime createdAt, Long reviewerId,
                     String reviewerName, String reviewerProfile) {
        this.id = id;
        this.rating = rating;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.reviewerProfile = reviewerProfile;
    }
}
