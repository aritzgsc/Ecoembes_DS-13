package es.deusto.sd.ecoembes.exception;

/** Indica que el recurso solicitado no existe (HTTP 404). */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
