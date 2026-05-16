package utec.reciscore.reporteReciclaje.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utec.reciscore.reporteReciclaje.model.ReporteReciclaje;

import java.util.List;

@Repository
public interface ReporteReciclajeRepository extends JpaRepository<ReporteReciclaje, Long> {
    List<ReporteReciclaje> findByUsuarioId(Long userId);
    List<ReporteReciclaje> findByValidadoIaTrue();
    List<ReporteReciclaje> findByUsuarioIdAndValidadoIaTrue(Long userId);
}