package utec.reciscore.reporteZona.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import utec.reciscore.reporteZona.model.ReporteZona;
import utec.reciscore.user.model.User;

import java.util.List;

public interface ReporteZonaRepository extends JpaRepository<ReporteZona, Long> {
    List<ReporteZona> findByUser(User user);
}
