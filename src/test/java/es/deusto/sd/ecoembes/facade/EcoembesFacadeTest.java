package es.deusto.sd.ecoembes.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import es.deusto.sd.ecoembes.dto.AssignmentRequestDTO;
import es.deusto.sd.ecoembes.dto.ContainerCreateDTO;
import es.deusto.sd.ecoembes.dto.LoginRequestDTO;
import es.deusto.sd.ecoembes.dto.SensorReadingDTO;
import es.deusto.sd.ecoembes.domain.FillLevel;
import es.deusto.sd.ecoembes.exception.BadRequestException;
import es.deusto.sd.ecoembes.exception.ConflictException;
import es.deusto.sd.ecoembes.exception.NotFoundException;
import es.deusto.sd.ecoembes.exception.UnauthorizedException;
import es.deusto.sd.ecoembes.external.ContSocketGateway;
import es.deusto.sd.ecoembes.external.PlasSBGateway;
import es.deusto.sd.ecoembes.service.AssignmentAppService;
import es.deusto.sd.ecoembes.service.AuthAppService;
import es.deusto.sd.ecoembes.service.ContainerAppService;
import es.deusto.sd.ecoembes.service.PlantAppService;
import es.deusto.sd.ecoembes.state.ServerState;

/** Pruebas de las once operaciones de la fachada, sin contexto Spring. */
class EcoembesFacadeTest {

    private EcoembesFacade facade;
    private long token;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        AuthAppService auth = new AuthAppService(new ServerState());
        ContainerAppService containers = new ContainerAppService();
        PlantAppService plants = new PlantAppService(List.of(new PlasSBGateway(), new ContSocketGateway()));
        AssignmentAppService assignments = new AssignmentAppService(containers, plants);
        facade = new EcoembesFacade(auth, containers, plants, assignments);
        token = facade.login(new LoginRequestDTO("ana.garcia@ecoembes.es", "ana123")).getBody().token();
    }

    @Test
    void loginRetornaTokenYCredencialesInvalidasSeRechazan() {
        assertTrue(token > 0);
        assertEquals(HttpStatus.OK, facade.login(
                new LoginRequestDTO("luis.perez@ecoembes.es", "luis123")).getStatusCode());
        assertThrows(UnauthorizedException.class, () -> facade.login(
                new LoginRequestDTO("ana.garcia@ecoembes.es", "incorrecta")));
        assertThrows(BadRequestException.class, () -> facade.login(
                new LoginRequestDTO(" ", "ana123")));
    }

    @Test
    void logoutDevuelve204EInvalidaElToken() {
        assertEquals(HttpStatus.NO_CONTENT, facade.logout(token).getStatusCode());
        assertThrows(UnauthorizedException.class, () -> facade.getPlants(token));
        assertThrows(UnauthorizedException.class, () -> facade.logout(token));
    }

    @Test
    void crearContenedorDevuelve201ConCamposDeLecturaNulos() {
        var response = facade.createContainer(new ContainerCreateDTO(
                "CONT-011", "Calle Nueva 1", "48009", 1000), token);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        var dto = response.getBody();
        assertNotNull(dto);
        assertEquals("CONT-011", dto.id());
        assertNull(dto.packageCount());
        assertNull(dto.fillLevel());
        assertNull(dto.estimatedTons());
        assertNull(dto.readingDate());
        assertThrows(ConflictException.class, () -> facade.createContainer(
                new ContainerCreateDTO("CONT-011", "Calle Nueva 1", "48009", 1000), token));
    }

    @Test
    void getContainersDevuelveLosDiezSemillaConUltimaLectura() {
        var response = facade.getContainers(token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, response.getBody().size());
        var first = response.getBody().get(0);
        assertEquals("CONT-001", first.id());
        assertEquals(400, first.packageCount().intValue());
        assertEquals(today, first.readingDate());
        assertEquals(0.012, first.estimatedTons(), 1e-9);
        assertThrows(UnauthorizedException.class, () -> facade.getContainers(-1L));
    }

    @Test
    void getContainerReadingsRespetaLasFechasYEl404() {
        var response = facade.getContainerReadings(
                "CONT-001", today.minusDays(3), today.minusDays(1), token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(160, 240, 320),
                response.getBody().stream().map(r -> r.packageCount()).toList());
        assertThrows(NotFoundException.class, () -> facade.getContainerReadings(
                "NO-EXISTE", today, today.minusDays(1), token));
        assertThrows(BadRequestException.class, () -> facade.getContainerReadings(
                "CONT-001", today, today.minusDays(1), token));
    }

    @Test
    void getContainersByZoneUtilizaLecturaEnFechaYExcluyeSinLectura() {
        facade.createContainer(new ContainerCreateDTO("CONT-011", "Nueva", "48009", 1000), token);
        var response = facade.getContainersByZone("48009", today.minusDays(1), token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(320, response.getBody().get(0).packageCount().intValue());
        assertFalse(response.getBody().stream().anyMatch(c -> "CONT-011".equals(c.id())));
    }

    @Test
    void registrarLecturaEs201YDuplicadaEs409SinSobrescribir() {
        facade.createContainer(new ContainerCreateDTO("CONT-011", "Nueva", "48009", 1000), token);
        var response = facade.registerReading("CONT-011", new SensorReadingDTO(0, FillLevel.GREEN));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(0, response.getBody().packageCount());
        assertEquals(today, response.getBody().date());
        assertThrows(ConflictException.class,
                () -> facade.registerReading("CONT-011", new SensorReadingDTO(20, FillLevel.RED)));
        assertEquals(1, facade.getContainerReadings("CONT-011", today, today, token).getBody().size());
        assertEquals(0, facade.getContainers(token).getBody().stream()
                .filter(c -> "CONT-011".equals(c.id())).findFirst().orElseThrow().packageCount().intValue());
    }

    @Test
    void sensorNuloDevuelve400AntesDeConsultarElContenedor() {
        assertThrows(BadRequestException.class,
                () -> facade.registerReading("NO-EXISTE", new SensorReadingDTO(null, FillLevel.GREEN)));
        assertThrows(BadRequestException.class,
                () -> facade.registerReading("CONT-001", new SensorReadingDTO(0, null)));
        assertThrows(NotFoundException.class,
                () -> facade.registerReading("NO-EXISTE", new SensorReadingDTO(2, FillLevel.GREEN)));
    }

    @Test
    void getPlantsDevuelveDosPlantasConSusIdentificadores() {
        var response = facade.getPlants(token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of("PLASSB", "CONTSOCKET"),
                response.getBody().stream().map(p -> p.id()).toList());
    }

    @Test
    void getPlantCapacityDescuentaToneladasAsignadas() {
        assertEquals(0.4,
                facade.getPlantCapacity("PLASSB", today, token).getBody().availableTons(), 1e-9);
        facade.assignContainers("PLASSB", new AssignmentRequestDTO(
                today, List.of("CONT-003", "CONT-006")), token);
        var result = facade.getPlantCapacity("PLASSB", today, token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(0.4, result.getBody().totalTons(), 1e-9);
        assertEquals(0.1005, result.getBody().assignedTons(), 1e-9);
        assertEquals(0.2995, result.getBody().availableTons(), 1e-9);
    }

    @Test
    void asignacionValidaDevuelve201YAcreditaEmpleado() {
        var response = facade.assignContainers("PLASSB", new AssignmentRequestDTO(
                today, List.of("CONT-003", "CONT-006", "CONT-009")), token);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        var dto = response.getBody();
        assertEquals(4950, dto.totalPackages());
        assertEquals(0.1485, dto.totalTons(), 1e-9);
        assertEquals("ana.garcia@ecoembes.es", dto.assignedBy());
        assertNotNull(dto.createdAt());
        assertThrows(ConflictException.class, () -> facade.assignContainers(
                "CONTSOCKET", new AssignmentRequestDTO(today, List.of("CONT-003")), token));
    }

    @Test
    void asignacionConFechaNulaEs400YTokenInvalidoTienePrioridad401() {
        assertThrows(BadRequestException.class, () -> facade.assignContainers("PLASSB",
                new AssignmentRequestDTO(null, List.of("CONT-001")), token));
        assertThrows(UnauthorizedException.class, () -> facade.assignContainers("PLASSB",
                new AssignmentRequestDTO(null, List.of("CONT-001")), -1L));
    }

    @Test
    void getAssignmentsFiltraOpcionalmentePorFecha() {
        facade.assignContainers("PLASSB", new AssignmentRequestDTO(today, List.of("CONT-001")), token);
        facade.assignContainers("PLASSB", new AssignmentRequestDTO(
                today.plusDays(1), List.of("CONT-002")), token);
        assertEquals(2, facade.getAssignments("PLASSB", null, token).getBody().size());
        assertEquals(1, facade.getAssignments("PLASSB", today, token).getBody().size());
        assertEquals(0, facade.getAssignments("CONTSOCKET", null, token).getBody().size());
        assertThrows(NotFoundException.class,
                () -> facade.getAssignments("DESCONOCIDA", null, token));
    }
}
