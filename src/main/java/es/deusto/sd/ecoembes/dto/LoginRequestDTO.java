package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Credenciales que recibe la API al iniciar sesión. */
@Schema(description = "Credenciales ficticias del personal para abrir una sesión local.")
public record LoginRequestDTO(
        @Schema(description = "Correo electrónico del empleado.", example = "ana.garcia@ecoembes.es", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(description = "Contraseña del empleado. Credencial ficticia de demostración.", example = "ana123", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
        String password) {
}
