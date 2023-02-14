package com.petspace.dev.service.auth;

import com.petspace.dev.domain.user.User;
import com.petspace.dev.domain.user.oauth.OauthAttributes;
import com.petspace.dev.dto.auth.LoginTokenResponseDto;
import com.petspace.dev.dto.auth.OauthRequestDto;
import com.petspace.dev.repository.UserRepository;
import com.petspace.dev.service.RedisService;
import com.petspace.dev.util.jwt.JwtProvider;
import com.petspace.dev.util.jwt.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OauthService {

    private final InMemoryClientRegistrationRepository inMemoryRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @Value("${default.image.url}")
    private String defaultProfileImage;

    @Transactional
    public LoginTokenResponseDto login(String providerName, OauthRequestDto requestDto) {
        ClientRegistration provider = inMemoryRepository.findByRegistrationId(providerName);
        User user = getUserProfile(provider, requestDto);
        log.info("user nickname={}, email={}, oauthProvider={}", user.getNickname(), user.getEmail(), user.getOauthProvider());

        String email = user.getEmail();

        if (!userRepository.existsByEmail(email)) {
            userRepository.save(user);
        }

        Token token = jwtProvider.createToken(email);
        redisService.save(email, token);

        return LoginTokenResponseDto.builder()
                .email(email)
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
    }

    /**
     * 회원정보를 토대로 회원 추출
     */
    private User getUserProfile(ClientRegistration provider, OauthRequestDto requestDto) {
        Map<String, Object> userAttributes = getUserAttributes(provider, requestDto);
        addDefaultProfileImage(userAttributes);
        User extract = OauthAttributes.extract(provider.getClientName(), userAttributes);
        log.info("user=[{}][{}][{}]", extract.getEmail(), extract.getNickname(), extract.getOauthProvider());
        return extract;
    }

    /**
     * token을 토대로 회원정보 요청
     */
    private Map<String, Object> getUserAttributes(ClientRegistration provider, OauthRequestDto requestDto) {
        log.info("userInfoUri = {}", provider.getProviderDetails().getUserInfoEndpoint().getUri());
        return WebClient.create()
                .get()
                .uri(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(header ->
                        header.setBearerAuth(requestDto.getAccessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    private void addDefaultProfileImage(Map<String, Object> userAttributes) {
        userAttributes.put("default_profile_image", defaultProfileImage);
    }
}
