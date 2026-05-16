package utec.reciscore.user.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utec.reciscore.user.dto.UserRequestDTO;
import utec.reciscore.user.dto.UserResponseDTO;
import utec.reciscore.user.model.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponseDTO create(@RequestBody @Valid UserRequestDTO dto) {
        return userService.create(dto);
    }

}
