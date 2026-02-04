package com.gestion.eventos.api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handlerValidationExceptions(MethodArgumentNotValidException ex){

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error->{
            errors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> body = new HashMap<>();

        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad request" );
        body.put("message", "Errores validación" );
        body.put("errors", errors );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handlerResourceNotFoundException(ResourceNotFoundException ex){

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found" );
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Versión súper simple para producción sin detalles específicos de la DB
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationSimple(DataIntegrityViolationException ex) {

        Map<String, String> errorDetails = new HashMap<>();

        errorDetails.put("error", "Conflicto de Datos");

        errorDetails.put("message", "La operación no se pudo completar debido a un conflicto de datos. Asegúrate de que los valores sean únicos y las referencias existan.");

        // O puedes usar el mensaje original de la causa raíz si no te importa exponerlo (no recomendado)
        // errorDetails.put("message", ex.getRootCause() != null ? ex.getRootCause().getMessage() : "Error de integridad de datos.");

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {

        System.err.println("Ocurrió un error inesperado: " + ex.getMessage());
        ex.printStackTrace(); // En producción, se loguea, no se imprime a consola

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", "Error Interno del Servidor");
        errorDetails.put("message", "Ocurrió un error inesperado. Por favor, inténtalo de nuevo más tarde.");

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }


//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
//
//        Map<String, String> errorDetails = new HashMap<>();
//
//        String errorMessage = "Error de integridad de datos. No se pudo completar la operación.";
//
//        HttpStatus status = HttpStatus.CONFLICT;
//
//        Throwable rootCause = ex.getRootCause();
//
//        if (rootCause instanceof PSQLException psqlEx) {
//
//            String sqlState = psqlEx.getSQLState();
//            String detail = psqlEx.getServerErrorMessage() != null ? psqlEx.getServerErrorMessage().getDetail() : null;
//
//            if ("23505".equals(sqlState)) { // unique_violation
//
//                errorMessage = "El recurso que intentas crear ya existe.";
//
//                if (detail != null) {
//
//                    if (detail.contains("name")) {
//
//                        errorMessage = "Ya existe un elemento con ese nombre. Por favor, elige uno diferente.";
//                    } else if (detail.contains("email")) {
//
//                        errorMessage = "Ya existe un elemento con ese email. El email debe ser único.";
//                    }
//
//                }
//
//            } else if ("23503".equals(sqlState)) { // foreign_key_violation
//
//                errorMessage = "No se pudo completar la operación. Hay una referencia a un recurso que no existe.";
//
//                if (detail != null && detail.contains("category_id")) {
//
//                    errorMessage = "La categoría especificada no existe.";
//                } else if (detail != null && detail.contains("speaker_id")) {
//
//                    errorMessage = "Uno de los ponentes especificados no existe.";
//                }
//
//                status = HttpStatus.BAD_REQUEST;
//
//            } else {
//
//                errorMessage = "Error inesperado de base de datos."; // Mensaje genérico para otros SQLStates
//                status = HttpStatus.INTERNAL_SERVER_ERROR;
//            }
//
//        } else {
//
//            // Fallback genérico si no es PSQLException (para otras DBs o errores inesperados)
//            errorMessage = "Error de base de datos desconocido. Por favor, contacta al soporte.";
//            status = HttpStatus.INTERNAL_SERVER_ERROR; // Aquí podrías poner 500 para genéricos
//        }
//
//        errorDetails.put("error", status.getReasonPhrase());
//        errorDetails.put("message", errorMessage);
//
//        return new ResponseEntity<>(errorDetails, status);
//    }

}