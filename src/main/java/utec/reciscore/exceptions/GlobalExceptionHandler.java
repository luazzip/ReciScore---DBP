package utec.reciscore.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidation(MethodArgumentNotValidException exception){
        Map<String,String> errores = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(e->errores.put(e.getField(),e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotEncontrado(NoSuchElementException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Punto en el mapa no encontrado.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleCategoriaInvalida(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categoría inválida. Los valores permitidos son: PLASTICO, VIDRIO, PAPEL, METAL.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDuplicado(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un material con ese nombre.");
    }

}
