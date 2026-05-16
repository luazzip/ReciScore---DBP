package utec.reciscore.PuntoMapa.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utec.reciscore.PuntoMapa.model.PuntoMapa;

import java.util.List;

public interface PuntoMapaRepository extends JpaRepository<PuntoMapa,Long> {
    @Query(value = """
        SELECT p FROM PuntoMapa p
        WHERE (6371000 * acos(
            cos(radians(:lat)) * cos(radians(p.latitude)) *
            cos(radians(p.longitude) - radians(:lng)) +
            sin(radians(:lat)) * sin(radians(p.latitude))
        )) <= :radioMetros
        """, nativeQuery = true)
    List<PuntoMapa> findPuntosEnRadio(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radioMetros") double radioMetros
    );
}
