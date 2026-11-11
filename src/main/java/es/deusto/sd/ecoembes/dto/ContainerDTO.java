package es.deusto.sd.ecoembes.dto;

import java.time.LocalDate;

import es.deusto.sd.ecoembes.domain.FillLevel;

/** Información del contenedor; los cuatro campos de lectura admiten null. */
public record ContainerDTO(
        String id,
        String address,
        String postalCode,
        int capacity,
        Integer packageCount,
        FillLevel fillLevel,
        Double estimatedTons,
        LocalDate readingDate) {
}
