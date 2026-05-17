package utec.reciscore.desafio.infraestructure;

import org.springframework.data.jpa.repository.JpaRepository;
import utec.reciscore.desafio.model.Desafio;

public interface DesafioRepository extends JpaRepository<Desafio,Long> {
    boolean existsByCategoria(String categoria);
}
