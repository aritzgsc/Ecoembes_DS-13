package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Token que recibe el empleado después de iniciar sesión. */
@Schema(description = "Token numérico de sesión en memoria; no es un JWT.")
public record LoginResponseDTO(
        @Schema(description = "Identificador numérico de sesión. Enviar como query token; se invalida al cerrar sesión o reiniciar.", example = "1794400000000")
        long token) {
}
