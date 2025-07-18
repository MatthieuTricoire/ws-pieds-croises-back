package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.AuthUserDto;
import com.crossfit.pieds_croises.model.User;

public interface AuthUserMapper {
  AuthUserDto convertToDTO(User user);
}
