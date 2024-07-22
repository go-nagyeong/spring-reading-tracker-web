package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "username", source = "name")
    UserDto toDto(User user);

    @Mapping(target = "name", source = "username")
    User toEntity(UserDto userDto);
}
