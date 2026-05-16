package utec.reciscore.material.infraestructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utec.reciscore.material.model.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}
