package es.deusto.sd.ecoembes.dto;

import java.time.LocalDate;

import es.deusto.sd.ecoembes.domain.FillLevel;

import io.swagger.v3.oas.annotations.media.Schema;

/** Información del contenedor; los cuatro campos de lectura admiten null. */
@Schema(description = "Estado de un contenedor. Los cuatro campos derivados de la lectura son null mientras no exista ninguna medición.")
public record ContainerDTO(
        @Schema(description = "Identificador del contenedor.", example = "CONT-001")
        String id,
        @Schema(description = "Dirección del contenedor.", example = "Calle Ercilla 10, Bilbao")
        String address,
        @Schema(description = "Código postal de cinco dígitos.", example = "48009")
        String postalCode,
        @Schema(description = "Capacidad máxima en número de envases.", example = "2000")
        int capacity,
        @Schema(description = "Envases de la lectura seleccionada; null si no existe lectura.", example = "400", types = {"integer", "null"}, nullable = true)
        Integer packageCount,
        @Schema(description = "Nivel de la lectura seleccionada; null si no existe lectura.", example = "GREEN", types = {"string", "null"}, nullable = true)
        FillLevel fillLevel,
        @Schema(description = "Toneladas estimadas: packageCount × 0,03 / 1000; null sin lectura.", example = "0.012", types = {"number", "null"}, nullable = true)
        Double estimatedTons,
        @Schema(description = "Fecha ISO de la lectura seleccionada; null si no existe lectura.", example = "2026-11-11", types = {"string", "null"}, nullable = true)
        LocalDate readingDate) {
}
