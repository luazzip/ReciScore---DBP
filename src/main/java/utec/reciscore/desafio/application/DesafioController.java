package utec.reciscore.desafio.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utec.reciscore.desafio.dto.CreateDesafioRequest;
import utec.reciscore.desafio.dto.DetailDesafioResponse;
import utec.reciscore.desafio.dto.ListDesafioResponse;
import utec.reciscore.desafio.dto.UpdateDesafioRequest;
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

    //Obtener desafios con estado de inscripcion del usuario
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<ListDesafioResponse>> getDesafiosByUser(@PathVariable Long userId) {
        List<ListDesafioResponse> desafios = desafioService.findAllWithUserStatus(userId);
        if (desafios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(desafios);
    }

    //Obtener desafios por id (con progreso del usuario si se pasa userId)
    @GetMapping("/{id}")
    public ResponseEntity<DetailDesafioResponse> getDesafioById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        DetailDesafioResponse response = (userId != null)
                ? desafioService.findByIdWithUserStatus(id, userId)
                : desafioService.findById(id);
        return ResponseEntity.ok(response);
    }

    //Unirse a desafio (usuario acepta un desafio)
    @PostMapping("/{id}/unirse")
    public ResponseEntity<DetailDesafioResponse> unirse(@PathVariable Long id,@RequestParam Long userId) {
        DetailDesafioResponse response=desafioService.unirse(id,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Desinscribirse de un desafio
    @DeleteMapping("/{id}/desistir")
    public ResponseEntity<DetailDesafioResponse> desistir(@PathVariable Long id, @RequestParam Long userId) {
        DetailDesafioResponse response = desafioService.desistir(id, userId);
        return ResponseEntity.ok(response);
    }

    //editar desafio
    @PatchMapping("/{id}")
    public ResponseEntity<DetailDesafioResponse> updateDesafio(@PathVariable Long id, @RequestBody UpdateDesafioRequest request) {
        DetailDesafioResponse response=desafioService.updateDesafio(id,request);
        return ResponseEntity.ok(response);
    }
}
