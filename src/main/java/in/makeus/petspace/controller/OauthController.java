package in.makeus.petspace.controller;

import in.makeus.petspace.dto.auth.LoginTokenResponseDto;
import in.makeus.petspace.dto.auth.OauthRequestDto;
import in.makeus.petspace.service.auth.OauthService;
import in.makeus.petspace.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
public class OauthController {

    private final OauthService oauthService;

    @PostMapping("/oauth/{provider}")
    public BaseResponse<LoginTokenResponseDto> OauthLoginRequest(@PathVariable String provider,
                                                                 @RequestBody OauthRequestDto requestDto) {
        log.info("provider={}, accessToken={}", provider, requestDto.getAccessToken());
        LoginTokenResponseDto loginTokenResponseDto = oauthService.login(provider, requestDto);
        return new BaseResponse<>(loginTokenResponseDto);
    }
}
