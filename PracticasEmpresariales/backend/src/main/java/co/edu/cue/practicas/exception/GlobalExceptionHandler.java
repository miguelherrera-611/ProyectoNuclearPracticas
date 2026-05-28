package co.edu.cue.practicas.exception;

import co.edu.cue.practicas.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccesoNoAutorizadoException.class)
    public ResponseEntity<ApiResponse<Void>> manejarAccesoNoAutorizado(AccesoNoAutorizadoException e) {
        log.warn("[SEGURIDAD] Acceso no autorizado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Void>> manejarNoEncontrado(RecursoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<ApiResponse<Void>> manejarOperacionNoPermitida(OperacionNoPermitidaException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> manejarIntegridad(DataIntegrityViolationException e) {
        // Violación de constraint de BD (NIT duplicado, correo duplicado, etc.)
        // Extraemos el mensaje más específico de la causa raíz para mostrarlo al usuario
        String causa = e.getMostSpecificCause().getMessage();
        String mensaje = "Dato duplicado: ya existe un registro con ese valor.";
        if (causa != null && causa.contains("Duplicate entry")) {
            // Ej: Duplicate entry '1321' for key 'idx_empresa_nit'
            mensaje = "Ya existe un registro con ese valor (constraint: " + extraerConstraint(causa) + ").";
        }
        log.warn("[INTEGRIDAD] Violación de constraint: {}", causa);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(mensaje));
    }

    /** Extrae el nombre del constraint del mensaje de MySQL para mostrarlo en la respuesta */
    private String extraerConstraint(String mensaje) {
        int idx = mensaje.lastIndexOf("'");
        int inicio = mensaje.lastIndexOf("'", idx - 1);
        if (inicio >= 0 && idx > inicio) {
            return mensaje.substring(inicio + 1, idx);
        }
        return "constraint desconocido";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> manejarValidacion(MethodArgumentNotValidException e) {
        Map<String, String> errores = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            errores.put(campo, error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .exitoso(false)
                        .mensaje("Errores de validación")
                        .datos(errores)
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> manejarGeneral(Exception e) {
        log.error("[ERROR] Error interno: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor. Contacta al Administrador DTI."));
    }
}
