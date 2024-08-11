package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.Review;
import com.readingtracker.boochive.dto.ReviewResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReviewMapper {

    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(target = "reviewerId", source = "user.id")
    @Mapping(target = "reviewerName", source = "user.name")
    @Mapping(target = "reviewerProfile", source = "user.profileImage")
    @Mapping(target = "createdDate", source = "createdAt")
    ReviewResponse toDto(Review review);
}
