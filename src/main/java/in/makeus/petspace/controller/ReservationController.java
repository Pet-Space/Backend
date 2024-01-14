package in.makeus.petspace.controller;

import in.makeus.petspace.domain.user.auth.PrincipalDetails;
import in.makeus.petspace.dto.reservation.ReservationCreateRequestDto;
import in.makeus.petspace.dto.reservation.ReservationCreateResponseDto;
import in.makeus.petspace.dto.reservation.ReservationDeleteResponseDto;
import in.makeus.petspace.dto.reservation.ReservationSliceResponseDto;
import in.makeus.petspace.service.ReservationService;
import in.makeus.petspace.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservations")
    public BaseResponse<ReservationCreateResponseDto> createReservation(@AuthenticationPrincipal PrincipalDetails principalDetail,
                                                                        @RequestParam("roomId") Long roomId,
                                                                        @RequestBody ReservationCreateRequestDto dto) {
        Long userId = principalDetail.getId();
        ReservationCreateResponseDto responseDto = reservationService.saveReservation(userId, roomId, dto);
        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/reservations/{reservationId}/delete")
    public BaseResponse<ReservationDeleteResponseDto> deleteReservation(@AuthenticationPrincipal PrincipalDetails principalDetail, @PathVariable Long reservationId) {
        Long userId = principalDetail.getId();
        ReservationDeleteResponseDto dto = reservationService.deleteReservation(userId, reservationId);
        return new BaseResponse<>(dto);
    }

    @GetMapping("/reservations")
    public BaseResponse readAllUpcomingReservations(@AuthenticationPrincipal PrincipalDetails principalDetail,
                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                            @RequestParam(value = "size", required = false, defaultValue = "5") int size) {

        Long userId = principalDetail.getId();
        PageRequest pageRequest = PageRequest.of(page, size);
        ReservationSliceResponseDto responseDto = reservationService.findAllUpcomingReservationByPage(userId, pageRequest);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/reservations/terminate")
    public BaseResponse readAllTerminateReservations(@AuthenticationPrincipal PrincipalDetails principalDetail,
                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                            @RequestParam(value = "size", required = false, defaultValue = "3") int size) {

        Long userId = principalDetail.getId();
        PageRequest pageRequest = PageRequest.of(page, size);
        ReservationSliceResponseDto responseDto = reservationService.findAllTerminateReservationByPage(userId, pageRequest);
        return new BaseResponse<>(responseDto);
    }
}
