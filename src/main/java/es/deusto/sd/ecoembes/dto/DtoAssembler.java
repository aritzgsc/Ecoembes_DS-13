package es.deusto.sd.ecoembes.dto;

import java.time.LocalDate;
import java.util.Optional;

import es.deusto.sd.ecoembes.domain.Assignment;
import es.deusto.sd.ecoembes.domain.Container;
import es.deusto.sd.ecoembes.domain.ContainerReading;
import es.deusto.sd.ecoembes.domain.RecyclingPlant;

/** Convierte las entidades del dominio en los DTO devueltos por la fachada REST. */
public final class DtoAssembler {

    private DtoAssembler() {
        // Clase de utilidad: solo se utilizan sus métodos estáticos.
    }

    /** Los cuatro valores derivados permanecen en null si aún no existe lectura. */
    public static ContainerDTO toContainerDTO(Container container, Optional<ContainerReading> reading) {
        if (reading.isEmpty()) {
            return new ContainerDTO(container.getId(), container.getAddress(),
                    container.getPostalCode(), container.getCapacity(),
                    null, null, null, null);
        }
        ContainerReading current = reading.get();
        return new ContainerDTO(container.getId(), container.getAddress(),
                container.getPostalCode(), container.getCapacity(),
                current.getPackageCount(), current.getFillLevel(),
                current.getTons(), current.getDate());
    }

    /** Convierte una medición conservando exactamente su fecha, cantidad y nivel. */
    public static ContainerReadingDTO toReadingDTO(ContainerReading reading) {
        return new ContainerReadingDTO(reading.getDate(),
                reading.getPackageCount(), reading.getFillLevel());
    }

    /** Representación pública mínima de una planta. */
    public static PlantDTO toPlantDTO(RecyclingPlant plant) {
        return new PlantDTO(plant.getId(), plant.getName());
    }

    /** La capacidad disponible es un dato derivado de las dos cantidades recibidas. */
    public static PlantCapacityDTO toPlantCapacityDTO(RecyclingPlant plant, LocalDate date,
            double totalTons, double assignedTons) {
        return new PlantCapacityDTO(plant.getId(), plant.getName(), date,
                totalTons, assignedTons, totalTons - assignedTons);
    }

    /** Expone los IDs de los contenedores y el email del empleado, nunca su contraseña. */
    public static AssignmentDTO toAssignmentDTO(Assignment assignment) {
        return new AssignmentDTO(assignment.getId(),
                assignment.getPlant().getId(), assignment.getPlant().getName(),
                assignment.getDate(),
                assignment.getContainers().stream().map(Container::getId).toList(),
                assignment.getContainers().size(), assignment.getTotalPackages(),
                assignment.getTotalTons(), assignment.getAssignedBy().getEmail(),
                assignment.getCreatedAt());
    }
}
