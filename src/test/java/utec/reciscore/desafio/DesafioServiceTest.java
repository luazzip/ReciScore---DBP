package utec.reciscore.desafio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import utec.reciscore.desafio.dto.CreateDesafioRequest;
import utec.reciscore.desafio.dto.DetailDesafioResponse;
import utec.reciscore.desafio.dto.UpdateDesafioRequest;
import utec.reciscore.desafio.infraestructure.DesafioRepository;
import utec.reciscore.desafio.model.Desafio;
import utec.reciscore.desafio.model.DesafioService;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesafioServiceTest {

    @Mock
    private DesafioRepository desafioRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DesafioService desafioService;

    private Desafio desafio;
    private CreateDesafioRequest createRequest;
    private User user;

    @BeforeEach
    void setUp() {
        desafio = new Desafio();
        desafio.setId(1L);
        desafio.setTitulo("Recicla 3 botellas");
        desafio.setDescripcion("Recicla 3 botellas de plastico");
        desafio.setCategoria("PLASTICO");
        desafio.setActivo(true);
        desafio.setFecha_inicio(LocalDateTime.now());
        desafio.setFecha_fin(LocalDateTime.now().plusDays(7));
        desafio.setMeta_valor(3);
        desafio.setPuntos(100);
        desafio.setUsuariosInscritos(new HashSet<>());

        createRequest = new CreateDesafioRequest();
        createRequest.setTitulo("Recicla 3 botellas");
        createRequest.setDescripcion("Recicla 3 botellas de plastico");
        createRequest.setCategoria("PLASTICO");
        createRequest.setFecha_inicio(LocalDateTime.now());
        createRequest.setFecha_fin(LocalDateTime.now().plusDays(7));
        createRequest.setPuntos(100);
        createRequest.setMeta_valor(3);

        user = new User();
        user.setId(1L);
        user.setEmail("luis@gmail.com");
        user.setName("Luis Nieva");
    }

    @Test
    void createDesafio_exitoso() {
        // Arrange
        when(desafioRepository.existsByCategoria(anyString())).thenReturn(false);
        when(modelMapper.map(any(), eq(Desafio.class))).thenReturn(desafio);
        when(desafioRepository.save(any())).thenReturn(desafio);
        when(modelMapper.map(any(), eq(DetailDesafioResponse.class)))
                .thenReturn(new DetailDesafioResponse());

        DetailDesafioResponse response = desafioService.createDesafio(createRequest);

        assertNotNull(response);
        verify(desafioRepository).save(any());
    }

    @Test
    void createDesafio_categoriaDuplicada_lanzaExcepcion() {
        when(desafioRepository.existsByCategoria(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> desafioService.createDesafio(createRequest));

        verify(desafioRepository, never()).save(any());
    }

    @Test
    void createDesafio_fechaFinAntesFechaInicio_lanzaExcepcion() {

        createRequest.setFecha_fin(LocalDateTime.now().minusDays(1));
        when(desafioRepository.existsByCategoria(anyString())).thenReturn(false);


        assertThrows(IllegalArgumentException.class,
                () -> desafioService.createDesafio(createRequest));

        verify(desafioRepository, never()).save(any());
    }

    @Test
    void findById_exitoso() {

        when(desafioRepository.findById(1L)).thenReturn(Optional.of(desafio));
        when(modelMapper.map(any(), eq(DetailDesafioResponse.class)))
                .thenReturn(new DetailDesafioResponse());

        DetailDesafioResponse response = desafioService.findById(1L);

        assertNotNull(response);
    }

    @Test
    void findById_noExiste_lanzaExcepcion() {

        when(desafioRepository.findById(anyLong())).thenReturn(Optional.empty());


        assertThrows(NoSuchElementException.class,
                () -> desafioService.findById(99L));
    }

    @Test
    void unirse_exitoso() {

        when(desafioRepository.findById(1L)).thenReturn(Optional.of(desafio));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(desafioRepository.save(any())).thenReturn(desafio);
        when(modelMapper.map(any(), eq(DetailDesafioResponse.class)))
                .thenReturn(new DetailDesafioResponse());


        DetailDesafioResponse response = desafioService.unirse(1L, 1L);


        assertNotNull(response);
        assertTrue(desafio.getUsuariosInscritos().contains(user));
        verify(desafioRepository).save(any());
    }

    @Test
    void unirse_usuarioYaInscrito_lanzaExcepcion() {
        // Arrange
        desafio.getUsuariosInscritos().add(user);
        when(desafioRepository.findById(1L)).thenReturn(Optional.of(desafio));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> desafioService.unirse(1L, 1L));

        verify(desafioRepository, never()).save(any());
    }

    @Test
    void updateDesafio_exitoso() {

        UpdateDesafioRequest updateRequest = new UpdateDesafioRequest();
        updateRequest.setTitulo("Nuevo titulo");
        updateRequest.setDescripcion("Nueva descripcion");

        when(desafioRepository.findById(1L)).thenReturn(Optional.of(desafio));
        when(desafioRepository.save(any())).thenReturn(desafio);
        when(modelMapper.map(any(), eq(DetailDesafioResponse.class)))
                .thenReturn(new DetailDesafioResponse());

        DetailDesafioResponse response = desafioService.updateDesafio(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Nuevo titulo", desafio.getTitulo());
        assertEquals("Nueva descripcion", desafio.getDescripcion());
    }

    @Test
    void updateDesafio_noExiste_lanzaExcepcion() {

        when(desafioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> desafioService.updateDesafio(99L, new UpdateDesafioRequest()));
    }
}