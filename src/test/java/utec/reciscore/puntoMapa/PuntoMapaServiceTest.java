package utec.reciscore.puntoMapa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import utec.reciscore.puntoMapa.dto.PuntoMapaRequestDTO;
import utec.reciscore.puntoMapa.dto.PuntoMapaResponseDTO;
import utec.reciscore.puntoMapa.infrastructure.PuntoMapaRepository;
import utec.reciscore.puntoMapa.model.PuntoMapa;
import utec.reciscore.puntoMapa.model.PuntoMapaService;
import utec.reciscore.puntoMapa.model.TipoPunto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PuntoMapaServiceTest {

    @Mock
    private PuntoMapaRepository puntoMapaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PuntoMapaService puntoMapaService;

    private PuntoMapa puntoMapa;
    private PuntoMapaRequestDTO requestDTO;
    private PuntoMapaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        puntoMapa = new PuntoMapa();
        puntoMapa.setId(1L);
        puntoMapa.setLatitude(-12.1191);
        puntoMapa.setLongitude(-77.0308);
        puntoMapa.setNombre("Punto Kennedy");
        puntoMapa.setTipo(TipoPunto.ACOPIO_OFICIAL);

        requestDTO = new PuntoMapaRequestDTO();
        requestDTO.setLatitude(-12.1191);
        requestDTO.setLongitude(-77.0308);
        requestDTO.setNombre("Punto Kennedy");
        requestDTO.setTipo(TipoPunto.ACOPIO_OFICIAL);

        responseDTO = new PuntoMapaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setLatitude(-12.1191);
        responseDTO.setLongitude(-77.0308);
        responseDTO.setNombre("Punto Kennedy");
        responseDTO.setTipo(TipoPunto.ACOPIO_OFICIAL);
    }

    @Test
    void crear_exitoso() {
        when(puntoMapaRepository.save(any())).thenReturn(puntoMapa);

        PuntoMapaResponseDTO response = puntoMapaService.crear(requestDTO);

        assertNotNull(response);
        assertEquals(-12.1191, response.getLatitude());
        assertEquals("Punto Kennedy", response.getNombre());
        verify(puntoMapaRepository).save(any());
    }

    @Test
    void obtenerTodos_retornaLista() {
        when(puntoMapaRepository.findAll()).thenReturn(List.of(puntoMapa));

        List<PuntoMapaResponseDTO> response = puntoMapaService.obtenerTodos();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void obtenerTodos_listaVacia_lanzaExcepcion() {
        when(puntoMapaRepository.findAll()).thenReturn(List.of());

        assertThrows(NoSuchElementException.class,
                () -> puntoMapaService.obtenerTodos());
    }

    @Test
    void buscarPorId_exitoso() {
        when(puntoMapaRepository.findById(1L)).thenReturn(Optional.of(puntoMapa));
        when(modelMapper.map(puntoMapa, PuntoMapaResponseDTO.class)).thenReturn(responseDTO);

        PuntoMapaResponseDTO response = puntoMapaService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(puntoMapaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> puntoMapaService.buscarPorId(99L));
    }

    @Test
    void estaEnZonaValida_hayPuntosCercanos_retornaTrue() {
        when(puntoMapaRepository.findPuntosEnRadio(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of(puntoMapa));

        boolean resultado = puntoMapaService.estaEnZonaValida(-12.1191, -77.0308);

        assertTrue(resultado);
    }

    @Test
    void estaEnZonaValida_noPuntosCercanos_retornaFalse() {
        when(puntoMapaRepository.findPuntosEnRadio(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of());

        boolean resultado = puntoMapaService.estaEnZonaValida(-12.1191, -77.0308);

        assertFalse(resultado);
    }
}