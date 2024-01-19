package in.makeus.petspace.repository;

import in.makeus.petspace.domain.Favorite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndRoomId(Long userId, Long roomId);

    @Query("select f from Favorite f " +
            "join fetch f.user u " +
            "join fetch f.room r " +
            "where f.isClicked = true and u.id = :userId and r.address.region = :region " +
            "order by f.id desc")
    Slice<Favorite> findAllFavoritesSliceBy(Long userId, String region, Pageable pageable);
}