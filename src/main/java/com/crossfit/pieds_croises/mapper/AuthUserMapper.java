package com.crossfit.pieds_croises.mapper;

import org.mapstruct.Mapper;

import com.crossfit.pieds_croises.dto.AuthUserDto;
import com.crossfit.pieds_croises.model.User;

@Mapper(componentModel = "spring")
public interface AuthUserMapper {
  AuthUserDto convertToDTO(User user);
}
