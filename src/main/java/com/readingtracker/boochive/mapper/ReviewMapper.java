package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.Review;
import com.readingtracker.boochive.dto.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReviewMapper {

    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "reviewerId", expression = "java(review.getUser().getId())"),
        @Mapping(target = "reviewerName", expression = "java(review.getUser().getName())"),
        @Mapping(target = "reviewerProfile", expression = "java(review.getUser().getProfileImage())")
    })
    ReviewDto toDto(Review review);
}
