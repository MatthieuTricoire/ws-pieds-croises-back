package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.UserDto;
import com.crossfit.pieds_croises.dto.UserUpdateDto;
import com.crossfit.pieds_croises.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isFirstLoginComplete", ignore = true)
    UserDto convertToDtoForUser(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "weightHistory", ignore = true)
    @Mapping(target = "performanceHistoryList", ignore = true)
    UserDto convertToDtoForAdmin(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "weightHistory", ignore = true)
    @Mapping(target = "performanceHistoryList", ignore = true)
    @Mapping(target = "strikeCount", ignore = true)
    @Mapping(target = "roles", source = "roles")
    UserDto convertToUserCreatedDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "strikeCount", ignore = true)
    @Mapping(target = "userSubscriptions", ignore = true)
    @Mapping(target = "weightHistory", ignore = true)
    @Mapping(target = "performanceHistoryList", ignore = true)
    UserDto convertToDtoForAnyUser(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User convertToEntity(UserDto userDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateUserFromDto(UserUpdateDto userDto, @MappingTarget User user);
}
