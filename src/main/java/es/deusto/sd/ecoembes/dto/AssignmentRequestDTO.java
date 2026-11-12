package es.deusto.sd.ecoembes.dto;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Petición para asignar uno o más contenedores a una planta. */
@Schema(description = "Selección atómica de contenedores para una planta y fecha.")
public record AssignmentRequestDTO(
        @Schema(
                description = "Fecha ISO yyyy-MM-dd, entre hoy y hoy+9 según el servidor.",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalDate date,
        @Schema(description = "IDs de contenedores existentes, con lecturas y no asignados ese día; lista no vacía y sin repetidos.", example = "[\"CONT-003\",\"CONT-006\",\"CONT-009\"]", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> containerIds) {
}
