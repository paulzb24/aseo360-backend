package com.aseo360.aseo360.controlador;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errores de validación de @Valid (@NotBlank, @NotNull, etc.) → 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Error de validación");
        response.put("message", "Por favor complete todos los campos obligatorios correctamente.");
        response.put("errores", errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // JSON malformado o tipos de datos incorrectos en el body → 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Solicitud inválida");
        response.put("message",
                "El formato de los datos enviados es incorrecto. Verifique los campos e intente nuevamente.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Violación de integridad de datos (duplicados, FK inválidas, etc.) → 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        String mensaje = "Error de integridad de datos.";

        String rootMsg = ex.getMostSpecificCause().getMessage();
        if (rootMsg != null) {
            if (rootMsg.contains("Duplicate") || rootMsg.contains("UNIQUE") || rootMsg.contains("duplicate")) {
                mensaje = "Ya existe un registro con los mismos datos. Verifique e intente nuevamente.";
            } else if (rootMsg.contains("foreign key") || rootMsg.contains("FOREIGN KEY")
                    || rootMsg.contains("REFERENCES")) {
                mensaje = "No se puede realizar la operación porque existen registros relacionados.";
            }
        }

        response.put("error", mensaje);
        response.put("message", mensaje);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Errores de negocio (throw new Exception("...") en servicios) → 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "Argumento inválido";
        response.put("error", mensaje);
        response.put("message", mensaje);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Cualquier otra excepción → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralExceptions(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor";
        response.put("error", mensaje);
        response.put("message", mensaje);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
