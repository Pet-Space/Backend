package in.makeus.petspace.dto.reservation;

import in.makeus.petspace.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReservationCreateResponseDto {

    private Long reservationId;

    public ReservationCreateResponseDto(Reservation reservation) {
        reservationId = reservation.getId();
    }
}
