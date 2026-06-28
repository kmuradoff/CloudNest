package me.kmuradoff.cloudnest.mapper;

import me.kmuradoff.cloudnest.dto.RegisterRequest;
import me.kmuradoff.cloudnest.jpa.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User registerToUser(RegisterRequest registerRequest);
}
