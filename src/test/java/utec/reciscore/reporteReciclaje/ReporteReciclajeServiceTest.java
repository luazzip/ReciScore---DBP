package utec.reciscore.reporteReciclaje;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import utec.reciscore.ia.IaClient;
import utec.reciscore.ia.IaResponse;
import utec.reciscore.material.infrastructure.MaterialRepository;
import utec.reciscore.material.model.Material;
import utec.reciscore.material.model.TipoMaterial;
import utec.reciscore.puntoMapa.model.PuntoMapaService;
import utec.reciscore.reporteReciclaje.dto.ReporteReciclajeRequestDTO;
import utec.reciscore.reporteReciclaje.dto.ReporteReciclajeResponseDTO;
import utec.reciscore.reporteReciclaje.infrastructure.ReporteReciclajeRepository;
import utec.reciscore.reporteReciclaje.model.ReporteReciclaje;
import utec.reciscore.reporteReciclaje.model.ReporteReciclajeService;
import utec.reciscore.reporteReciclaje.model.TamanoObjeto;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteReciclajeServiceTest {
    @Mock
    private ReporteReciclajeRepository reporteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MaterialRepository materialRepository;
    @Mock
    private PuntoMapaService puntoMapaService;
    @Mock
    private IaClient iaClient;
    @InjectMocks
    private ReporteReciclajeService reporteService;

    private User user;
    private Material material;
    private ReporteReciclaje reporte;
    private ReporteReciclajeRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("luis@gmail.com");
        user.setName("Luis Nieva");
        user.setPoints(0);
        user.setMultiplier(1.0);

        material = Material.builder()
                .id(1L)
                .name("Botella PET")
                .pointsPerKg(10.0)
                .weight(0.5)
                .category(TipoMaterial.PLASTICO)
                .recyclable(true)
                .build();

        reporte = new ReporteReciclaje();
        reporte.setNumeroReporte(1L);
        reporte.setUsuario(user);
        reporte.setMaterial(material);
        reporte.setFotoUrl("http://foto.com/foto.jpg");
        reporte.setTamanoObjeto(TamanoObjeto.PEQUENO);
        reporte.setNumeroArticulos(3);
        reporte.setMaterialDetectadoIa(true);
        reporte.setConfianzaIa(0.95);
        reporte.setValidadoIa(true);
        reporte.setGpsValidado(true);
        reporte.setFecha(LocalDateTime.now());

        requestDTO = new ReporteReciclajeRequestDTO();
        requestDTO.setMaterialId(1L);
        requestDTO.setFotoUrl("http://foto.com/foto.jpg");
        requestDTO.setTamanoObjeto(TamanoObjeto.PEQUENO);
        requestDTO.setNumeroArticulos(3);
        requestDTO.setLatitud(-12.1191);
        requestDTO.setLongitud(-77.0308);

        IaResponse iaResponse = new IaResponse();
        iaResponse.setTipoMaterial("PLASTICO");
        iaResponse.setConfidence(0.95);
        iaResponse.setRecyclable(true);
        lenient().when(iaClient.classify(anyString())).thenReturn(iaResponse);
    }

    private void mockAuth(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void shouldAddPointsWhenGpsAndIaAreValid() {
        mockAuth(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(puntoMapaService.estaEnZonaValida(anyDouble(), anyDouble())).thenReturn(true);
        when(reporteRepository.save(any())).thenReturn(reporte);

        ReporteReciclajeResponseDTO response = reporteService.crear(requestDTO);

        assertNotNull(response);
        assertTrue(response.getGpsValidado());
        assertTrue(user.getPoints() > 0);
        verify(userRepository).save(user);
    }

    @Test
    void shouldNotAddPointsWhenGpsIsInvalid() {
        mockAuth(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(puntoMapaService.estaEnZonaValida(anyDouble(), anyDouble())).thenReturn(false);
        when(reporteRepository.save(any())).thenReturn(reporte);

        ReporteReciclajeResponseDTO response = reporteService.crear(requestDTO);

        assertNotNull(response);
        assertEquals(0, user.getPoints());
        verify(userRepository, never()).save(user);
    }

    @Test
    void shouldNotAddPointsWhenIaValidationFails() {
        mockAuth(user.getEmail());
        reporte.setMaterialDetectadoIa(false);
        reporte.setValidadoIa(false);
        IaResponse iaFail = new IaResponse();
        iaFail.setTipoMaterial("VIDRIO");
        iaFail.setConfidence(0.40);
        iaFail.setRecyclable(true);
        lenient().when(iaClient.classify(anyString())).thenReturn(iaFail);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(puntoMapaService.estaEnZonaValida(anyDouble(), anyDouble())).thenReturn(true);
        when(reporteRepository.save(any())).thenReturn(reporte);

        ReporteReciclajeResponseDTO response = reporteService.crear(requestDTO);

        assertNotNull(response);
        assertEquals(0, user.getPoints());
        verify(userRepository, never()).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        mockAuth("noexiste@reciscore.com");
        when(userRepository.findByEmail("noexiste@reciscore.com")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> reporteService.crear(requestDTO));

        verify(reporteRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenMaterialDoesNotExist() {
        mockAuth(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(materialRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> reporteService.crear(requestDTO));

        verify(reporteRepository, never()).save(any());
    }

    @Test
    void shouldApplyMultiplierWhenCalculatingPoints() {
        mockAuth(user.getEmail());
        user.setMultiplier(2.0);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(puntoMapaService.estaEnZonaValida(anyDouble(), anyDouble())).thenReturn(true);
        when(reporteRepository.save(any())).thenReturn(reporte);

        reporteService.crear(requestDTO);

        assertEquals(60, user.getPoints());
    }

    @Test
    void shouldReturnHistoryWhenUserHasReportes() {
        when(reporteRepository.findByUsuarioId(1L)).thenReturn(List.of(reporte));

        List<ReporteReciclajeResponseDTO> response = reporteService.obtenerPorUsuario(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }
}