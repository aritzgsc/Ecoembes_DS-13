package es.deusto.sd.ecoembes.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Datos de una asignación para devolver al cliente de la API. */
public record AssignmentDTO(
        long id,
        String plantId,
        String plantName,
        LocalDate date,
        List<String> containerIds,
        int containerCount,
        int totalPackages,
        double totalTons,
        String assignedBy,
        LocalDateTime createdAt) {
}
