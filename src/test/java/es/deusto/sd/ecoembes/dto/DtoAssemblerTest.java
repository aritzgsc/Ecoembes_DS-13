package es.deusto.sd.ecoembes.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import es.deusto.sd.ecoembes.domain.Assignment;
import es.deusto.sd.ecoembes.domain.Container;
import es.deusto.sd.ecoembes.domain.ContainerReading;
import es.deusto.sd.ecoembes.domain.Employee;
import es.deusto.sd.ecoembes.domain.FillLevel;
import es.deusto.sd.ecoembes.domain.RecyclingPlant;

/** Pruebas de conversión de dominio a DTO, sin arrancar Spring. */
class DtoAssemblerTest {

    @Test
    void contenedorSinLecturaMantieneNulosLosCuatroDatosDerivados() {
        Container container = new Container("CONT-011", "Calle Nueva 1", "48009", 1000);

        ContainerDTO dto = DtoAssembler.toContainerDTO(container, container.getLatestReading());

        assertEquals("CONT-011", dto.id());
        assertEquals("Calle Nueva 1", dto.address());
        assertEquals("48009", dto.postalCode());
        assertEquals(1000, dto.capacity());
        assertNull(dto.packageCount());
        assertNull(dto.fillLevel());
        assertNull(dto.estimatedTons());
        assertNull(dto.readingDate());
    }

    @Test
    void contenedorConLecturaYLecturaIndividualConservanDatos() {
        LocalDate date = LocalDate.of(2026, 11, 11);
        Container container = new Container("CONT-001", "Calle Ercilla 10", "48009", 2000);
        ContainerReading reading = new ContainerReading(date, 400, FillLevel.GREEN);
        container.addReading(reading);

        ContainerDTO dto = DtoAssembler.toContainerDTO(container, Optional.of(reading));
        ContainerReadingDTO readingDto = DtoAssembler.toReadingDTO(reading);

        assertEquals("CONT-001", dto.id());
        assertEquals(400, dto.packageCount().intValue());
        assertEquals(FillLevel.GREEN, dto.fillLevel());
        assertEquals(0.012, dto.estimatedTons(), 1e-12);
        assertEquals(date, dto.readingDate());
        assertEquals(date, readingDto.date());
        assertEquals(400, readingDto.packageCount());
        assertEquals(FillLevel.GREEN, readingDto.fillLevel());
    }

    @Test
    void plantaYCapacidadCalculanDisponibleSinModificarTotales() {
        RecyclingPlant plant = new RecyclingPlant("PLASSB", "PlasSB Ltd.");
        LocalDate date = LocalDate.of(2026, 11, 12);

        PlantDTO plantDto = DtoAssembler.toPlantDTO(plant);
        PlantCapacityDTO capacityDto = DtoAssembler.toPlantCapacityDTO(plant, date, 0.40, 0.1485);

        assertEquals("PLASSB", plantDto.id());
        assertEquals("PlasSB Ltd.", plantDto.name());
        assertEquals("PLASSB", capacityDto.plantId());
        assertEquals("PlasSB Ltd.", capacityDto.plantName());
        assertEquals(date, capacityDto.date());
        assertEquals(0.40, capacityDto.totalTons(), 1e-12);
        assertEquals(0.1485, capacityDto.assignedTons(), 1e-12);
        assertEquals(0.2515, capacityDto.availableTons(), 1e-12);
    }

    @Test
    void asignacionExponeIdsYEmailDelResponsableSinCredenciales() {
        RecyclingPlant plant = new RecyclingPlant("CONTSOCKET", "ContSocket Ltd.");
        Employee author = new Employee(1L, "Ana García", "ana.garcia@ecoembes.es", "hash-secreto");
        LocalDate date = LocalDate.of(2026, 11, 13);
        Container first = new Container("CONT-001", "Calle Ercilla 10", "48009", 2000);
        Container second = new Container("CONT-002", "Gran Vía 25", "48009", 2000);
        Assignment assignment = new Assignment(7L, plant, date, List.of(first, second), 1600, 0.048, author);

        AssignmentDTO dto = DtoAssembler.toAssignmentDTO(assignment);

        assertEquals(7L, dto.id());
        assertEquals("CONTSOCKET", dto.plantId());
        assertEquals("ContSocket Ltd.", dto.plantName());
        assertEquals(date, dto.date());
        assertEquals(List.of("CONT-001", "CONT-002"), dto.containerIds());
        assertEquals(2, dto.containerCount());
        assertEquals(1600, dto.totalPackages());
        assertEquals(0.048, dto.totalTons(), 1e-12);
        assertEquals("ana.garcia@ecoembes.es", dto.assignedBy());
        assertEquals(assignment.getCreatedAt(), dto.createdAt());
        assertFalse(dto.toString().contains("hash-secreto"));
        assertTrue(dto.containerIds().size() == dto.containerCount());
    }
}
