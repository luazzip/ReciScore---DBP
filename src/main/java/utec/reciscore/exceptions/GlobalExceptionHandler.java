package utec.reciscore.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(
            HttpMessageNotReadableException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(400, "Bad Request", "JSON malformado o campo inválido", req.getRequestURI()));
    }

    @ExceptionHandler({NoSuchElementException.class, ResourceNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(
            RuntimeException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(404, "Not Found", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(400, "Bad Request", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOp(
            InvalidOperationException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(400, "Invalid Operation", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(UploadException.class)
    public ResponseEntity<ErrorResponse> handleUpload(
            UploadException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(400, "Upload Failed", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(IaValidationException.class)
    public ResponseEntity<ErrorResponse> handleIaValidation(
            IaValidationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponse(422, "IA Validation Failed", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(GpsValidationException.class)
    public ResponseEntity<ErrorResponse> handleGps(
            GpsValidationException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(400, "GPS Validation Failed", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler({DataIntegrityViolationException.class,
            DuplicateUserException.class, DuplicateReportException.class})
    public ResponseEntity<ErrorResponse> handleDuplicate(
            Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(409, "Conflict", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedOperationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErrorResponse(403, "Forbidden", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, HttpServletRequest req) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(500, "Internal Server Error", "Error inesperado: " + ex.getMessage(), req.getRequestURI()));
    }
}