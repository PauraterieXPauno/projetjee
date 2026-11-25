package fr.cytech.bde.web.api;

import fr.cytech.bde.service.exception.BusinessException;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import fr.cytech.bde.web.api.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "fr.cytech.bde.web.api")
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> ressourceIntrouvable(ResourceNotFoundException exception,
                                                         HttpServletRequest request) {
        return construire(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> erreurMetier(BusinessException exception, HttpServletRequest request) {
        return construire(HttpStatus.BAD_REQUEST, exception.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception,
                                               HttpServletRequest request) {
        Map<String, String> details = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            details.merge(fieldError.getField(), fieldError.getDefaultMessage(),
                    (existant, nouveau) -> existant + " ; " + nouveau);
        }
        exception.getBindingResult().getGlobalErrors()
                .forEach(error -> details.put("_global", error.getDefaultMessage()));
        return construire(HttpStatus.BAD_REQUEST, "La requête contient des champs invalides.", request, details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> jsonIllisible(HttpMessageNotReadableException exception,
                                                   HttpServletRequest request) {
        return construire(HttpStatus.BAD_REQUEST,
                "Le corps JSON est invalide ou contient une valeur de statut/type inconnue.", request, null);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ApiError> parametreInvalide(Exception exception, HttpServletRequest request) {
        return construire(HttpStatus.BAD_REQUEST, "Paramètre de requête invalide.", request, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> contrainte(DataIntegrityViolationException exception,
                                                HttpServletRequest request) {
        return construire(HttpStatus.BAD_REQUEST,
                "L'opération viole une contrainte d'intégrité (association ou valeur unique).", request, null);
    }

    private ResponseEntity<ApiError> construire(HttpStatus status, String message, HttpServletRequest request,
                                                 Map<String, String> details) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                details
        );
        return ResponseEntity.status(status).body(error);
    }
}
