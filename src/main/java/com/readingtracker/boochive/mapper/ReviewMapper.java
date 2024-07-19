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
        @Mapping(target = "reviewerId", source = "user.id"),
        @Mapping(target = "reviewerName", source = "user.name"),
        @Mapping(target = "reviewerProfile", source = "user.profileImage")
    })
    ReviewDto toDto(Review review);
}
