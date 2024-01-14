package in.makeus.petspace.controller;

import in.makeus.petspace.domain.user.auth.PrincipalDetails;
import in.makeus.petspace.dto.favorite.FavoriteClickResponseDto;
import in.makeus.petspace.dto.favorite.FavoritesSliceResponseDto;
import in.makeus.petspace.service.FavoriteService;
import in.makeus.petspace.util.BaseResponse;
import in.makeus.petspace.util.input.room.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/favorites")
    public BaseResponse<FavoritesSliceResponseDto> showFavorites(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                 @RequestParam RegionType region,
                                                                 @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                 @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        log.info("input region={}", region.getKorRegionName());
        Long userId = principalDetails.getId();
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("favorite 통신 [{}][{}]", region.getKorRegionName(), page);
        FavoritesSliceResponseDto responseDto = favoriteService.showFavoritesSliceByRegion(userId, region.getKorRegionName(), pageRequest);
        log.info("favorite 통신 성공!!");
        return new BaseResponse<>(responseDto);
    }

    @PostMapping("/favorites")
    public BaseResponse<FavoriteClickResponseDto> addFavorite(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                              @RequestParam Long roomId) {
        Long userId = principalDetails.getId();
        log.info("user=[{}][{}]", principalDetails.getId(), principalDetails.getUsername());
        FavoriteClickResponseDto responseDto = favoriteService.clickFavorite(userId, roomId);
        return new BaseResponse<>(responseDto);
    }
}
