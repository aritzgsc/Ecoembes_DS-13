package es.deusto.sd.ecoembes.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import es.deusto.sd.ecoembes.exception.ConflictException;

/** Contenedor con capacidad máxima de envases e historial de mediciones. */
public class Container {

    private final String id;
    private final String address;
    private final String postalCode;
    private final int capacity;
    private final List<ContainerReading> readings;

    public Container(String id, String address, String postalCode, int capacity) {
        this.id = id;
        this.address = address;
        this.postalCode = postalCode;
        this.capacity = capacity;
        this.readings = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized List<ContainerReading> getReadings() {
        return List.copyOf(readings);
    }

    /** Añade una lectura atómicamente, sin sobrescribir otra de la misma fecha. */
    public synchronized void addReading(ContainerReading reading) {
        boolean exists = readings.stream().anyMatch(r -> r.getDate().equals(reading.getDate()));
        if (exists) {
            throw new ConflictException("Ya existe una lectura para el contenedor " + id
                    + " en la fecha " + reading.getDate() + ".");
        }
        readings.add(reading);
        readings.sort(Comparator.comparing(ContainerReading::getDate));
    }

    /** Devuelve la lectura más reciente, si existe alguna. */
    public synchronized Optional<ContainerReading> getLatestReading() {
        if (readings.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(readings.get(readings.size() - 1));
    }

    /** Busca la lectura más reciente anterior o igual a la fecha indicada. */
    public synchronized Optional<ContainerReading> getReadingAt(LocalDate date) {
        for (int i = readings.size() - 1; i >= 0; i--) {
            ContainerReading reading = readings.get(i);
            if (!reading.getDate().isAfter(date)) {
                return Optional.of(reading);
            }
        }
        return Optional.empty();
    }

    /** Consulta el historial entre dos fechas, incluidas ambas. */
    public synchronized List<ContainerReading> getReadingsBetween(LocalDate from, LocalDate to) {
        return readings.stream()
                .filter(r -> !r.getDate().isBefore(from) && !r.getDate().isAfter(to))
                .toList();
    }
}
