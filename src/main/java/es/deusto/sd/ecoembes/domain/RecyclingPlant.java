package es.deusto.sd.ecoembes.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/** Planta de reciclaje con el histórico de capacidades consultadas por fecha. */
public class RecyclingPlant {

    private final String id;
    private final String name;
    private final Map<LocalDate, PlantCapacityRecord> capacityHistory;

    public RecyclingPlant(String id, String name) {
        this.id = id;
        this.name = name;
        this.capacityHistory = new TreeMap<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /** Guarda la última capacidad declarada para la fecha, sin borrar otras fechas. */
    public synchronized void recordCapacity(LocalDate date, double totalTons) {
        PlantCapacityRecord record = new PlantCapacityRecord(date, totalTons);
        capacityHistory.put(date, record);
    }

    /** Consulta una fecha almacenada; no realiza llamadas externas. */
    public synchronized Optional<PlantCapacityRecord> getCapacityAt(LocalDate date) {
        return Optional.ofNullable(capacityHistory.get(date));
    }

    /** Copia inmutable del histórico, ordenada de fecha más antigua a más reciente. */
    public synchronized List<PlantCapacityRecord> getCapacityHistory() {
        return List.copyOf(capacityHistory.values());
    }
}
