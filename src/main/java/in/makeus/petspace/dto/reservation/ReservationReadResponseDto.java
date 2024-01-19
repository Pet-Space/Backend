package in.makeus.petspace.dto.reservation;

import in.makeus.petspace.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class ReservationReadResponseDto {

    private String reservationCode;
    private Long roomId;
    private boolean reviewCreated;
    private String roomName;
    private List<String> roomImageUrls;
    private LocalDate startDate;
    private LocalDate endDate;
    private int remainingDays;

    public ReservationReadResponseDto(Reservation reservation) {
        reservationCode = reservation.getReservationCode();
        roomId = reservation.getRoom().getId();
        reviewCreated = reservation.isReviewCreated();
        roomName = reservation.getRoom().getRoomName();
        roomImageUrls = reservation.getRoom().getRoomImages().stream()
                .map(roomImage -> roomImage.getRoomImageUrl())
                .collect(Collectors.toList());
        // roomImage가 5개 이상인 경우 5개까지만 출력
        if(roomImageUrls.size() > 5) {
            roomImageUrls = roomImageUrls.subList(0, 5);
        }
        startDate = reservation.getStartDate().toLocalDate();
        endDate = reservation.getEndDate().toLocalDate();
        remainingDays = (int) Duration.between(LocalDate.now().atStartOfDay(), startDate.atStartOfDay()).toDays();
    }
}

