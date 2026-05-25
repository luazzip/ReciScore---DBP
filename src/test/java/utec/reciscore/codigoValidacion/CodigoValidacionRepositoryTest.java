package utec.reciscore.codigoValidacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import utec.reciscore.codigoValidacion.infrastructure.CodigoValidacionRepository;
import utec.reciscore.codigoValidacion.model.CodigoValidacion;
import utec.reciscore.puntoMapa.infrastructure.PuntoMapaRepository;
import utec.reciscore.puntoMapa.model.PuntoMapa;
import utec.reciscore.puntoMapa.model.TipoPunto;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.Role;
import utec.reciscore.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CodigoValidacionRepositoryTest {

    @Autowired
    private CodigoValidacionRepository codigoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PuntoMapaRepository puntoMapaRepository;

    private User usuario;
    private PuntoMapa punto;

    @BeforeEach
    void setUp() {
        usuario = new User();
        usuario.setEmail("codigo@test.com");
        usuario.setPassword("pass");
        usuario.setName("Test Codigo");
        usuario.setUsername("testcodigo");
        usuario.setPoints(0);
        usuario.setMultiplier(1.0);
        usuario.setReciclajes(0);
        usuario.setNivel(1);
        usuario.setRachaDias(0);
        usuario.setRole(Role.USER);
        userRepository.save(usuario);

        punto = new PuntoMapa();
        punto.setLatitude(-12.1191);
        punto.setLongitude(-77.0308);
        punto.setNombre("Punto Test");
        punto.setTipo(TipoPunto.ACOPIO_OFICIAL);
        puntoMapaRepository.save(punto);

        CodigoValidacion codigo = new CodigoValidacion();
        codigo.setCodigo("ABC123");
        codigo.setUsuario(usuario);
        codigo.setPuntoMapa(punto);
        codigo.setUsado(false);
        codigoRepository.save(codigo);
    }

    @Test
    void shouldReturnCodigoWhenCodigoStringMatches() {
        Optional<CodigoValidacion> result = codigoRepository.findByCodigo("ABC123");
        assertTrue(result.isPresent());
        assertEquals("ABC123", result.get().getCodigo());
    }

    @Test
    void shouldReturnEmptyWhenCodigoDoesNotExist() {
        Optional<CodigoValidacion> result = codigoRepository.findByCodigo("NOEXISTE");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldSaveCodigoAndAssignFechaExpiracion() {
        CodigoValidacion codigo = codigoRepository.findByCodigo("ABC123").orElseThrow();
        assertNotNull(codigo.getFechaExpiracion());
    }

    @Test
    void shouldReturnFalseForUsadoWhenNewlySaved() {
        CodigoValidacion codigo = codigoRepository.findByCodigo("ABC123").orElseThrow();
        assertFalse(codigo.getUsado());
    }

    @Test
    void shouldUpdateUsadoWhenMarkedAsUsed() {
        CodigoValidacion codigo = codigoRepository.findByCodigo("ABC123").orElseThrow();
        codigo.setUsado(true);
        codigoRepository.save(codigo);
        CodigoValidacion updated = codigoRepository.findByCodigo("ABC123").orElseThrow();
        assertTrue(updated.getUsado());
    }
}