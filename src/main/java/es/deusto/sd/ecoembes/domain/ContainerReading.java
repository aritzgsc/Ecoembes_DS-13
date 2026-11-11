package es.deusto.sd.ecoembes.domain;

import java.time.LocalDate;

/** Una medición diaria del número de envases y del nivel de llenado. */
public class ContainerReading {

    // Hipótesis del prototipo: cada envase pesa 0,03 kg de media.
    public static final double PACKAGE_WEIGHT_KG = 0.03;

    private final LocalDate date;
    private final int packageCount;
    private final FillLevel fillLevel;

    public ContainerReading(LocalDate date, int packageCount, FillLevel fillLevel) {
        this.date = date;
        this.packageCount = packageCount;
        this.fillLevel = fillLevel;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public FillLevel getFillLevel() {
        return fillLevel;
    }

    /** Convierte el número de envases en toneladas estimadas. */
    public double getTons() {
        return packageCount * PACKAGE_WEIGHT_KG / 1000;
    }
}
