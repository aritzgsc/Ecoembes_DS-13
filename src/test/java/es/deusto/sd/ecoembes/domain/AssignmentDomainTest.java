package es.deusto.sd.ecoembes.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.deusto.sd.ecoembes.dto.AssignmentDTO;
import es.deusto.sd.ecoembes.dto.AssignmentRequestDTO;

/** Pruebas unitarias del dominio de asignaciones y rutas conceptuales. */
class AssignmentDomainTest {

    @Test
    void assignmentGuardaDatosYFechaDeCreacion() {
        RecyclingPlant plant = new RecyclingPlant("PLASSB", "PlasSB Ltd.");
        Employee employee = new Employee(1L, "Ana García", "ana.garcia@ecoembes.es", "hash");
        Container container = new Container("CONT-001", "Calle Ercilla 10", "48009", 2000);
        LocalDate date = LocalDate.now();
        LocalDateTime before = LocalDateTime.now();

        Assignment assignment = new Assignment(7L, plant, date, List.of(container), 400, 0.012, employee);
        LocalDateTime after = LocalDateTime.now();

        assertEquals(7L, assignment.getId());
        assertSame(plant, assignment.getPlant());
        assertEquals(date, assignment.getDate());
        assertEquals(List.of(container), assignment.getContainers());
        assertEquals(400, assignment.getTotalPackages());
        assertEquals(0.012, assignment.getTotalTons(), 0.0000001);
        assertSame(employee, assignment.getAssignedBy());
        assertFalse(assignment.getCreatedAt().isBefore(before));
        assertFalse(assignment.getCreatedAt().isAfter(after));
    }

    @Test
    void assignmentConservaUnaCopiaDeLosContenedores() {
        Container container = new Container("CONT-001", "Calle Ercilla 10", "48009", 2000);
        List<Container> originals = new ArrayList<>();
        originals.add(container);
        Assignment assignment = new Assignment(1L, new RecyclingPlant("PLASSB", "PlasSB"),
                LocalDate.now(), originals, 400, 0.012,
                new Employee(1L, "Ana", "ana@ecoembes.es", "hash"));
        originals.clear();

        assertEquals(1, assignment.getContainers().size());
        assertThrows(UnsupportedOperationException.class,
                () -> assignment.getContainers().clear());
    }

    @Test
    void requestDtoPermiteFechaNulaParaValidarlaDespues() {
        AssignmentRequestDTO request = new AssignmentRequestDTO(null, List.of("CONT-001"));
        assertEquals(null, request.date());
        assertEquals(List.of("CONT-001"), request.containerIds());

        AssignmentDTO dto = new AssignmentDTO(1L, "PLASSB", "PlasSB", LocalDate.now(),
                List.of("CONT-001"), 1, 400, 0.012, "ana@ecoembes.es", LocalDateTime.now());
        assertEquals(1, dto.containerCount());
        assertEquals("ana@ecoembes.es", dto.assignedBy());
    }

    @Test
    void routeExigeContenedoresYGuardaSusAtributos() {
        LocalDate date = LocalDate.now();
        Container container = new Container("CONT-001", "Calle Ercilla 10", "48009", 2000);
        assertThrows(IllegalArgumentException.class, () -> new Route("R1", date, 25, null));
        assertThrows(IllegalArgumentException.class, () -> new Route("R1", date, 25, List.of()));

        Route route = new Route("R1", date, 25, List.of(container));
        assertEquals("R1", route.getId());
        assertEquals(date, route.getDate());
        assertEquals(25, route.getDurationMinutes());
        assertEquals(List.of(container), route.getContainers());
        assertThrows(UnsupportedOperationException.class, () -> route.getContainers().clear());
    }

    @Test
    void unaRutaPuedeEstarEnVariosCamionesSinRelacionInversa() {
        Container container = new Container("CONT-001", "Calle Ercilla 10", "48009", 2000);
        Route route = new Route("R1", LocalDate.now(), 20, List.of(container));
        Truck truck1 = new Truck("TRUCK-01");
        Truck truck2 = new Truck("TRUCK-02");

        assertTrue(truck1.getRoutes().isEmpty());
        assertEquals("TRUCK-01", truck1.getId());
        truck1.getRoutes().add(route);
        truck2.getRoutes().add(route);
        assertSame(route, truck1.getRoutes().get(0));
        assertSame(route, truck2.getRoutes().get(0));
    }
}
