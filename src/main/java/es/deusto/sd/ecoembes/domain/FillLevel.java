package es.deusto.sd.ecoembes.domain;

/** Nivel de llenado calculado por el servidor a partir del porcentaje ocupado. */
public enum FillLevel {
    GREEN, ORANGE, RED;

    /** Usa los mismos umbrales que los datos semilla del Prototipo 1. */
    public static FillLevel fromRatio(int packageCount, int capacity) {
        if (capacity <= 0 || packageCount < 0 || packageCount > capacity) {
            throw new IllegalArgumentException("Cantidad o capacidad inválida.");
        }
        double ratio = (double) packageCount / capacity;
        if (ratio < 0.50) {
            return GREEN;
        }
        if (ratio < 0.85) {
            return ORANGE;
        }
        return RED;
    }
}
