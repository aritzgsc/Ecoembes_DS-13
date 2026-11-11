package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

import es.deusto.sd.ecoembes.domain.FillLevel;

/** Información del contenedor; los cuatro campos de lectura admiten null. */
public record ContainerDTO(
        String id,
        String address,
        String postalCode,
        int capacity,
        @Schema(description = "Envases de la lectura; null si no hay mediciones.", types = {"integer", "null"}, format = "int32")
        Integer packageCount,
        @Schema(description = "Nivel de la lectura; null si no hay mediciones.", types = {"string", "null"})
        FillLevel fillLevel,
        @Schema(description = "Toneladas: envases × 0,03 / 1000; null sin mediciones.", types = {"number", "null"}, format = "double")
        Double estimatedTons,
        @Schema(description = "Fecha de la lectura; null si no hay mediciones.", types = {"string", "null"}, format = "date")
        LocalDate readingDate) {
}
