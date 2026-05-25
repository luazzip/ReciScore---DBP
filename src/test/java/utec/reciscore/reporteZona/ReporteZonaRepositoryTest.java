package utec.reciscore.reporteZona;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import utec.reciscore.reporteZona.infrastructure.ReporteZonaRepository;
import utec.reciscore.reporteZona.model.ReporteZona;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.Role;
import utec.reciscore.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReporteZonaRepositoryTest {

    @Autowired
    private ReporteZonaRepository reporteZonaRepository;

    @Autowired
    private UserRepository userRepository;

    private User usuario;
    private User otroUsuario;

    @BeforeEach
    void setUp() {
        usuario = new User();
        usuario.setEmail("zona@test.com");
        usuario.setPassword("pass");
        usuario.setName("Usuario Zona");
        usuario.setUsername("usuariozona");
        usuario.setPoints(0);
        usuario.setMultiplier(1.0);
        usuario.setReciclajes(0);
        usuario.setNivel(1);
        usuario.setRachaDias(0);
        usuario.setRole(Role.USER);
        userRepository.save(usuario);

        otroUsuario = new User();
        otroUsuario.setEmail("otro@test.com");
        otroUsuario.setPassword("pass");
        otroUsuario.setName("Otro Usuario");
        otroUsuario.setUsername("otrousuario");
        otroUsuario.setPoints(0);
        otroUsuario.setMultiplier(1.0);
        otroUsuario.setReciclajes(0);
        otroUsuario.setNivel(1);
        otroUsuario.setRachaDias(0);
        otroUsuario.setRole(Role.USER);
        userRepository.save(otroUsuario);

        ReporteZona reporte = new ReporteZona();
        reporte.setLatitude(-12.0464);
        reporte.setLongitude(-77.0428);
        reporte.setDescripcion("Zona sucia cerca al parque");
        reporte.setUser(usuario);
        reporteZonaRepository.save(reporte);
    }

    @Test
    void shouldReturnReportesWhenUserMatches() {
        List<ReporteZona> result = reporteZonaRepository.findByUser(usuario);
        assertEquals(1, result.size());
        assertEquals("Zona sucia cerca al parque", result.get(0).getDescripcion());
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoReportes() {
        List<ReporteZona> result = reporteZonaRepository.findByUser(otroUsuario);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSaveReporteZonaAndAssignId() {
        ReporteZona nuevo = new ReporteZona();
        nuevo.setLatitude(-12.1000);
        nuevo.setLongitude(-77.0500);
        nuevo.setDescripcion("Otra zona");
        nuevo.setUser(usuario);
        ReporteZona saved = reporteZonaRepository.save(nuevo);
        assertNotNull(saved.getId());
    }

    @Test
    void shouldDeleteReporteZonaWhenExists() {
        ReporteZona reporte = reporteZonaRepository.findByUser(usuario).get(0);
        reporteZonaRepository.deleteById(reporte.getId());
        assertTrue(reporteZonaRepository.findByUser(usuario).isEmpty());
    }
}