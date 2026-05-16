package utec.reciscore.Material.infraestructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utec.reciscore.Material.model.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}
