package es.deusto.sd.ecoembes.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Asignación de contenedores a una planta, registrada para su auditoría. */
public class Assignment {

    private final long id;
    private final RecyclingPlant plant;
    private final LocalDate date;
    private final List<Container> containers;
    private final int totalPackages;
    private final double totalTons;
    private final Employee assignedBy;
    private final LocalDateTime createdAt;

    public Assignment(long id, RecyclingPlant plant, LocalDate date,
            List<Container> containers, int totalPackages, double totalTons, Employee assignedBy) {
        this.id = id;
        this.plant = plant;
        this.date = date;
        this.containers = List.copyOf(containers);
        this.totalPackages = totalPackages;
        this.totalTons = totalTons;
        this.assignedBy = assignedBy;
        this.createdAt = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public RecyclingPlant getPlant() {
        return plant;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public int getTotalPackages() {
        return totalPackages;
    }

    public double getTotalTons() {
        return totalTons;
    }

    public Employee getAssignedBy() {
        return assignedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
