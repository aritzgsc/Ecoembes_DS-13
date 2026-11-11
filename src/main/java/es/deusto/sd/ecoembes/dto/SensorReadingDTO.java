package es.deusto.sd.ecoembes.dto;

import es.deusto.sd.ecoembes.domain.FillLevel;

/** Lectura enviada por el sensor: packageCount debe validarse si es null. */
public record SensorReadingDTO(Integer packageCount, FillLevel fillLevel) {
}
