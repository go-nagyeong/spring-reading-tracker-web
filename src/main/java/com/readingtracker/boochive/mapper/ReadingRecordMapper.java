package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.ReadingRecord;
import com.readingtracker.boochive.dto.record.ReadingRecordRequest;
import com.readingtracker.boochive.dto.record.ReadingRecordResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReadingRecordMapper {

    ReadingRecordMapper INSTANCE = Mappers.getMapper(ReadingRecordMapper.class);

    ReadingRecord toEntity(ReadingRecordRequest readingRecordRequest);

    @Mapping(target = "dDay", ignore = true)
    ReadingRecordResponse toDto(ReadingRecord readingRecord);
}
