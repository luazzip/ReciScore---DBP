package utec.reciscore.reporteZona;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import utec.reciscore.exceptions.DuplicateReportException;
import utec.reciscore.puntoMapa.infrastructure.PuntoMapaRepository;
import utec.reciscore.puntoMapa.model.PuntoMapa;
import utec.reciscore.puntoMapa.model.TipoPunto;
import utec.reciscore.reporteZona.dto.ReporteZonaRequestDTO;
import utec.reciscore.reporteZona.dto.ReporteZonaResponseDTO;
import utec.reciscore.reporteZona.infrastructure.ReporteZonaRepository;
import utec.reciscore.reporteZona.model.ReporteZona;
import utec.reciscore.reporteZona.model.ReporteZonaService;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.Role;
import utec.reciscore.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteZonaServiceTest {
    @Mock
    private ReporteZonaRepository reporteZonaRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PuntoMapaRepository puntoMapaRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReporteZonaService reporteZonaService;

    private User user;
    private ReporteZonaRequestDTO requestDTO;
    private ReporteZona reporteZona;
    private ReporteZonaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@reciscore.com");
        user.setUsername("reciuser");
        user.setName("Reci User");
        user.setRole(Role.USER);
        user.setPoints(0);
        user.setMultiplier(1.0);

        requestDTO = new ReporteZonaRequestDTO();
        requestDTO.setLatitude(-12.1191);
        requestDTO.setLongitude(-77.0308);
        requestDTO.setDescripcion("Zona sucia cerca del parque");

        reporteZona = new ReporteZona();
        reporteZona.setId(1L);
        reporteZona.setLatitude(-12.1191);
        reporteZona.setLongitude(-77.0308);
        reporteZona.setDescripcion("Zona sucia cerca del parque");
        reporteZona.setUser(user);
        reporteZona.setFecha(LocalDateTime.now());
        reporteZona.setProcesado(false);

        responseDTO = new ReporteZonaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setLatitude(-12.1191);
        responseDTO.setLongitude(-77.0308);
        responseDTO.setDescripcion("Zona sucia cerca del parque");
        responseDTO.setUsername("reciuser");
        responseDTO.setFecha(LocalDateTime.now());
        responseDTO.setProcesado(false);
    }


    private void mockAuth(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }


    @Test
    void create_exitoso_retornaResponseDTO() {
        mockAuth(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reporteZonaRepository.findByUser(user)).thenReturn(new ArrayList<>());
        when(modelMapper.map(requestDTO, ReporteZona.class)).thenReturn(reporteZona);
        when(reporteZonaRepository.save(reporteZona)).thenReturn(reporteZona);
        when(reporteZonaRepository.findAll()).thenReturn(List.of(reporteZona));
        when(modelMapper.map(reporteZona, ReporteZonaResponseDTO.class)).thenReturn(responseDTO);

        ReporteZonaResponseDTO result = reporteZonaService.create(requestDTO);

        assertNotNull(result);
        assertEquals("reciuser", result.getUsername());
        verify(reporteZonaRepository).save(reporteZona);
    }

    @Test
    void create_zonaYaReportadaPorElMismoUsuario_lanzaDuplicateReportException() {
        mockAuth(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ReporteZona existente = new ReporteZona();
        existente.setLatitude(-12.1192);   // diferencia < 0.001
        existente.setLongitude(-77.0309);  // diferencia < 0.001
        existente.setUser(user);
        when(reporteZonaRepository.findByUser(user)).thenReturn(List.of(existente));

        assertThrows(DuplicateReportException.class,
                () -> reporteZonaService.create(requestDTO));

        verify(reporteZonaRepository, never()).save(any());
    }

    @Test
    void create_zonaDiferenteDelMismoUsuario_noLanzaExcepcion() {
        mockAuth(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ReporteZona otro = new ReporteZona();
        otro.setLatitude(-13.5000);
        otro.setLongitude(-72.0000);
        otro.setUser(user);
        when(reporteZonaRepository.findByUser(user)).thenReturn(List.of(otro));
        when(modelMapper.map(requestDTO, ReporteZona.class)).thenReturn(reporteZona);
        when(reporteZonaRepository.save(reporteZona)).thenReturn(reporteZona);
        when(reporteZonaRepository.findAll()).thenReturn(List.of(reporteZona));
        when(modelMapper.map(reporteZona, ReporteZonaResponseDTO.class)).thenReturn(responseDTO);

        assertDoesNotThrow(() -> reporteZonaService.create(requestDTO));
    }

    @Test
    void create_limite0_001EsExacto_consideraDuplicado() {
        mockAuth(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ReporteZona existente = new ReporteZona();
        existente.setLatitude(requestDTO.getLatitude() + 0.001);   // diferencia == 0.001
        existente.setLongitude(requestDTO.getLongitude() + 0.001);
        existente.setUser(user);
        when(reporteZonaRepository.findByUser(user)).thenReturn(List.of(existente));
        when(modelMapper.map(requestDTO, ReporteZona.class)).thenReturn(reporteZona);
        when(reporteZonaRepository.save(reporteZona)).thenReturn(reporteZona);
        when(reporteZonaRepository.findAll()).thenReturn(List.of(reporteZona));
        when(modelMapper.map(reporteZona, ReporteZonaResponseDTO.class)).thenReturn(responseDTO);

        assertDoesNotThrow(() -> reporteZonaService.create(requestDTO));
    }

    @Test
    void create_con20ReportesEnMismaZona_creaPuntoMapaZonaSucia() {
        mockAuth(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reporteZonaRepository.findByUser(user)).thenReturn(new ArrayList<>());
        when(modelMapper.map(requestDTO, ReporteZona.class)).thenReturn(reporteZona);
        when(reporteZonaRepository.save(reporteZona)).thenReturn(reporteZona);

        List<ReporteZona> reportes20 = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ReporteZona r = new ReporteZona();
            r.setId((long) i);
            r.setLatitude(-12.1191);
            r.setLongitude(-77.0308);
            r.setProcesado(false);
            r.setUser(user);
            reportes20.add(r);
        }
        when(reporteZonaRepository.findAll()).thenReturn(reportes20);
        when(modelMapper.map(reporteZona, ReporteZonaResponseDTO.class)).thenReturn(responseDTO);

        reporteZonaService.create(requestDTO);

        verify(puntoMapaRepository).save(argThat(pm ->
                pm.getTipo() == TipoPunto.ZONA_SUCIA
                        && pm.getNombre().equals("Zona Crítica")
                        && pm.getLatitude().equals(-12.1191)
                        && pm.getLongitude().equals(-77.0308)
        ));

        assertTrue(reportes20.stream().allMatch(ReporteZona::isProcesado));
        verify(reporteZonaRepository).saveAll(reportes20);
    }

    @Test
    void create_con19ReportesEnMismaZona_noCreaPuntoMapa() {
        mockAuth(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reporteZonaRepository.findByUser(user)).thenReturn(new ArrayList<>());
        when(modelMapper.map(requestDTO, ReporteZona.class)).thenReturn(reporteZona);
        when(reporteZonaRepository.save(reporteZona)).thenReturn(reporteZona);

        // Solo 19 reportes → no debe dispararse la creación del punto
        List<ReporteZona> reportes19 = new ArrayList<>();
        for (int i = 0; i < 19; i++) {
            ReporteZona r = new ReporteZona();
            r.setId((long) i);
            r.setLatitude(-12.1191);
            r.setLongitude(-77.0308);
            r.setProcesado(false);
            r.setUser(user);
            reportes19.add(r);
        }
        when(reporteZonaRepository.findAll()).thenReturn(reportes19);
        when(modelMapper.map(reporteZona, ReporteZonaResponseDTO.class)).thenReturn(responseDTO);

        reporteZonaService.create(requestDTO);

        verify(puntoMapaRepository, never()).save(any());
    }

    @Test
    void create_reportesProcesadosNoContanParaUmbral() {
        mockAuth(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reporteZonaRepository.findByUser(user)).thenReturn(new ArrayList<>());
        when(modelMapper.map(requestDTO, ReporteZona.class)).thenReturn(reporteZona);
        when(reporteZonaRepository.save(reporteZona)).thenReturn(reporteZona);

        List<ReporteZona> reportesMixtos = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            ReporteZona r = new ReporteZona();
            r.setId((long) i);
            r.setLatitude(-12.1191);
            r.setLongitude(-77.0308);
            r.setProcesado(i < 10); // los primeros 10 ya procesados
            r.setUser(user);
            reportesMixtos.add(r);
        }
        when(reporteZonaRepository.findAll()).thenReturn(reportesMixtos);
        when(modelMapper.map(reporteZona, ReporteZonaResponseDTO.class)).thenReturn(responseDTO);

        reporteZonaService.create(requestDTO);

        verify(puntoMapaRepository, never()).save(any());
    }

    @Test
    void create_usuarioNoEncontrado_lanzaExcepcion() {
        mockAuth("noexiste@reciscore.com");
        when(userRepository.findByEmail("noexiste@reciscore.com"))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> reporteZonaService.create(requestDTO));
        verify(reporteZonaRepository, never()).save(any());
    }


    @Test
    void getAll_retornaListaConUsername() {
        when(reporteZonaRepository.findAll()).thenReturn(List.of(reporteZona));
        when(modelMapper.map(reporteZona, ReporteZonaResponseDTO.class))
                .thenReturn(responseDTO);

        List<ReporteZonaResponseDTO> result = reporteZonaService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("reciuser", result.get(0).getUsername());
    }

    @Test
    void getAll_sinReportes_retornaListaVacia() {
        when(reporteZonaRepository.findAll()).thenReturn(new ArrayList<>());

        List<ReporteZonaResponseDTO> result = reporteZonaService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_variosReportes_retornaTodos() {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("reciuser2");

        ReporteZona reporte2 = new ReporteZona();
        reporte2.setId(2L);
        reporte2.setLatitude(-11.0);
        reporte2.setLongitude(-76.0);
        reporte2.setUser(user2);

        ReporteZonaResponseDTO response2 = new ReporteZonaResponseDTO();
        response2.setId(2L);
        response2.setUsername("reciuser2");

        when(reporteZonaRepository.findAll()).thenReturn(List.of(reporteZona, reporte2));
        when(modelMapper.map(reporteZona, ReporteZonaResponseDTO.class)).thenReturn(responseDTO);
        when(modelMapper.map(reporte2, ReporteZonaResponseDTO.class)).thenReturn(response2);

        List<ReporteZonaResponseDTO> result = reporteZonaService.getAll();

        assertEquals(2, result.size());
        assertEquals("reciuser", result.get(0).getUsername());
        assertEquals("reciuser2", result.get(1).getUsername());
    }
}