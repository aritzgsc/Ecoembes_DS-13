package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Credenciales que recibe la API al iniciar sesión. */
public record LoginRequestDTO(
        @Schema(description = "Correo del empleado de demostración.", example = "ana.garcia@ecoembes.es", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(description = "Contraseña del empleado; no conservar en evidencias.", format = "password", accessMode = Schema.AccessMode.WRITE_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        String password) {
}
