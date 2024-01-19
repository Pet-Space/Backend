package in.makeus.petspace.service;

import in.makeus.petspace.domain.Reservation;
import in.makeus.petspace.domain.Room;
import in.makeus.petspace.domain.RoomAvailable;
import in.makeus.petspace.domain.Status;
import in.makeus.petspace.domain.user.User;
import in.makeus.petspace.dto.reservation.ReservationCreateRequestDto;
import in.makeus.petspace.dto.reservation.ReservationCreateResponseDto;
import in.makeus.petspace.dto.reservation.ReservationDeleteResponseDto;
import in.makeus.petspace.dto.reservation.ReservationReadResponseDto;
import in.makeus.petspace.dto.reservation.ReservationSliceResponseDto;
import in.makeus.petspace.repository.ReservationRepository;
import in.makeus.petspace.repository.RoomRepository;
import in.makeus.petspace.repository.UserRepository;
import in.makeus.petspace.util.exception.ReservationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static in.makeus.petspace.util.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReservationCreateResponseDto saveReservation(Long userId, Long roomId, ReservationCreateRequestDto dto) {
        //엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ReservationException(GET_RESERVATION_EMPTY_USER));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ReservationException(GET_RESERVATION_EMPTY_ROOM));

        //validation
        if(dto.getTotalGuest() > room.getMaxGuest()) {
            throw new ReservationException(POST_RESERVATION_OVERCAPACITY_TOTAL_GUEST);
        }
        if(dto.getTotalPet() > room.getMaxPet()) {
            throw new ReservationException(POST_RESERVATION_OVERCAPACITY_TOTAL_PET);
        }


//        if(LocalDate.parse(dto.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE).isBefore(LocalDate.now())) {
//            throw  new ReservationException((POST_RESERVATION_INVALID_STARTDATE));
//        }

        //Reservation 생성
        Reservation reservation = dto.toEntity(user, room);

        //startDate부터 endDate까지 Room의 RoomAvailable을 INACTIVE로 변경
        LocalDate startDate = reservation.getStartDate().toLocalDate();
        LocalDate endDate = reservation.getEndDate().toLocalDate();

        List<RoomAvailable> roomAvailables = reservation.getRoom().getRoomAvailables().stream()
                .filter(roomAvailable -> roomAvailable.getAvailableDay().compareTo(startDate) >= 0)
                .filter(roomAvailable -> roomAvailable.getAvailableDay().isBefore(endDate))
                .collect(Collectors.toList());

        if(roomAvailables.isEmpty()) {
            throw new ReservationException(POST_RESERVATION_INVALID_ROOM_AVAILABLE_DATE);
        }

        for(RoomAvailable roomAvailable : roomAvailables) {
            if(roomAvailable.getStatus() != Status.ACTIVE) {
                throw new ReservationException(POST_RESERVATION_INVALID_ROOM_AVAILABLE_STATUS);
            }
            roomAvailable.changeStatus(Status.INACTIVE);
        }

        reservationRepository.save(reservation);

        return new ReservationCreateResponseDto(reservation);
    }

    public List<ReservationReadResponseDto> readUpComingReservation(Long userId) {

        //엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ReservationException(GET_RESERVATION_EMPTY_USER));

        return user.getReservations().stream()
                .filter(r -> r.getStartDate().toLocalDate().compareTo(LocalDate.now()) >= 0) //예약이 현재보다 나중에 있으면 ture
                .filter(r -> r.getStatus() == Status.ACTIVE) //예약의 Status가 ACTIVE이면 true
                .map(ReservationReadResponseDto :: new)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<ReservationReadResponseDto> readTerminateReservation(Long userId) {

        //엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ReservationException(GET_RESERVATION_EMPTY_USER));

        return user.getReservations().stream()
                .filter(r -> r.getStartDate().toLocalDate().compareTo(LocalDate.now()) < 0) //예약이 현재보다 나중에 있으면 ture
                .filter(r -> r.getStatus() == Status.ACTIVE) //예약의 Status가 ACTIVE이면 true
                .map(ReservationReadResponseDto :: new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationDeleteResponseDto deleteReservation(Long userId, Long reservationId) {
        //엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ReservationException(GET_RESERVATION_EMPTY_USER));
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(NONE_RESERVATION));

        //validation
        if(userId != reservation.getUser().getId()) {
            throw new ReservationException(PATCH_RESERVATION_INVALID_USER);
        }
        ReservationDeleteResponseDto dto = new ReservationDeleteResponseDto(reservation);
        reservation.deleteReservation();

        return dto;
    }

    @Transactional(readOnly = true)
    public ReservationSliceResponseDto findAllUpcomingReservationByPage(Long userId, Pageable pageable) {
        Slice<Reservation> allReservationsSliceBy = reservationRepository.findAllReservationsSliceByStartDateAfter(userId, pageable);

        List<ReservationReadResponseDto> reservations = allReservationsSliceBy.getContent().stream()
                .map((ReservationReadResponseDto::new))
                .collect(Collectors.toList());

        return ReservationSliceResponseDto.builder()
                .reservations(reservations)
                .page(allReservationsSliceBy.getPageable().getPageNumber())
                .isLast(allReservationsSliceBy.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ReservationSliceResponseDto findAllTerminateReservationByPage(Long userId, Pageable pageable) {
        Slice<Reservation> allReservationsSliceBy = reservationRepository.findAllReservationsSliceByStartDateBefore(userId, pageable);

        List<ReservationReadResponseDto> reservations = allReservationsSliceBy.getContent().stream()
                .map((ReservationReadResponseDto::new))
                .collect(Collectors.toList());

        return ReservationSliceResponseDto.builder()
                .reservations(reservations)
                .page(allReservationsSliceBy.getPageable().getPageNumber())
                .isLast(allReservationsSliceBy.isLast())
                .build();
    }
}
