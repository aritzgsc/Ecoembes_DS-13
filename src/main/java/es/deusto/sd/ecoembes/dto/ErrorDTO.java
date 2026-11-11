package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Mensaje de error que se devuelve a los clientes de la API. */
public record ErrorDTO(
        @Schema(description = "Motivo legible del rechazo.", example = "Ya existe una lectura para el día actual.")
        String message) {
}
