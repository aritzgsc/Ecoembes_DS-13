package es.deusto.sd.ecoembes.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

/** Lectura histórica expuesta por la API, sin el nivel calculado internamente. */
@Schema(description = "Medición histórica diaria. Una sola lectura por contenedor y día; no incluye fillLevel.")
public record ContainerReadingDTO(
        @Schema(description = "Fecha de lectura ISO yyyy-MM-dd, fijada por el servidor.", example = "2026-11-11")
        LocalDate date,
        @Schema(description = "Número de envases de la medición.", example = "125")
        int packageCount) {
}
