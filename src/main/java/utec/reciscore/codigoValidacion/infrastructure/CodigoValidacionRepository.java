package utec.reciscore.codigoValidacion.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import utec.reciscore.codigoValidacion.model.CodigoValidacion;

import java.util.Optional;

public interface CodigoValidacionRepository extends JpaRepository<CodigoValidacion, Long> {
    Optional<CodigoValidacion> findByCodigo(String codigo);
}