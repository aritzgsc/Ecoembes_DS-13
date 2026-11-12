package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Datos recibidos para dar de alta un contenedor. */
@Schema(description = "Alta de un contenedor sin lecturas iniciales.")
public record ContainerCreateDTO(
        @Schema(description = "Identificador único, no vacío.", example = "CONT-011", requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
        @Schema(description = "Dirección, no vacía.", example = "Calle de Prueba 11, Bilbao", requiredMode = Schema.RequiredMode.REQUIRED)
        String address,
        @Schema(description = "Código postal de cinco dígitos.", example = "48009", pattern = "[0-9]{5}", requiredMode = Schema.RequiredMode.REQUIRED)
        String postalCode,
        @Schema(description = "Capacidad máxima en número de envases; entero positivo.", example = "1000", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        int capacity) {
}
