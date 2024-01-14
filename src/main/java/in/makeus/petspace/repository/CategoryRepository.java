package in.makeus.petspace.repository;

import in.makeus.petspace.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CategoryRepository extends JpaRepository<Category, Long>{

        List<Category> findAll();

}
