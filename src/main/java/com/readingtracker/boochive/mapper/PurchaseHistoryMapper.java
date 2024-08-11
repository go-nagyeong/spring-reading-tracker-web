package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.PurchaseHistory;
import com.readingtracker.boochive.dto.PurchaseHistoryParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PurchaseHistoryMapper {

    PurchaseHistoryMapper INSTANCE = Mappers.getMapper(PurchaseHistoryMapper.class);

    PurchaseHistoryParameter toDto(PurchaseHistory purchaseHistory);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PurchaseHistory toEntity(PurchaseHistoryParameter purchaseHistoryResponse);
}
