package es.deusto.sd.ecoembes.dto;

import java.time.LocalDate;

/** Capacidad total, asignada y disponible de una planta para una fecha. */
public record PlantCapacityDTO(
        String plantId,
        String plantName,
        LocalDate date,
        double totalTons,
        double assignedTons,
        double availableTons) {
}
