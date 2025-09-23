package org.devbid.user.domain;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    DORMANT;

    public static UserStatus getUserStatus(String status) {
        if(status == null) {
            return ACTIVE;
        }
        try {
            return valueOf(status.toUpperCase());
        }  catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user status value: " + status);
        }
    }
}
