package es.deusto.sd.ecoembes.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;

import es.deusto.sd.ecoembes.dto.ErrorDTO;

/** Comprueba que los errores esperados se convierten en respuestas HTTP correctas. */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void badRequestDevuelve400() {
        comprobarRespuesta(handler.handleBadRequest(new BadRequestException("Datos incorrectos")),
                HttpStatus.BAD_REQUEST, "Datos incorrectos");
    }

    @Test
    void unauthorizedDevuelve401() {
        comprobarRespuesta(handler.handleUnauthorized(new UnauthorizedException("Token no válido")),
                HttpStatus.UNAUTHORIZED, "Token no válido");
    }

    @Test
    void notFoundDevuelve404() {
        comprobarRespuesta(handler.handleNotFound(new NotFoundException("Contenedor no encontrado")),
                HttpStatus.NOT_FOUND, "Contenedor no encontrado");
    }

    @Test
    void conflictDevuelve409() {
        comprobarRespuesta(handler.handleConflict(new ConflictException("Lectura duplicada")),
                HttpStatus.CONFLICT, "Lectura duplicada");
    }

    @Test
    void parametroObligatorioAusenteDevuelve400() {
        var error = new MissingServletRequestParameterException("token", "long");
        comprobarRespuesta(handler.handleBadInput(error),
                HttpStatus.BAD_REQUEST, "Los datos de la petición no tienen un formato válido");
    }

    /** Reutiliza las mismas comprobaciones para todas las respuestas de error. */
    private void comprobarRespuesta(ResponseEntity<ErrorDTO> respuesta,
            HttpStatus estadoEsperado, String mensajeEsperado) {
        assertEquals(estadoEsperado, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(mensajeEsperado, respuesta.getBody().message());
    }
}
