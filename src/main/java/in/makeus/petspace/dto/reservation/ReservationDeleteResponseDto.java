package in.makeus.petspace.dto.reservation;

import in.makeus.petspace.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReservationDeleteResponseDto {
    private Long reservationId;

    public ReservationDeleteResponseDto(Reservation reservation) {
        reservationId = reservation.getId();
    }
}
