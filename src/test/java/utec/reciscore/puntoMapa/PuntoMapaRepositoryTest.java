package utec.reciscore.puntoMapa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import utec.reciscore.puntoMapa.infrastructure.PuntoMapaRepository;
import utec.reciscore.puntoMapa.model.PuntoMapa;
import utec.reciscore.puntoMapa.model.TipoPunto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PuntoMapaRepositoryTest {
    @Autowired
    private PuntoMapaRepository puntoMapaRepository;

    private PuntoMapa punto;

    @BeforeEach
    void setUp() {
        punto = new PuntoMapa();
        punto.setLatitude(-12.1191);
        punto.setLongitude(-77.0308);
        punto.setNombre("Punto Kennedy");
        punto.setTipo(TipoPunto.ACOPIO_OFICIAL);

        puntoMapaRepository.save(punto);
    }

    @Test
    void shouldReturnNearbyPointsWhenCoordinatesAreClose() {
        List<PuntoMapa> resultado =
                puntoMapaRepository.findPuntosEnRadio(-12.1192, -77.0309, 50);

        assertFalse(resultado.isEmpty());
        assertEquals("Punto Kennedy", resultado.get(0).getNombre());
    }

    @Test
    void shouldReturnEmptyListWhenPointIsFarAway() {
        List<PuntoMapa> resultado =
                puntoMapaRepository.findPuntosEnRadio(-12.1500, -77.0100, 50);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void shouldReturnPointWhenCoordinatesMatchExactly() {
        List<PuntoMapa> resultado =
                puntoMapaRepository.findPuntosEnRadio(-12.1191, -77.0308, 50);

        assertEquals(1, resultado.size());
    }
}