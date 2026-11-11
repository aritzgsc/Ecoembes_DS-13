package es.deusto.sd.ecoembes.external;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/** Simula la planta PlasSB; en el Prototipo 2 se conectará por HTTP. */
@Component
public class PlasSBGateway implements RecyclingPlantGateway {

    private static final double[] CAPACITY_PROFILE = {
            0.40, 0.35, 0.50, 0.45, 0.30, 0.20, 0.00, 0.40, 0.45, 0.50
    };

    private final LocalDate initialDate;
    private final Map<LocalDate, Double> capacityByDate = new LinkedHashMap<>();

    public PlasSBGateway() {
        initialDate = LocalDate.now();
        synchronizeWindow(initialDate);
    }

    @Override
    public String getPlantId() {
        return "PLASSB";
    }

    @Override
    public String getPlantName() {
        return "PlasSB Ltd.";
    }

    /** Devuelve la capacidad declarada en toneladas para una fecha de la ventana. */
    @Override
    public synchronized double getCapacity(LocalDate date) {
        synchronizeWindow(LocalDate.now());
        Double capacity = capacityByDate.get(date);
        if (capacity == null) {
            throw new IllegalArgumentException("No hay capacidad para la fecha solicitada: " + date);
        }
        return capacity;
    }

    /** En P1 solo registra la notificación; en P2 se enviará por HTTP. */
    @Override
    public void notifyAssignment(LocalDate date, int containerCount, int packageCount) {
        System.out.printf("[PlasSB - simulado] %s: %d contenedores, %d envases%n",
                date, containerCount, packageCount);
    }

    /** Desplaza la ventana conservando las capacidades ya anunciadas para cada fecha. */
    private void synchronizeWindow(LocalDate today) {
        capacityByDate.keySet().removeIf(date -> date.isBefore(today));
        for (int day = 0; day < 10; day++) {
            LocalDate date = today.plusDays(day);
            int profileIndex = (int) Math.floorMod(
                    ChronoUnit.DAYS.between(initialDate, date), CAPACITY_PROFILE.length);
            capacityByDate.putIfAbsent(date, CAPACITY_PROFILE[profileIndex]);
        }
    }
}
