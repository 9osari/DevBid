package org.devbid.user.domain;

public class UserFactory {
    public static User createFromPrimitives(
            String username,
            String email,
            String encodedPassword,
            String nickname,
            String phone,
            String zipcode,
            String street,
            String detail
    ) {
        return User.create(
                new Username(username),
                new Email(email),
                new Password(encodedPassword),
                new Nickname(nickname),
                new Phone(phone),
                new Address(zipcode, street, detail)
        );
    }

    public static User createSocialUser(
            String providerId,
            String providerUserId,
            Email email,
            Nickname nickname
    ) {
        return User.createFromSocialAuth(
                new SocialAuthInfo(providerId, providerUserId),
                email,
                nickname
        );
    }
}
