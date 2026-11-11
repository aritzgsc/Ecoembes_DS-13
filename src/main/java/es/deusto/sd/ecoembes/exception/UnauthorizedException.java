package es.deusto.sd.ecoembes.exception;

/** Indica que la autenticación no es válida (HTTP 401). */
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	public UnauthorizedException(String message) {
        super(message);
    }
}
