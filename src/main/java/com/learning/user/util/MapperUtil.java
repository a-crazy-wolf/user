package com.learning.user.util;

import com.learning.user.dto.UserDto;
import com.learning.user.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MapperUtil {

    @Mapping(target = "enabled" , constant = "true")
    @Mapping(target = "accountNonExpired" , constant = "true")
    @Mapping(target = "credentialsNonExpired" , constant = "true")
    @Mapping(target = "accountNonLocked" , constant = "true")
    User userDtoToUserEntity(UserDto dto);

    UserDto userEntityToUserDto(User entity);
}
