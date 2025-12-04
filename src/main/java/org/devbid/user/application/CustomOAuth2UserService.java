package org.devbid.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devbid.user.domain.*;
import org.devbid.user.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 부모 클래스로 유저 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("OAuth2User 로그인 시도: {}" + oAuth2User.getAttributes());

        String providerId = getProviderId(userRequest); //provider 구분 (kakao/google)
        String providerUserId;
        String email;
        String nickname;

        //데이터 추출
        if("kakao".equals(providerId)) {
            providerUserId = String.valueOf(oAuth2User.getAttributes().get("id"));

            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            email = (String) kakaoAccount.get("email");

            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            nickname = (String) profile.get("nickname");

        } else if ("google".equals(providerId)) {
            providerUserId = (String) oAuth2User.getAttributes().get("sub");
            email = (String) oAuth2User.getAttributes().get("email");
            nickname = (String) oAuth2User.getAttributes().get("name");
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인 입니다.");
        }

        //DB에서 유저 조회 또는 생성
        //providerId(kakao, google)와 providerUserId를 조합해야 고유하게 식별 가능 (복합키)
        User user = userRepository.findBySocialAuthInfo_ProviderIdAndSocialAuthInfo_ProviderUserId(providerId,providerUserId)
                .orElseGet(() -> {
                    //회원가입
                    log.info("새로운 소셜 회원 가입: provider={}, email={}", providerId, email);

                    SocialAuthInfo socialAuthInfo = new SocialAuthInfo(providerId, providerUserId);
                    User newUser = User.createFromSocialAuth(
                            socialAuthInfo,
                            new Email(email),
                            new Nickname(nickname)
                    );
                    return userRepository.save(newUser);
                });
        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    private String getProviderId(OAuth2UserRequest userRequest) {
        return userRequest.getClientRegistration().getRegistrationId();
    }
}
