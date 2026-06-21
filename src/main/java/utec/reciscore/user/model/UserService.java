package utec.reciscore.user.model;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import utec.reciscore.exceptions.DuplicateUserException;
import utec.reciscore.user.dto.UserRequestDTO;
import utec.reciscore.user.dto.UserResponseDTO;
import utec.reciscore.user.dto.UserUpdateDTO;
import utec.reciscore.user.infrastructure.UserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor

public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }


    public UserResponseDTO create(UserRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateUserException(
                    "El email ya está registrado"
            );
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateUserException(
                    "El username ya existe"
            );
        }

        User user = modelMapper.map(dto, User.class);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO update(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        checkOwnerOrAdmin(user, "No puedes editar otra cuenta");

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getProfilePicture() != null) user.setProfilePicture(dto.getProfilePicture());
        if (dto.getLocation() != null) user.setLocation(dto.getLocation());

        return modelMapper.map(userRepository.save(user), UserResponseDTO.class);
    }

    public void delete(Long id) {

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        checkOwnerOrAdmin(userToDelete, "No puedes eliminar otra cuenta");

        userRepository.delete(userToDelete);
    }

    private void checkOwnerOrAdmin(User target, String mensajeError) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        assert authentication != null;
        String currentEmail = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boolean isSameUser = target.getEmail().equals(currentEmail);

        if (!isAdmin && !isSameUser) {
            throw new AccessDeniedException(mensajeError);
        }
    }
}