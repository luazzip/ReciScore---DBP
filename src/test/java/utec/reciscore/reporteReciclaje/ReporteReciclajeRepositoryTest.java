package utec.reciscore.reporteReciclaje;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import utec.reciscore.material.infrastructure.MaterialRepository;
import utec.reciscore.material.model.Material;
import utec.reciscore.material.model.TipoMaterial;
import utec.reciscore.reporteReciclaje.infrastructure.ReporteReciclajeRepository;
import utec.reciscore.reporteReciclaje.model.ReporteReciclaje;
import utec.reciscore.reporteReciclaje.model.TamanoObjeto;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.Role;
import utec.reciscore.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReporteReciclajeRepositoryTest {

    @Autowired
    private ReporteReciclajeRepository reporteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MaterialRepository materialRepository;

    private User usuario;
    private Material material;

    @BeforeEach
    void setUp() {
        usuario = new User();
        usuario.setEmail("user@test.com");
        usuario.setPassword("pass");
        usuario.setName("Test User");
        usuario.setUsername("testuser");
        usuario.setPoints(0);
        usuario.setMultiplier(1.0);
        usuario.setReciclajes(0);
        usuario.setNivel(1);
        usuario.setRachaDias(0);
        usuario.setRole(Role.USER);
        userRepository.save(usuario);

        material = Material.builder()
                .name("Lata")
                .pointsPerKg(8.0)
                .weight(0.2)
                .category(TipoMaterial.METAL)
                .recyclable(true)
                .build();
        materialRepository.save(material);

        ReporteReciclaje reporte1 = new ReporteReciclaje();
        reporte1.setUsuario(usuario);
        reporte1.setMaterial(material);
        reporte1.setFotoUrl("http://foto1.jpg");
        reporte1.setTamanoObjeto(TamanoObjeto.PEQUENO);
        reporte1.setNumeroArticulos(2);
        reporte1.setMaterialDetectadoIa(true);
        reporte1.setConfianzaIa(0.95);
        reporte1.setValidadoIa(true);
        reporte1.setGpsValidado(true);
        reporteRepository.save(reporte1);

        ReporteReciclaje reporte2 = new ReporteReciclaje();
        reporte2.setUsuario(usuario);
        reporte2.setMaterial(material);
        reporte2.setFotoUrl("http://foto2.jpg");
        reporte2.setTamanoObjeto(TamanoObjeto.GRANDE);
        reporte2.setNumeroArticulos(5);
        reporte2.setMaterialDetectadoIa(false);
        reporte2.setConfianzaIa(0.40);
        reporte2.setValidadoIa(false);
        reporte2.setGpsValidado(false);
        reporteRepository.save(reporte2);
    }

    @Test
    void shouldReturnReportesWhenUsuarioIdMatches() {
        List<ReporteReciclaje> result = reporteRepository.findByUsuarioId(usuario.getId());
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyWhenUsuarioHasNoReportes() {
        List<ReporteReciclaje> result = reporteRepository.findByUsuarioId(999L);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnOnlyValidatedReportesWhenFilteredByIa() {
        List<ReporteReciclaje> result = reporteRepository.findByValidadoIaTrue();
        assertEquals(1, result.size());
        assertTrue(result.get(0).getValidadoIa());
    }

    @Test
    void shouldReturnValidatedReportesForSpecificUser() {
        List<ReporteReciclaje> result = reporteRepository.findByUsuarioIdAndValidadoIaTrue(usuario.getId());
        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoValidatedReportes() {
        List<ReporteReciclaje> result = reporteRepository.findByUsuarioIdAndValidadoIaTrue(999L);
        assertTrue(result.isEmpty());
    }
}