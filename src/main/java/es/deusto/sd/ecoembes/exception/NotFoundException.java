package es.deusto.sd.ecoembes.exception;

/** Indica que el recurso solicitado no existe (HTTP 404). */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	public NotFoundException(String message) {
        super(message);
    }
}
