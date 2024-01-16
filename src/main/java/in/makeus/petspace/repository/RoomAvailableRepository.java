package in.makeus.petspace.repository;

import in.makeus.petspace.domain.Room;
import in.makeus.petspace.domain.RoomAvailable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomAvailableRepository extends JpaRepository<RoomAvailable, Long> {

    List<RoomAvailable> findAllByRoom(Room room);
}
