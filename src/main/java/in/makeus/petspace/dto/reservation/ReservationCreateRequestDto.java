package in.makeus.petspace.dto.reservation;

import in.makeus.petspace.domain.Reservation;
import in.makeus.petspace.domain.Room;
import in.makeus.petspace.domain.Status;
import in.makeus.petspace.domain.user.User;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Data
public class ReservationCreateRequestDto {

    @NotBlank(message = "인원수를 입력해주세요.")
    private int totalGuest;

    @NotBlank(message = "반려동물수를 입력해주세요.")
    private int totalPet;

    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")
    private String startDate;

    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")
    private String endDate;

    //dto -> entity
    public Reservation toEntity(User user, Room room) {
        LocalDate startDate = LocalDate.parse(this.startDate, DateTimeFormatter.ISO_LOCAL_DATE); //String 을 LocalDate 로 변환
        LocalDate endDate = LocalDate.parse(this.endDate, DateTimeFormatter.ISO_LOCAL_DATE); //String 을 LocalDate 로 변환
        return Reservation.builder()
                .user(user)
                .room(room)
                .reservationCode(UUID.randomUUID().toString()) //reservationCode 생성
                .totalGuest(totalGuest)
                .totalPet(totalPet)
                .startDate(startDate.atTime(room.getCheckinTime().toLocalTime()))
                .endDate(endDate.atTime(room.getCheckoutTime().toLocalTime()))
                .totalPrice(Period.between(startDate, endDate).getDays() * room.getPrice())
                .isReviewCreated(false) // 처음 예약이 생성될 때 리뷰 생성 상태는 false
                .status(Status.ACTIVE) //처음 reservation을 create할 때는 ACTIVE상태이다.
                .build();
    }
}
