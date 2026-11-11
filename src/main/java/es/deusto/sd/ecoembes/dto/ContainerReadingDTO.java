package es.deusto.sd.ecoembes.dto;

import java.time.LocalDate;

import es.deusto.sd.ecoembes.domain.FillLevel;

/** Lectura de un contenedor presentada por la API. */
public record ContainerReadingDTO(LocalDate date, int packageCount, FillLevel fillLevel) {
}
