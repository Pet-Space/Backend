package in.makeus.petspace.service.auth;

import in.makeus.petspace.domain.user.User;
import in.makeus.petspace.domain.user.oauth.OauthAttributes;
import in.makeus.petspace.dto.auth.LoginTokenResponseDto;
import in.makeus.petspace.dto.auth.OauthRequestDto;
import in.makeus.petspace.repository.UserRepository;
import in.makeus.petspace.service.RedisService;
import in.makeus.petspace.util.jwt.JwtProvider;
import in.makeus.petspace.util.jwt.Token;
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
