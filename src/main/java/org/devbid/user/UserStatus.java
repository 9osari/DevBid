package org.devbid.user;

public enum UserStatus {
    ACTIVE          //정상
    , WITHDRAWN;    //탈퇴


    public boolean isUsable() {
        return this == ACTIVE;
    }

}
