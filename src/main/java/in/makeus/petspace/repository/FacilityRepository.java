package in.makeus.petspace.repository;

import in.makeus.petspace.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    List<Facility> findAll();

}
