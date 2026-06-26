package utec.reciscore.desafio.infraestructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utec.reciscore.desafio.model.Desafio;

import java.util.List;

public interface DesafioRepository extends JpaRepository<Desafio,Long> {
    boolean existsByCategoria(String categoria);

    @Query("SELECT d FROM Desafio d JOIN d.usuariosInscritos u WHERE u.id = :userId")
    List<Desafio> findByUsuariosInscritosId(@Param("userId") Long userId);
}
