package es.deusto.sd.ecoembes.domain;

import java.time.LocalDate;

/** Capacidad total declarada por una planta de reciclaje para una fecha concreta. */
public class PlantCapacityRecord {

    private final LocalDate date;
    private final double totalTons;

    public PlantCapacityRecord(LocalDate date, double totalTons) {
        if (date == null || !Double.isFinite(totalTons) || totalTons < 0) {
            throw new IllegalArgumentException("Fecha o capacidad inválida.");
        }
        this.date = date;
        this.totalTons = totalTons;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getTotalTons() {
        return totalTons;
    }
}
