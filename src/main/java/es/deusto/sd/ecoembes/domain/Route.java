package es.deusto.sd.ecoembes.domain;

import java.time.LocalDate;
import java.util.List;

/** Ruta conceptual asociada a uno o más contenedores, sin planificación en P1. */
public class Route {

    private final String id;
    private final LocalDate date;
    private final int durationMinutes;
    private final List<Container> containers;

    public Route(String id, LocalDate date, int durationMinutes, List<Container> containers) {
        if (containers == null || containers.isEmpty()) {
            throw new IllegalArgumentException("La ruta debe contener al menos un contenedor.");
        }
        this.id = id;
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.containers = List.copyOf(containers);
    }

    public String getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public List<Container> getContainers() {
        return containers;
    }
}
