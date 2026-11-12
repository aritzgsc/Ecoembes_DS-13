package es.deusto.sd.ecoembes.dto;

import java.time.LocalDate;

import es.deusto.sd.ecoembes.domain.FillLevel;

import io.swagger.v3.oas.annotations.media.Schema;

/** Lectura de un contenedor presentada por la API. */
@Schema(description = "Medición histórica diaria. Una sola lectura por contenedor y día.")
public record ContainerReadingDTO(
        @Schema(description = "Fecha de lectura ISO yyyy-MM-dd, fijada por el servidor.", example = "2026-11-11")
        LocalDate date,
        @Schema(description = "Número de envases de la medición.", example = "125")
        int packageCount,
        @Schema(description = "Nivel comunicado por el sensor o generado para los datos semilla.", example = "GREEN")
        FillLevel fillLevel) {
}
