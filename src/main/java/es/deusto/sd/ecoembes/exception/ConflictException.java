package es.deusto.sd.ecoembes.exception;

/** Indica que la operación entra en conflicto con el estado actual (HTTP 409). */
public class ConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	public ConflictException(String message) {
        super(message);
    }
}
