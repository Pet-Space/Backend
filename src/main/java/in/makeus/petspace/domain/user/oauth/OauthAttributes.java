package in.makeus.petspace.domain.user.oauth;

import in.makeus.petspace.domain.Status;
import in.makeus.petspace.domain.user.Role;
import in.makeus.petspace.domain.user.User;
import in.makeus.petspace.util.exception.UserException;

import java.util.Arrays;
import java.util.Map;

import static in.makeus.petspace.util.BaseResponseStatus.NONE_OAUTH_PROVIDER;

public enum OauthAttributes {
    KAKAO("kakao") {
        public User of(Map<String, Object> attributes) {
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(attributes);
            return User.builder()
                    .nickname(kakaoUserInfo.getNickName())
                    .email(kakaoUserInfo.getEmail())
                    .oauthProvider(kakaoUserInfo.getProvider())
                    .profileImage(kakaoUserInfo.getDefaultProfileImage())
                    .privacyAgreement(true)
                    .hostPermission(false)
                    .status(Status.ACTIVE)
                    .role(Role.USER)
                    .build();
        }
    };

    private final String providerName;

    OauthAttributes(String providerName) {
        this.providerName = providerName;
    }

    public static User extract(String providerName, Map<String, Object> userAttributes) {
        return Arrays.stream(values())
                .filter(provider -> providerName.equals(provider.providerName))
                .findFirst()
                .orElseThrow(() -> new UserException(NONE_OAUTH_PROVIDER))
                .of(userAttributes);
    }

    public abstract User of(Map<String, Object> attributes);
}
