package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.RentalHistory;
import com.readingtracker.boochive.dto.rental.RentalHistoryRequest;
import com.readingtracker.boochive.dto.rental.RentalHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RentalHistoryMapper {

    RentalHistoryMapper INSTANCE = Mappers.getMapper(RentalHistoryMapper.class);

    RentalHistory toEntity(RentalHistoryRequest rentalHistoryRequest);

    RentalHistoryResponse toDto(RentalHistory rentalHistory);
}
