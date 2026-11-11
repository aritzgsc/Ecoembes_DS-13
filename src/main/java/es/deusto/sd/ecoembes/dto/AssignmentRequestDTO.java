package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

/** Petición para asignar uno o más contenedores a una planta. */
public record AssignmentRequestDTO(
        @Schema(description = "Fecha obligatoria yyyy-MM-dd entre hoy y hoy+9; sustituir por la fecha real del servidor.", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate date,
        @Schema(description = "Selección no vacía, sin identificadores repetidos. Todos deben existir, tener lectura y no estar asignados ese día.", example = "[\"CONT-003\",\"CONT-006\",\"CONT-009\"]", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> containerIds) {
}
