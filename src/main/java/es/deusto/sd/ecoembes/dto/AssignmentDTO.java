package es.deusto.sd.ecoembes.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Datos de una asignación para devolver al cliente de la API. */
@Schema(description = "Asignación aceptada con una instantánea de la carga y sus datos de auditoría.")
public record AssignmentDTO(
        @Schema(description = "Identificador secuencial en memoria; se reinicia con el servidor.", example = "1")
        long id,
        @Schema(description = "Identificador de la planta destinataria.", example = "PLASSB")
        String plantId,
        @Schema(description = "Nombre de la planta destinataria.", example = "PlasSB Ltd.")
        String plantName,
        @Schema(description = "Fecha ISO prevista para procesar la asignación.", example = "2026-11-11")
        LocalDate date,
        @Schema(description = "Identificadores de los contenedores de la selección aceptada.", example = "[\"CONT-003\",\"CONT-006\",\"CONT-009\"]")
        List<String> containerIds,
        @Schema(description = "Número de contenedores asignados.", example = "3")
        int containerCount,
        @Schema(description = "Suma de los envases de las últimas lecturas en el momento de asignar.", example = "4950")
        int totalPackages,
        @Schema(description = "Carga estimada, en toneladas; 0,03 kg por envase.", example = "0.1485")
        double totalTons,
        @Schema(description = "Email del empleado responsable. No expone su contraseña.", example = "ana.garcia@ecoembes.es")
        String assignedBy,
        @Schema(description = "Instante local de registro ISO yyyy-MM-ddTHH:mm:ss, sin zona horaria.", example = "2026-11-11T16:30:00")
        LocalDateTime createdAt) {
}
