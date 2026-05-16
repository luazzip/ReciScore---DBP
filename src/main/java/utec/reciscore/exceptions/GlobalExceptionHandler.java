package utec.reciscore.exceptions;

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

}
