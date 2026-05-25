package utec.reciscore.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import utec.reciscore.user.dto.UserResponseDTO;
import utec.reciscore.user.dto.UserUpdateDTO;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.User;
import utec.reciscore.user.model.UserService;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private ModelMapper modelMapper;
    @InjectMocks private UserService userService;

    private User user;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("luis@gmail.com");
        user.setName("Luis Nieva");
        user.setUsername("luisnieva");
        user.setPoints(100);
        user.setMultiplier(1.0);
        user.setLocation("Miraflores");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setEmail("luis@gmail.com");
        userResponseDTO.setName("Luis Nieva");
        userResponseDTO.setPoints(100);
    }

    @Test
    void shouldReturnUserWhenIdExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(userResponseDTO);

        UserResponseDTO response = userService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Luis Nieva", response.getName());
    }

    @Test
    void shouldThrowExceptionWhenUserIdDoesNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getById(99L));
    }

    @Test
    void shouldUpdateNameWhenNewNameIsProvided() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("Nuevo Nombre");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(modelMapper.map(any(), eq(UserResponseDTO.class))).thenReturn(userResponseDTO);

        userService.update(1L, dto);

        assertEquals("Nuevo Nombre", user.getName());
        verify(userRepository).save(user);
    }

    @Test
    void shouldUpdateLocationWhenNewLocationIsProvided() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setLocation("San Isidro");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(modelMapper.map(any(), eq(UserResponseDTO.class))).thenReturn(userResponseDTO);

        userService.update(1L, dto);

        assertEquals("San Isidro", user.getLocation());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.update(99L, new UserUpdateDTO()));
    }
}