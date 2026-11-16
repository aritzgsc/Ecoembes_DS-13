package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Lectura enviada por el sensor: el nivel de llenado se calcula en el servidor. */
@Schema(description = "Lectura diaria del sensor, sin token. El servidor determina la fecha y calcula el nivel de llenado.")
public record SensorReadingDTO(
        @Schema(description = "Cantidad obligatoria, entre cero y la capacidad del contenedor. Omitirla o enviar null produce 400.", example = "125", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer packageCount) {
}
