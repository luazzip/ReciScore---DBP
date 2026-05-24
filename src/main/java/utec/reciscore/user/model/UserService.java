package utec.reciscore.user.model;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }


    //create
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

    //getById
    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    //update
    public UserResponseDTO update(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getProfilePicture() != null) user.setProfilePicture(dto.getProfilePicture());
        if (dto.getLocation() != null) user.setLocation(dto.getLocation());

        return modelMapper.map(userRepository.save(user), UserResponseDTO.class);
    }

}
