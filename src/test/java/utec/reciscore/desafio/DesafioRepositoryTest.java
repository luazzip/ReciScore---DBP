package utec.reciscore.desafio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import utec.reciscore.desafio.infraestructure.DesafioRepository;
import utec.reciscore.desafio.model.Desafio;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DesafioRepositoryTest {

    @Autowired
    private DesafioRepository desafioRepository;

    @BeforeEach
    void setUp() {
        Desafio desafio = new Desafio();
        desafio.setTitulo("Recicla 10 botellas");
        desafio.setDescripcion("Recicla 10 botellas de plástico esta semana");
        desafio.setCategoria("PLASTICO");
        desafio.setMeta_valor(10);
        desafio.setPuntos(50);
        desafio.setFecha_inicio(LocalDateTime.now());
        desafio.setFecha_fin(LocalDateTime.now().plusDays(7));
        desafio.setActivo(true);
        desafioRepository.save(desafio);
    }

    @Test
    void shouldReturnTrueWhenCategoriaExists() {
        boolean exists = desafioRepository.existsByCategoria("PLASTICO");
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenCategoriaDoesNotExist() {
        boolean exists = desafioRepository.existsByCategoria("METAL");
        assertFalse(exists);
    }

    @Test
    void shouldSaveDesafioAndReturnWithId() {
        Desafio desafio = new Desafio();
        desafio.setTitulo("Recicla papel");
        desafio.setDescripcion("Recicla 5 hojas de papel");
        desafio.setCategoria("PAPEL");
        desafio.setMeta_valor(5);
        desafio.setPuntos(20);
        desafio.setFecha_inicio(LocalDateTime.now());
        desafio.setFecha_fin(LocalDateTime.now().plusDays(3));
        desafio.setActivo(true);
        Desafio saved = desafioRepository.save(desafio);
        assertNotNull(saved.getId());
    }

    @Test
    void shouldReturnAllDesafiosWhenFindAll() {
        var all = desafioRepository.findAll();
        assertFalse(all.isEmpty());
    }

    @Test
    void shouldDeleteDesafioWhenExists() {
        Desafio desafio = desafioRepository.findAll().get(0);
        desafioRepository.deleteById(desafio.getId());
        assertFalse(desafioRepository.findById(desafio.getId()).isPresent());
    }
}