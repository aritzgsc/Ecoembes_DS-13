package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Mensaje de error que se devuelve a los clientes de la API. */
@Schema(description = "Respuesta común de los errores previstos de la API (400, 401, 404 y 409).")
public record ErrorDTO(
        @Schema(description = "Descripción legible del motivo del error.", example = "Ya existe una lectura para la fecha indicada.")
        String message) {
}
