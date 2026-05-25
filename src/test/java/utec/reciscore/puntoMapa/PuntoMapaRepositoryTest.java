package utec.reciscore.puntoMapa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import utec.reciscore.puntoMapa.infrastructure.PuntoMapaRepository;
import utec.reciscore.puntoMapa.model.PuntoMapa;
import utec.reciscore.puntoMapa.model.TipoPunto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PuntoMapaRepositoryTest {

    @Autowired
    private PuntoMapaRepository puntoMapaRepository;

    @BeforeEach
    void setUp() {
        PuntoMapa punto = new PuntoMapa();
        punto.setLatitude(-12.1191);
        punto.setLongitude(-77.0308);
        punto.setNombre("Punto Kennedy");
        punto.setTipo(TipoPunto.ACOPIO_OFICIAL);
        puntoMapaRepository.save(punto);
    }

    @Test
    void findPuntosEnRadio_hayPuntosCercanos_retornaLista() {
        double lat = -12.1192;
        double lng = -77.0309;

        List<PuntoMapa> resultado = puntoMapaRepository.findPuntosEnRadio(lat, lng, 50);

        assertFalse(resultado.isEmpty());
        assertEquals("Punto Kennedy", resultado.get(0).getNombre());
    }

    @Test
    void findPuntosEnRadio_puntoMuyLejos_retornaVacio() {
        double lat = -12.1500;
        double lng = -77.0100;

        List<PuntoMapa> resultado = puntoMapaRepository.findPuntosEnRadio(lat, lng, 50);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void findPuntosEnRadio_mismasCoordenadas_retornaLista() {
        double lat = -12.1191;
        double lng = -77.0308;

        List<PuntoMapa> resultado = puntoMapaRepository.findPuntosEnRadio(lat, lng, 50);

        assertEquals(1, resultado.size());
    }
}