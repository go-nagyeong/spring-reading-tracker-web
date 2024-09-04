package com.readingtracker.boochive.mapper;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.user.UserInfoRequest;
import com.readingtracker.boochive.dto.user.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserInfoMapper {

    UserInfoMapper INSTANCE = Mappers.getMapper(UserInfoMapper.class);

    User toEntity(UserInfoRequest userInfoRequest);

    @Mapping(target = "username", source = "name")
    UserInfoResponse toDto(User user);
}
