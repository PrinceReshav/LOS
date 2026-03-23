package com.los.events;

import java.io.Serializable;

public record UserCreatedEvent(

        String userId,
        String employeeId,
        String username,
        String email,
        String mobile,
        String firstName,
        String lastName,
        String roleId,
        String profileId
) implements Serializable {}