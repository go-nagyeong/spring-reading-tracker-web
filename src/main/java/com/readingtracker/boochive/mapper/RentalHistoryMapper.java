package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.RentalHistory;
import com.readingtracker.boochive.dto.RentalHistoryParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RentalHistoryMapper {

    RentalHistoryMapper INSTANCE = Mappers.getMapper(RentalHistoryMapper.class);

    RentalHistoryParameter toDto(RentalHistory rentalHistory);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RentalHistory toEntity(RentalHistoryParameter rentalHistoryParameter);
}
