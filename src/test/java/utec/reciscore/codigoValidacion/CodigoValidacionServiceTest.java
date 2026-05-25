package utec.reciscore.codigoValidacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utec.reciscore.codigoValidacion.dto.CodigoValidacionRequestDTO;
import utec.reciscore.codigoValidacion.dto.CodigoValidacionResponseDTO;
import utec.reciscore.codigoValidacion.infrastructure.CodigoValidacionRepository;
import utec.reciscore.codigoValidacion.model.CodigoValidacion;
import utec.reciscore.codigoValidacion.model.CodigoValidacionService;
import utec.reciscore.puntoMapa.infrastructure.PuntoMapaRepository;
import utec.reciscore.puntoMapa.model.PuntoMapa;
import utec.reciscore.puntoMapa.model.TipoPunto;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.User;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodigoValidacionServiceTest {
    @Mock
    private CodigoValidacionRepository codigoRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PuntoMapaRepository puntoMapaRepository;

    @InjectMocks
    private CodigoValidacionService codigoService;

    private User user;
    private PuntoMapa puntoMapa;
    private CodigoValidacion codigo;
    private CodigoValidacionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("luis@gmail.com");

        puntoMapa = new PuntoMapa();
        puntoMapa.setId(1L);
        puntoMapa.setLatitude(-12.1191);
        puntoMapa.setLongitude(-77.0308);
        puntoMapa.setNombre("Punto Kennedy");
        puntoMapa.setTipo(TipoPunto.ACOPIO_OFICIAL);

        codigo = new CodigoValidacion();
        codigo.setId(1L);
        codigo.setCodigo("A3F9B2C1");
        codigo.setUsuario(user);
        codigo.setPuntoMapa(puntoMapa);
        codigo.setFechaExpiracion(LocalDateTime.now().plusMinutes(10));
        codigo.setUsado(false);

        requestDTO = new CodigoValidacionRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setPuntoMapaId(1L);
    }

    @Test
    void generarCodigo_exitoso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(puntoMapaRepository.findById(1L)).thenReturn(Optional.of(puntoMapa));
        when(codigoRepository.save(any())).thenReturn(codigo);

        CodigoValidacionResponseDTO response = codigoService.generarCodigo(requestDTO);

        assertNotNull(response);
        assertNotNull(response.getCodigo());
        assertEquals(1L, response.getUserId());
        assertEquals(1L, response.getPuntoMapaId());
        assertFalse(response.getUsado());
        verify(codigoRepository).save(any());
    }

    @Test
    void generarCodigo_usuarioNoExiste_lanzaExcepcion() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> codigoService.generarCodigo(requestDTO));

        verify(codigoRepository, never()).save(any());
    }

    @Test
    void generarCodigo_puntoMapaNoExiste_lanzaExcepcion() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(puntoMapaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> codigoService.generarCodigo(requestDTO));

        verify(codigoRepository, never()).save(any());
    }

    @Test
    void verificarCodigo_exitoso() {
        when(codigoRepository.findByCodigo("A3F9B2C1")).thenReturn(Optional.of(codigo));
        when(codigoRepository.save(any())).thenReturn(codigo);

        boolean resultado = codigoService.verificarCodigo("A3F9B2C1");

        assertTrue(resultado);
        assertTrue(codigo.getUsado());
        verify(codigoRepository).save(codigo);
    }

    @Test
    void verificarCodigo_yaUsado_lanzaExcepcion() {
        codigo.setUsado(true);
        when(codigoRepository.findByCodigo("A3F9B2C1")).thenReturn(Optional.of(codigo));

        assertThrows(IllegalArgumentException.class,
                () -> codigoService.verificarCodigo("A3F9B2C1"));

        verify(codigoRepository, never()).save(any());
    }

    @Test
    void verificarCodigo_expirado_lanzaExcepcion() {
        codigo.setFechaExpiracion(LocalDateTime.now().minusMinutes(1));
        when(codigoRepository.findByCodigo("A3F9B2C1")).thenReturn(Optional.of(codigo));

        assertThrows(IllegalArgumentException.class,
                () -> codigoService.verificarCodigo("A3F9B2C1"));

        verify(codigoRepository, never()).save(any());
    }

    @Test
    void verificarCodigo_noExiste_lanzaExcepcion() {
        when(codigoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> codigoService.verificarCodigo("NOEXISTE"));
    }
}