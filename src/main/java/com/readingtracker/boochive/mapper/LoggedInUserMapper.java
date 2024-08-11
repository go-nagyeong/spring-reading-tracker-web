package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.LoggedInUserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoggedInUserMapper {

    LoggedInUserMapper INSTANCE = Mappers.getMapper(LoggedInUserMapper.class);

    LoggedInUserResponse toDto(User user);
}
