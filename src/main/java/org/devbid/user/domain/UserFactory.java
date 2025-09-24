package org.devbid.user.domain;

public class UserFactory {
    public static User createFromPrimitives(String username, String email, String encodedPassword, String nickname, String phone) {
        return User.of(
                new Username(username),
                new Email(email),
                new Password(encodedPassword),
                new Nickname(nickname),
                new Phone(phone)
        );
    }
}
