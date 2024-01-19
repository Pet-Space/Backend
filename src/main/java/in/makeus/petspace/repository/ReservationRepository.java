package in.makeus.petspace.repository;

import in.makeus.petspace.domain.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findById(Long id);

    @Query("select rs from Reservation rs " +
            "join fetch rs.user " +
            "join fetch rs.room " +
            "where rs.id = :reservationId and rs.user.id = :userId")
    Optional<Reservation> findByIdAndUserId(Long reservationId, Long userId);

    @Query("select rs from Reservation rs " +
            "join fetch rs.user " +
            "join fetch rs.room  ro " +
            "left join fetch rs.review " +
            "where rs.status = 'ACTIVE' " +
            "and rs.user.id = :userId " +
            "and rs.startDate >= current_date " +
            "order by rs.startDate asc ")
    Slice<Reservation> findAllReservationsSliceByStartDateAfter(@Param("userId") Long userId, Pageable pageable);

    @Query("select rs from Reservation rs " +
            "join fetch rs.user " +
            "join fetch rs.room  ro " +
            "left join fetch rs.review " +
            "where rs.status = 'ACTIVE'" +
            "and rs.user.id = :userId " +
            "and rs.startDate < current_date " +
            "order by rs.startDate desc ")
    Slice<Reservation> findAllReservationsSliceByStartDateBefore(@Param("userId") Long userId, Pageable pageable);
}
