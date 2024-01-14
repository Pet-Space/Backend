package in.makeus.petspace.controller;

import in.makeus.petspace.domain.user.auth.PrincipalDetails;
import in.makeus.petspace.dto.room.RoomDetailResponseDto;
import in.makeus.petspace.dto.room.RoomFacilityResponseDto;
import in.makeus.petspace.dto.room.RoomListResponseDto;
import in.makeus.petspace.service.FavoriteService;
import in.makeus.petspace.service.RoomService;
import in.makeus.petspace.util.BaseResponse;
import in.makeus.petspace.util.input.room.CategoryType;
import in.makeus.petspace.util.input.room.SortBy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class RoomController {
    private final FavoriteService favoriteService;
    private final RoomService roomService;

    @GetMapping("/rooms")
    public BaseResponse<List<RoomListResponseDto>> get(@RequestParam Optional<Integer> page,
                                                       @RequestParam Optional<SortBy> sortBy,
                                                       @RequestParam Optional<CategoryType> categoryType) {
        if (!categoryType.isEmpty()) {
            return new BaseResponse<>(roomService.findAllDescByCategory(page, sortBy, categoryType.get()));
        }
        return new BaseResponse<>(roomService.findAllDesc(page, sortBy));
    }

    @GetMapping("/rooms/host/{userId}")
    public BaseResponse<List<RoomListResponseDto>> getById(@PathVariable Long userId,
                                                           @RequestParam Optional<Integer> page) {
        log.info("user =[{}]", userId);
        return new BaseResponse<>(roomService.findAllDescByUserId(userId, page));
    }

    @GetMapping("/rooms/filtering")
    public BaseResponse<List<RoomListResponseDto>> getByFilter(@RequestParam("startDay") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDay,
                                                         @RequestParam("endDay") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDay,
                                                         @RequestParam Optional<Integer> people,
                                                         @RequestParam Optional<Integer> pets,
                                                         @RequestParam Optional<String> keyword,
                                                         @RequestParam Optional<Integer> page,
                                                         @RequestParam Optional<SortBy> sortBy,
                                                         @RequestParam Optional<CategoryType> categoryType) {
        log.info("startDay={}, endDay={}, keyword={}", startDay, endDay, keyword);
        if (!categoryType.isEmpty()) {
            return new BaseResponse<>(roomService.findAllDescByFilterAndCategory(startDay, endDay, people, pets, keyword, page, sortBy, categoryType.get()));
        }
        return new BaseResponse<>(roomService.findAllDescByFilter(startDay, endDay, people, pets, keyword, page, sortBy));
    }

    @GetMapping("/rooms/{roomId}")
    public BaseResponse<RoomDetailResponseDto> getRoomDetail(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("roomId") Long roomId){

        RoomDetailResponseDto roomDetailResponseDto;

        if(principalDetails == null){
            roomDetailResponseDto = roomService.getRoomDetail(roomId, null);
        }else{
            Long userId = principalDetails.getId();
            roomDetailResponseDto = roomService.getRoomDetail(roomId, userId);
        }

        return new BaseResponse(roomDetailResponseDto);

    }

    @GetMapping("/rooms/{roomId}/facilities")
    public BaseResponse<RoomFacilityResponseDto> getRoomFacilities(@PathVariable("roomId") Long roomId){

        RoomFacilityResponseDto roomFacilitiesDto = roomService.getRoomFacilities(roomId);
        return new BaseResponse(roomFacilitiesDto);
    }
}
