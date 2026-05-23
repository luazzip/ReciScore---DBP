package utec.reciscore.material.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utec.reciscore.material.model.Material;
import utec.reciscore.material.model.TipoMaterial;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByCategory(TipoMaterial category);
    boolean existsByName(String name);
}