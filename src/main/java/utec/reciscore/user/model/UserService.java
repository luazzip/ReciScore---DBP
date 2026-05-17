package utec.reciscore.user.model;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import utec.reciscore.exceptions.DuplicateUserException;
import utec.reciscore.user.dto.UserRequestDTO;
import utec.reciscore.user.dto.UserResponseDTO;
import utec.reciscore.user.infrastructure.UserRepository;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


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

}
