package es.deusto.sd.ecoembes.dto;

import java.time.LocalDate;
import java.util.List;

/** Petición para asignar uno o más contenedores a una planta. */
public record AssignmentRequestDTO(LocalDate date, List<String> containerIds) {
}
