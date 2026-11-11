package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import es.deusto.sd.ecoembes.domain.FillLevel;

/** Lectura enviada por el sensor: packageCount debe validarse si es null. */
public record SensorReadingDTO(
        @Schema(description = "Cantidad obligatoria entre cero y la capacidad del contenedor.", example = "125", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer packageCount,
        @Schema(description = "Nivel informado por el sensor; no se recalcula.", example = "GREEN", requiredMode = Schema.RequiredMode.REQUIRED)
        FillLevel fillLevel) {
}
