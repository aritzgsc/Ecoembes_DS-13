package es.deusto.sd.ecoembes.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import es.deusto.sd.ecoembes.domain.Assignment;
import es.deusto.sd.ecoembes.domain.Container;
import es.deusto.sd.ecoembes.domain.ContainerReading;
import es.deusto.sd.ecoembes.domain.Employee;
import es.deusto.sd.ecoembes.domain.RecyclingPlant;
import es.deusto.sd.ecoembes.exception.BadRequestException;
import es.deusto.sd.ecoembes.exception.ConflictException;

/** Registra asignaciones de contenedores a plantas, simuladas en memoria. */
@Service
public class AssignmentAppService {

    private final ContainerAppService containerAppService;
    private final PlantAppService plantAppService;
    private final List<Assignment> assignments = new ArrayList<>();
    private long nextId = 1L;

    public AssignmentAppService(ContainerAppService containerAppService, PlantAppService plantAppService) {
        this.containerAppService = containerAppService;
        this.plantAppService = plantAppService;
    }

    /** Valida capacidad, lecturas y duplicados antes de guardar y notificar. */
    public synchronized Assignment assignContainers(Employee by, String plantId, LocalDate date,
            List<String> containerIds) {
        // 1. Planta y fecha: primero 404 de planta, después 400 de fecha.
        RecyclingPlant plant = plantAppService.getPlant(plantId);
        double totalCapacity = plantAppService.getTotalCapacity(plantId, date);

        // 2. La petición debe contener una lista de ids no vacía y sin repetidos.
        if (containerIds == null || containerIds.isEmpty()
                || new HashSet<>(containerIds).size() != containerIds.size()) {
            throw new BadRequestException("Indica contenedores sin repetir identificadores.");
        }

        // 3. Todos los contenedores solicitados deben existir.
        List<Container> containers = new ArrayList<>();
        for (String id : containerIds) {
            containers.add(containerAppService.getContainer(id));
        }

        // 4. Un contenedor no puede asignarse dos veces en la misma fecha,
        // incluso si la segunda asignación es para una planta diferente.
        Set<String> alreadyAssigned = new HashSet<>();
        for (Assignment assignment : assignments) {
            if (assignment.getDate().equals(date)) {
                for (Container container : assignment.getContainers()) {
                    alreadyAssigned.add(container.getId());
                }
            }
        }
        for (Container container : containers) {
            if (alreadyAssigned.contains(container.getId())) {
                throw new ConflictException("El contenedor " + container.getId()
                        + " ya está asignado para la fecha " + date + ".");
            }
        }

        // 5. La cantidad se obtiene de la última lectura real; nunca se inventa una.
        int totalPackages = 0;
        double totalTons = 0.0;
        for (Container container : containers) {
            ContainerReading reading = container.getLatestReading()
                    .orElseThrow(() -> new ConflictException("El contenedor " + container.getId()
                            + " no tiene lecturas registradas."));
            totalPackages += reading.getPackageCount();
            totalTons += reading.getTons();
        }

        // 6. La capacidad disponible descuenta asignaciones previas a esa planta y fecha.
        double available = totalCapacity - getAssignedTons(plantId, date);
        if (totalTons > available + 1e-9) {
            throw new ConflictException("La planta no tiene capacidad suficiente para la asignación.");
        }

        // 7. Solo después de superar todas las validaciones se registra y notifica.
        Assignment assignment = new Assignment(nextId++, plant, date, containers,
                totalPackages, totalTons, by);
        assignments.add(assignment);
        plantAppService.notifyAssignment(plantId, date, containers.size(), totalPackages);
        return assignment;
    }

    /** Lista las asignaciones de una planta, con filtro opcional por fecha. */
    public synchronized List<Assignment> getAssignments(String plantId, LocalDate dateOrNull) {
        plantAppService.getPlant(plantId);
        return assignments.stream()
                .filter(assignment -> assignment.getPlant().getId().equals(plantId))
                .filter(assignment -> dateOrNull == null || assignment.getDate().equals(dateOrNull))
                .toList();
    }

    /** Suma las toneladas ya reservadas para una planta y fecha concretas. */
    public synchronized double getAssignedTons(String plantId, LocalDate date) {
        return assignments.stream()
                .filter(assignment -> assignment.getPlant().getId().equals(plantId))
                .filter(assignment -> assignment.getDate().equals(date))
                .mapToDouble(Assignment::getTotalTons)
                .sum();
    }
}
