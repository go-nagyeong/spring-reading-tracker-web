package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.PurchaseHistory;
import com.readingtracker.boochive.dto.purchase.PurchaseHistoryRequest;
import com.readingtracker.boochive.dto.purchase.PurchaseHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PurchaseHistoryMapper {

    PurchaseHistoryMapper INSTANCE = Mappers.getMapper(PurchaseHistoryMapper.class);

    PurchaseHistory toEntity(PurchaseHistoryRequest purchaseHistoryRequest);

    PurchaseHistoryResponse toDto(PurchaseHistory purchaseHistory);
}
