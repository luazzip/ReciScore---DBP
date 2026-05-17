package utec.reciscore.desafio.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utec.reciscore.desafio.dto.CreateDesafioRequest;
import utec.reciscore.desafio.dto.DetailDesafioResponse;
import utec.reciscore.desafio.dto.ListDesafioResponse;
import utec.reciscore.desafio.model.DesafioService;

import java.util.List;

@RestController
@RequestMapping("/desafios")
@RequiredArgsConstructor
public class DesafioController {
    private final DesafioService desafioService;

    //Crear desafio
    @PostMapping
    public ResponseEntity<DetailDesafioResponse> createDesafio(@RequestBody @Valid CreateDesafioRequest desafio){
        DetailDesafioResponse desafiocreado=desafioService.createDesafio(desafio);
        return ResponseEntity.status(HttpStatus.CREATED).body(desafiocreado);
    }

    //Obtener desafios
    @GetMapping
    public ResponseEntity<List<ListDesafioResponse>> getAllDesafios() {
        List<ListDesafioResponse> desafios=desafioService.findAll();
        if(desafios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(desafios);
    }
}
