package com.petspace.dev.controller;

import com.petspace.dev.dto.auth.LoginTokenResponseDto;
import com.petspace.dev.dto.auth.OauthRequestDto;
import com.petspace.dev.service.auth.OauthService;
import com.petspace.dev.util.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Oauth login", description = "Oauth login API Doc")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/oauth/{provider}")
    public BaseResponse<LoginTokenResponseDto> OauthLoginRequest(@PathVariable String provider,
                                                                 @RequestBody OauthRequestDto requestDto) {
        log.info("provider={}, accessToken={}", provider, requestDto.getAccessToken());
        LoginTokenResponseDto loginTokenResponseDto = oauthService.login(provider, requestDto);
        return new BaseResponse<>(loginTokenResponseDto);
    }
}
