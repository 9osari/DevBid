package org.devbid.user.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.user.domain.*;
import org.devbid.user.repository.UserRepository;
import org.devbid.user.security.oauth2.attribute.OAuth2Attribute;
import org.devbid.user.security.oauth2.attribute.OAuth2AttributeFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // 부모 클래스로 유저 정보 가져오기
        String providerId = getProviderId(userRequest); //provider 구분 (kakao/google)
        log.info("OAuth2User 로그인 시도: {}, 플랫폼: {}" + oAuth2User.getAttributes() + providerId);

        //provider별 데이터 추출
        OAuth2Attribute oAuth2Attribute = OAuth2AttributeFactory.of(providerId, oAuth2User.getAttributes());

        //DB 조회 또는 신규 생성
        return getOrCreateOAuth2User(
                providerId,
                oAuth2Attribute.getProviderUserId(),
                oAuth2Attribute.getEmail(),
                oAuth2Attribute.getNickname(),
                oAuth2User
        );
    }

    private String getProviderId(OAuth2UserRequest userRequest) {
        return userRequest.getClientRegistration().getRegistrationId();
    }

    private CustomOAuth2User getOrCreateOAuth2User(
            String providerId,
            String providerUserId,
            String email,
            String nickname,
            OAuth2User oAuth2User
    ) {
        //DB에서 유저 조회 또는 생성
        //providerId(kakao, google)와 providerUserId를 조합해야 고유하게 식별 가능 (복합키)
        User user = userRepository.findBySocialAuthInfo_ProviderIdAndSocialAuthInfo_ProviderUserId(providerId,providerUserId)
                .orElseGet(() -> {
                    //회원가입
                    log.info("새로운 소셜 회원 가입: provider={}, email={}", providerId, email);

                    SocialAuthInfo socialAuthInfo = new SocialAuthInfo(
                            providerId,
                            providerUserId
                    );
                    User newUser = User.createFromSocialAuth(
                            socialAuthInfo,
                            new Email(email),
                            new Nickname(nickname)
                    );
                    return userRepository.save(newUser);
                });
        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }
}
