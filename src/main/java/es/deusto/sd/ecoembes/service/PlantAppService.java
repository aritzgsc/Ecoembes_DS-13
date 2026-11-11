package es.deusto.sd.ecoembes.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import es.deusto.sd.ecoembes.domain.RecyclingPlant;
import es.deusto.sd.ecoembes.exception.BadRequestException;
import es.deusto.sd.ecoembes.exception.NotFoundException;
import es.deusto.sd.ecoembes.external.RecyclingPlantGateway;

/** Consulta las plantas externas simuladas sin acoplarse a su protocolo. */
@Service
public class PlantAppService {

    private final List<RecyclingPlantGateway> gateways;
    private final List<RecyclingPlant> plants;

    /** Spring inyecta aquí los componentes que implementan RecyclingPlantGateway. */
    public PlantAppService(List<RecyclingPlantGateway> gateways) {
        this.gateways = List.copyOf(gateways);
        this.plants = this.gateways.stream()
                .map(gateway -> new RecyclingPlant(gateway.getPlantId(), gateway.getPlantName()))
                .toList();
    }

    /** Lista las plantas configuradas. */
    public List<RecyclingPlant> getPlants() {
        return plants;
    }

    /** Obtiene una planta o informa de que no existe. */
    public RecyclingPlant getPlant(String plantId) {
        return plants.stream()
                .filter(plant -> plant.getId().equals(plantId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No existe la planta: " + plantId));
    }

    /** Consulta toneladas totales en los diez días permitidos: de hoy a hoy+9. */
    public double getTotalCapacity(String plantId, LocalDate date) {
        getPlant(plantId); // Primero se comprueba la existencia (404).

        LocalDate today = LocalDate.now();
        if (date == null || date.isBefore(today) || date.isAfter(today.plusDays(9))) {
            throw new BadRequestException("La fecha debe estar entre hoy y los próximos nueve días.");
        }

        return gateways.stream()
                .filter(gateway -> gateway.getPlantId().equals(plantId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No existe la planta: " + plantId))
                .getCapacity(date);
    }

    /** Notifica la asignación a la planta indicada (simulada en P1). */
    public void notifyAssignment(String plantId, LocalDate date, int containerCount, int packageCount) {
        getPlant(plantId);
        gateways.stream()
                .filter(gateway -> gateway.getPlantId().equals(plantId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No existe la planta: " + plantId))
                .notifyAssignment(date, containerCount, packageCount);
    }
}
