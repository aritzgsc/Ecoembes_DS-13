package es.deusto.sd.ecoembes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.deusto.sd.ecoembes.domain.Assignment;
import es.deusto.sd.ecoembes.domain.Employee;
import es.deusto.sd.ecoembes.domain.FillLevel;
import es.deusto.sd.ecoembes.exception.BadRequestException;
import es.deusto.sd.ecoembes.exception.ConflictException;
import es.deusto.sd.ecoembes.exception.NotFoundException;
import es.deusto.sd.ecoembes.external.ContSocketGateway;
import es.deusto.sd.ecoembes.external.PlasSBGateway;
import es.deusto.sd.ecoembes.external.RecyclingPlantGateway;

/** Pruebas unitarias de asignaciones sin levantar Spring. */
class AssignmentAppServiceTest {

    private static final double EPSILON = 1e-9;

    private ContainerAppService containerService;
    private PlantAppService plantService;
    private AssignmentAppService assignmentService;
    private Employee ana;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        ana = new Employee(1L, "Ana García", "ana.garcia@ecoembes.es", "hash");
        containerService = new ContainerAppService();
        plantService = new PlantAppService(List.of(new PlasSBGateway(), new ContSocketGateway()));
        assignmentService = new AssignmentAppService(containerService, plantService);
    }

    @Test
    void asignacionCorrectaRegistraCantidadesResponsableYCapacidadUsada() {
        Assignment assignment = assignmentService.assignContainers(ana, "PLASSB", today,
                List.of("CONT-003", "CONT-006", "CONT-009"));

        assertEquals(1L, assignment.getId());
        assertSame(ana, assignment.getAssignedBy());
        assertEquals("PLASSB", assignment.getPlant().getId());
        assertEquals(today, assignment.getDate());
        assertEquals(3, assignment.getContainers().size());
        assertEquals(4950, assignment.getTotalPackages());
        assertEquals(0.1485, assignment.getTotalTons(), EPSILON);
        assertEquals(0.1485, assignmentService.getAssignedTons("PLASSB", today), EPSILON);
        assertEquals(List.of(assignment), assignmentService.getAssignments("PLASSB", today));
    }

    @Test
    void noPermiteReasignarMismoContenedorEseDiaPeroSiOtroDia() {
        assignmentService.assignContainers(ana, "PLASSB", today, List.of("CONT-003"));

        assertThrows(ConflictException.class, () -> assignmentService.assignContainers(
                ana, "CONTSOCKET", today, List.of("CONT-003")));
        assertTrue(assignmentService.getAssignments("CONTSOCKET", null).isEmpty());

        LocalDate tomorrow = today.plusDays(1);
        Assignment tomorrowAssignment = assignmentService.assignContainers(
                ana, "CONTSOCKET", tomorrow, List.of("CONT-003"));
        assertEquals(tomorrow, tomorrowAssignment.getDate());
        assertEquals(0.0435, assignmentService.getAssignedTons("CONTSOCKET", tomorrow), EPSILON);
    }

    @Test
    void sinLecturaRechazaSinGuardarNiNotificarYTrasMedicionSePuedeAsignar() {
        RecordingGateway fakePlas = new RecordingGateway("PLASSB", "PlasSB Ltd.", 0.40);
        AssignmentAppService app = withGateways(List.of(fakePlas));
        containerService.createContainer("CONT-011", "Calle Nueva 1", "48009", 1000);

        assertThrows(ConflictException.class, () -> app.assignContainers(
                ana, "PLASSB", today, List.of("CONT-011")));
        assertTrue(app.getAssignments("PLASSB", null).isEmpty());
        assertEquals(0, fakePlas.notifications.size());

        containerService.registerReading("CONT-011", 125, FillLevel.GREEN);
        Assignment accepted = app.assignContainers(ana, "PLASSB", today, List.of("CONT-011"));
        assertEquals(125, accepted.getTotalPackages());
        assertEquals(0.00375, accepted.getTotalTons(), EPSILON);
        assertEquals(List.of("1:125:" + today), fakePlas.notifications);
    }

    @Test
    void siNoCabeTodaLaSeleccionNoRegistraNada() {
        List<String> all = containerService.getAllContainers().stream().map(c -> c.getId()).toList();
        assertEquals(10, all.size());

        assertThrows(ConflictException.class, () -> assignmentService.assignContainers(
                ana, "CONTSOCKET", today.plusDays(1), all));
        assertTrue(assignmentService.getAssignments("CONTSOCKET", null).isEmpty());
        assertEquals(0.0, assignmentService.getAssignedTons("CONTSOCKET", today.plusDays(1)), EPSILON);
    }

    @Test
    void capacidadCeroImpideAsignacionSinReservarToneladas() {
        LocalDate dayWithoutCapacity = today.plusDays(6);
        assertEquals(0.0, plantService.getTotalCapacity("PLASSB", dayWithoutCapacity), EPSILON);

        assertThrows(ConflictException.class, () -> assignmentService.assignContainers(
                ana, "PLASSB", dayWithoutCapacity, List.of("CONT-001")));
        assertTrue(assignmentService.getAssignments("PLASSB", dayWithoutCapacity).isEmpty());
    }

    @Test
    void listasNulaVaciaYConDuplicadosDevuelvenBadRequest() {
        RecordingGateway gateway = new RecordingGateway("PLASSB", "PlasSB Ltd.", 0.40);
        AssignmentAppService app = withGateways(List.of(gateway));

        assertThrows(BadRequestException.class, () -> app.assignContainers(ana, "PLASSB", today, null));
        assertThrows(BadRequestException.class, () -> app.assignContainers(ana, "PLASSB", today, List.of()));
        assertThrows(BadRequestException.class, () -> app.assignContainers(
                ana, "PLASSB", today, List.of("CONT-001", "CONT-001")));
        assertTrue(app.getAssignments("PLASSB", null).isEmpty());
        assertTrue(gateway.notifications.isEmpty());
    }

    @Test
    void prioridadDePlantaFechaYExistenciaDeContenedores() {
        RecordingGateway gateway = new RecordingGateway("PLASSB", "PlasSB Ltd.", 0.40);
        AssignmentAppService app = withGateways(List.of(gateway));

        // Una planta inexistente da 404 antes de examinar una fecha inválida.
        assertThrows(NotFoundException.class, () -> app.assignContainers(
                ana, "NO-EXISTE", null, List.of("CONT-001")));
        assertThrows(BadRequestException.class, () -> app.assignContainers(
                ana, "PLASSB", null, List.of("CONT-001")));
        assertThrows(BadRequestException.class, () -> app.assignContainers(
                ana, "PLASSB", today.plusDays(10), List.of("CONT-001")));
        assertThrows(NotFoundException.class, () -> app.assignContainers(
                ana, "PLASSB", today, List.of("CONT-999")));
        assertThrows(NotFoundException.class, () -> app.getAssignments("NO-EXISTE", null));
        assertTrue(app.getAssignments("PLASSB", null).isEmpty());
        assertTrue(gateway.notifications.isEmpty());
    }

    @Test
    void variasAsignacionesSumanToneladasYNotificanSoloALaPlantaElegida() {
        RecordingGateway fakePlas = new RecordingGateway("PLASSB", "PlasSB Ltd.", 0.40);
        RecordingGateway fakeCont = new RecordingGateway("CONTSOCKET", "ContSocket Ltd.", 0.30);
        AssignmentAppService app = withGateways(List.of(fakePlas, fakeCont));

        Assignment first = app.assignContainers(ana, "PLASSB", today, List.of("CONT-001"));
        Assignment second = app.assignContainers(ana, "PLASSB", today, List.of("CONT-002"));
        Assignment third = app.assignContainers(ana, "CONTSOCKET", today.plusDays(1),
                List.of("CONT-001"));

        assertEquals(1L, first.getId());
        assertEquals(2L, second.getId());
        assertEquals(3L, third.getId());
        assertEquals(0.048, app.getAssignedTons("PLASSB", today), EPSILON);
        assertEquals(0.012, app.getAssignedTons("CONTSOCKET", today.plusDays(1)), EPSILON);
        assertEquals(0.0, app.getAssignedTons("PLASSB", today.plusDays(1)), EPSILON);
        assertEquals(2, app.getAssignments("PLASSB", null).size());
        assertEquals(2, app.getAssignments("PLASSB", today).size());
        assertTrue(app.getAssignments("PLASSB", today.plusDays(1)).isEmpty());
        assertEquals(List.of("1:400:" + today, "1:1200:" + today), fakePlas.notifications);
        assertEquals(List.of("1:400:" + today.plusDays(1)), fakeCont.notifications);
    }

    private AssignmentAppService withGateways(List<RecyclingPlantGateway> gateways) {
        return new AssignmentAppService(containerService, new PlantAppService(gateways));
    }

    /** Sustituye el envío externo por una lista de llamadas observables en el test. */
    private static class RecordingGateway implements RecyclingPlantGateway {

        private final String id;
        private final String name;
        private final double capacity;
        private final List<String> notifications = new ArrayList<>();

        RecordingGateway(String id, String name, double capacity) {
            this.id = id;
            this.name = name;
            this.capacity = capacity;
        }

        @Override
        public String getPlantId() {
            return id;
        }

        @Override
        public String getPlantName() {
            return name;
        }

        @Override
        public double getCapacity(LocalDate date) {
            return capacity;
        }

        @Override
        public void notifyAssignment(LocalDate date, int containerCount, int packageCount) {
            notifications.add(containerCount + ":" + packageCount + ":" + date);
        }
    }
}
