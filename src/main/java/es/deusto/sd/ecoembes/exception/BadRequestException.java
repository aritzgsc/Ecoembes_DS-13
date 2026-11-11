package es.deusto.sd.ecoembes.exception;

/** Indica que la petición contiene datos incorrectos (HTTP 400). */
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	public BadRequestException(String message) {
        super(message);
    }
}
