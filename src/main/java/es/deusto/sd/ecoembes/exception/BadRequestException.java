package es.deusto.sd.ecoembes.exception;

/** Indica que la petición contiene datos incorrectos (HTTP 400). */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
