package org.devbid.user.ui;

import org.devbid.user.application.UserRegistrationResult;
import org.devbid.user.application.command.UserRegistrationCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserRegistrationCommand toCommand(UserRegistrationRequest request);

    UserRegistrationResponse toResponse(UserRegistrationResult result);
}