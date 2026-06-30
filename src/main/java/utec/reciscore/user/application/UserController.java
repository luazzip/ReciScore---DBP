package utec.reciscore.user.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utec.reciscore.user.dto.UserImpactDTO;
import utec.reciscore.user.dto.UserRequestDTO;
import utec.reciscore.user.dto.UserResponseDTO;
import utec.reciscore.user.dto.UserUpdateDTO;
import utec.reciscore.user.model.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrent() {
        return ResponseEntity.ok(userService.getCurrent());
    }

    @GetMapping("/me/impacto")
    public ResponseEntity<UserImpactDTO> getCurrentImpact() {
        return ResponseEntity.ok(userService.getImpact());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id,
                                                  @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}