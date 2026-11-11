package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Datos recibidos para dar de alta un contenedor. */
public record ContainerCreateDTO(
        @Schema(description = "Identificador único del contenedor.", example = "CONT-011", requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
        @Schema(example = "Calle de Prueba 11, Bilbao", requiredMode = Schema.RequiredMode.REQUIRED)
        String address,
        @Schema(description = "Cinco dígitos.", example = "48009", pattern = "[0-9]{5}", requiredMode = Schema.RequiredMode.REQUIRED)
        String postalCode,
        @Schema(description = "Capacidad máxima en envases, mayor que cero.", example = "1000", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        int capacity) {
}
