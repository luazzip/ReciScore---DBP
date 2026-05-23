package utec.reciscore.codigoValidacion.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utec.reciscore.codigoValidacion.dto.CodigoValidacionRequestDTO;
import utec.reciscore.codigoValidacion.dto.CodigoValidacionResponseDTO;
import utec.reciscore.codigoValidacion.model.CodigoValidacionService;

@RestController
@RequestMapping("/codigos-validacion")
@RequiredArgsConstructor
public class CodigoValidacionController {

    private final CodigoValidacionService codigoService;

    @PostMapping("/generar")
    public ResponseEntity<CodigoValidacionResponseDTO> generar(
            @RequestBody @Valid CodigoValidacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(codigoService.generarCodigo(dto));
    }

    @PostMapping("/verificar/{codigo}")
    public ResponseEntity<Boolean> verificar(@PathVariable String codigo) {
        return ResponseEntity.ok(codigoService.verificarCodigo(codigo));
    }
}