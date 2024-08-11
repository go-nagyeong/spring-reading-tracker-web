package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.UserInfoParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserInfoMapper {

    UserInfoMapper INSTANCE = Mappers.getMapper(UserInfoMapper.class);

    @Mapping(target = "username", source = "name")
    UserInfoParameter toDto(User user);
}
