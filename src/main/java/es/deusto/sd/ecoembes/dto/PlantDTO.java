package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Información pública de una planta de reciclaje. */
@Schema(description = "Identificación de una de las dos plantas simuladas del prototipo.")
public record PlantDTO(
        @Schema(description = "Identificador de planta: PLASSB o CONTSOCKET.", example = "PLASSB")
        String id,
        @Schema(description = "Nombre de la planta.", example = "PlasSB Ltd.")
        String name) {
}
