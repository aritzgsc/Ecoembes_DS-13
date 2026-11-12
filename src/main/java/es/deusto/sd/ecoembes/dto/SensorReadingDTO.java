package es.deusto.sd.ecoembes.dto;

import es.deusto.sd.ecoembes.domain.FillLevel;

import io.swagger.v3.oas.annotations.media.Schema;

/** Lectura enviada por el sensor: packageCount debe validarse si es null. */
@Schema(description = "Lectura diaria del sensor, sin token. El servidor determina la fecha.")
public record SensorReadingDTO(
        @Schema(description = "Cantidad obligatoria, entre cero y la capacidad del contenedor. Omitirla o enviar null produce 400.", example = "125", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer packageCount,
        @Schema(description = "Nivel comunicado por el sensor. Obligatorio; no se deriva de la cantidad recibida.", example = "GREEN", requiredMode = Schema.RequiredMode.REQUIRED)
        FillLevel fillLevel) {
}
