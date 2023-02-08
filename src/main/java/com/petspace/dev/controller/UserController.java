package com.petspace.dev.controller;

import com.petspace.dev.domain.user.auth.PrincipalDetails;
import com.petspace.dev.dto.auth.LoginTokenReissueRequestDto;
import com.petspace.dev.dto.auth.LoginTokenResponseDto;
import com.petspace.dev.dto.user.UserCheckEmailResponseDto;
import com.petspace.dev.dto.user.UserJoinRequestDto;
import com.petspace.dev.dto.user.UserLoginRequestDto;
import com.petspace.dev.dto.user.UserResponseDto;
import com.petspace.dev.service.UserService;
import com.petspace.dev.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public BaseResponse<UserResponseDto> join(@Valid @RequestBody UserJoinRequestDto joinRequestDto) {
        UserResponseDto responseDto = userService.join(joinRequestDto);
        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/users")
    public BaseResponse<UserResponseDto> update(@AuthenticationPrincipal PrincipalDetails principalDetails,
                               @RequestParam("profileImage") MultipartFile image) {
        Long userId = principalDetails.getId();
        UserResponseDto responseDto = userService.updateProfileImage(userId, image);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/sign-up/email-check")
    public BaseResponse<UserCheckEmailResponseDto> checkEmail(@RequestParam String email) {
        UserCheckEmailResponseDto checkEmailResponseDto = userService.checkEmailDuplicate(email);
        return new BaseResponse<>(checkEmailResponseDto);
    }

    @PostMapping("/login")
    public BaseResponse<LoginTokenResponseDto> login(@Valid @RequestBody UserLoginRequestDto loginRequestDto) {
        LoginTokenResponseDto loginResponseDto = userService.login(loginRequestDto);
        log.info("로그인 : [{}]", loginResponseDto.getEmail());
        return new BaseResponse<>(loginResponseDto);
    }

    @PostMapping("/token-reissue")
    public BaseResponse<LoginTokenResponseDto> reissue(@RequestBody LoginTokenReissueRequestDto reissueRequestDto) {
        log.info("ref=[{}][{}]", reissueRequestDto.getAccessToken(), reissueRequestDto.getRefreshToken());
        LoginTokenResponseDto loginResponseDto = userService.reissueRefreshToken(reissueRequestDto);
        return new BaseResponse<>(loginResponseDto);
    }
}
