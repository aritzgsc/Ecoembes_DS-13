package es.deusto.sd.ecoembes.exception;

/** Indica que la autenticación no es válida (HTTP 401). */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
