package fr.cytech.bde.service.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException pour(String ressource, Long id) {
        return new ResourceNotFoundException(ressource + " introuvable (id=" + id + ").");
    }
}
