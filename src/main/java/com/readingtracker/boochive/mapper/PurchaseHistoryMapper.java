package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.PurchaseHistory;
import com.readingtracker.boochive.dto.PurchaseHistoryRequest;
import com.readingtracker.boochive.dto.PurchaseHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PurchaseHistoryMapper {

    PurchaseHistoryMapper INSTANCE = Mappers.getMapper(PurchaseHistoryMapper.class);

    PurchaseHistory toEntity(PurchaseHistoryRequest purchaseHistoryRequest);

    PurchaseHistoryResponse toDto(PurchaseHistory purchaseHistory);
}
