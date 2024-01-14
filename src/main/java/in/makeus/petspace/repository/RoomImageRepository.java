package in.makeus.petspace.repository;

import in.makeus.petspace.domain.image.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
}
