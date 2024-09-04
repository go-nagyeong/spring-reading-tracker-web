package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.auth.LoggedInUserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoggedInUserMapper {

    LoggedInUserMapper INSTANCE = Mappers.getMapper(LoggedInUserMapper.class);

    @Mapping(target = "username", source = "name")
    LoggedInUserResponse toDto(User user);
}
