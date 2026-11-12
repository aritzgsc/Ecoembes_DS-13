package es.deusto.sd.ecoembes.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

/** Capacidad total, asignada y disponible de una planta para una fecha. */
@Schema(description = "Capacidad diaria simulada, expresada en toneladas.")
public record PlantCapacityDTO(
        @Schema(description = "Identificador de la planta.", example = "PLASSB")
        String plantId,
        @Schema(description = "Nombre de la planta.", example = "PlasSB Ltd.")
        String plantName,
        @Schema(description = "Fecha ISO consultada.", example = "2026-11-11")
        LocalDate date,
        @Schema(description = "Capacidad diaria total simulada, en toneladas.", example = "0.4")
        double totalTons,
        @Schema(description = "Toneladas reservadas por asignaciones aceptadas para esta planta y fecha.", example = "0.1485")
        double assignedTons,
        @Schema(description = "Capacidad disponible: totalTons - assignedTons.", example = "0.2515")
        double availableTons) {
}
