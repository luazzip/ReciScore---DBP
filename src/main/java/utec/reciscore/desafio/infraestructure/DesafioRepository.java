package utec.reciscore.desafio.infraestructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utec.reciscore.desafio.model.Desafio;
import utec.reciscore.desafio.model.UsuarioDesafio;

import java.util.List;
import java.util.Optional;


public interface DesafioRepository extends JpaRepository<Desafio,Long> {
    boolean existsByCategoria(String categoria);

    @Query("SELECT d FROM Desafio d JOIN d.inscripciones i WHERE i.usuario.id = :userId AND i.activo = true")
    List<Desafio> findByUsuarioInscritoId(@Param("userId") Long userId);

    @Query("SELECT i FROM UsuarioDesafio i WHERE i.usuario.id = :userId AND i.desafio.id = :desafioId AND i.activo = true")
    Optional<UsuarioDesafio> findInscripcionByUsuarioAndDesafio(@Param("userId") Long userId, @Param("desafioId") Long desafioId);

    @Query("SELECT i FROM UsuarioDesafio i WHERE i.usuario.id = :userId AND i.activo = true")
    List<UsuarioDesafio> findInscripcionesByUsuarioId(@Param("userId") Long userId);

    @Query("SELECT i FROM UsuarioDesafio i WHERE i.desafio.id = :desafioId")
    List<UsuarioDesafio> findInscripcionesByDesafioId(@Param("desafioId") Long desafioId);
}
