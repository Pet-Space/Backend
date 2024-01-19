package in.makeus.petspace.repository;

import in.makeus.petspace.domain.RoomCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomCategoryRepository extends JpaRepository<RoomCategory, Long> {
}
