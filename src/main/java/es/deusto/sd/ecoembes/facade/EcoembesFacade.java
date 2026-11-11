package es.deusto.sd.ecoembes.facade;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.deusto.sd.ecoembes.domain.Container;
import es.deusto.sd.ecoembes.domain.ContainerReading;
import es.deusto.sd.ecoembes.domain.Employee;
import es.deusto.sd.ecoembes.domain.RecyclingPlant;
import es.deusto.sd.ecoembes.dto.AssignmentDTO;
import es.deusto.sd.ecoembes.dto.AssignmentRequestDTO;
import es.deusto.sd.ecoembes.dto.ContainerCreateDTO;
import es.deusto.sd.ecoembes.dto.ContainerDTO;
import es.deusto.sd.ecoembes.dto.ContainerReadingDTO;
import es.deusto.sd.ecoembes.dto.DtoAssembler;
import es.deusto.sd.ecoembes.dto.LoginRequestDTO;
import es.deusto.sd.ecoembes.dto.LoginResponseDTO;
import es.deusto.sd.ecoembes.dto.PlantCapacityDTO;
import es.deusto.sd.ecoembes.dto.PlantDTO;
import es.deusto.sd.ecoembes.dto.SensorReadingDTO;
import es.deusto.sd.ecoembes.exception.BadRequestException;
import es.deusto.sd.ecoembes.service.AssignmentAppService;
import es.deusto.sd.ecoembes.service.AuthAppService;
import es.deusto.sd.ecoembes.service.ContainerAppService;
import es.deusto.sd.ecoembes.service.PlantAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Fachada REST del Servidor Ecoembes (Prototipo 1): 11 operaciones */
@RestController
@RequestMapping("/api")
@Tag(name = "Ecoembes", description = "Operaciones del Servidor Ecoembes")
public class EcoembesFacade {

    private final AuthAppService authAppService;
    private final ContainerAppService containerAppService;
    private final PlantAppService plantAppService;
    private final AssignmentAppService assignmentAppService;

    public EcoembesFacade(AuthAppService authAppService, ContainerAppService containerAppService,
            PlantAppService plantAppService, AssignmentAppService assignmentAppService) {
        this.authAppService = authAppService;
        this.containerAppService = containerAppService;
        this.plantAppService = plantAppService;
        this.assignmentAppService = assignmentAppService;
    }

    /** 1. Abre una sesión y devuelve un token timestamp. */
    @PostMapping("/auth/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al personal mediante email y contraseña.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión iniciada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        long token = authAppService.login(request.email(), request.password());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    /** 2. Invalida un token; no es necesario validar la sesión dos veces. */
    @PostMapping("/auth/logout")
    @Operation(summary = "Cerrar sesión", description = "Invalida el token recibido.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sesión cerrada"),
            @ApiResponse(responseCode = "400", description = "Token ausente o mal formado"),
            @ApiResponse(responseCode = "401", description = "Token no activo")
    })
    public ResponseEntity<Void> logout(@RequestParam long token) {
        authAppService.logout(token);
        return ResponseEntity.noContent().build();
    }

    /** 3. Crea un contenedor vacío; la primera lectura procede del sensor. */
    @PostMapping("/containers")
    @Operation(summary = "Crear contenedor", description = "Da de alta un contenedor sin lecturas iniciales.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contenedor creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token no activo"),
            @ApiResponse(responseCode = "409", description = "Identificador duplicado")
    })
    public ResponseEntity<ContainerDTO> createContainer(@RequestBody ContainerCreateDTO request,
            @RequestParam long token) {
        authAppService.validateToken(token);
        Container container = containerAppService.createContainer(
                request.id(), request.address(), request.postalCode(), request.capacity());
        return ResponseEntity.status(201)
                .body(DtoAssembler.toContainerDTO(container, container.getLatestReading()));
    }

    /** 4. Lista todos los contenedores con su última lectura, si existe. */
    @GetMapping("/containers")
    @Operation(summary = "Consultar contenedores", description = "Devuelve la información actual de los contenedores.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado recuperado"),
            @ApiResponse(responseCode = "400", description = "Token mal formado"),
            @ApiResponse(responseCode = "401", description = "Token no activo")
    })
    public ResponseEntity<List<ContainerDTO>> getContainers(@RequestParam long token) {
        authAppService.validateToken(token);
        List<ContainerDTO> result = containerAppService.getAllContainers().stream()
                .map(container -> DtoAssembler.toContainerDTO(container, container.getLatestReading()))
                .toList();
        return ResponseEntity.ok(result);
    }

    /** 5. Devuelve las lecturas de un contenedor en un intervalo inclusivo. */
    @GetMapping("/containers/{containerId}/readings")
    @Operation(summary = "Consultar historial de un contenedor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial recuperado"),
            @ApiResponse(responseCode = "400", description = "Fechas inválidas"),
            @ApiResponse(responseCode = "401", description = "Token no activo"),
            @ApiResponse(responseCode = "404", description = "Contenedor inexistente")
    })
    public ResponseEntity<List<ContainerReadingDTO>> getContainerReadings(
            @PathVariable String containerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam long token) {
        authAppService.validateToken(token);
        List<ContainerReadingDTO> result = containerAppService.getReadings(containerId, from, to)
                .stream().map(DtoAssembler::toReadingDTO).toList();
        return ResponseEntity.ok(result);
    }

    /** 6. Consulta la última lectura a una fecha para los contenedores de una zona. */
    @GetMapping("/zones/{postalCode}/containers")
    @Operation(summary = "Consultar contenedores por zona y fecha")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta realizada"),
            @ApiResponse(responseCode = "400", description = "Zona o fecha inválida"),
            @ApiResponse(responseCode = "401", description = "Token no activo")
    })
    public ResponseEntity<List<ContainerDTO>> getContainersByZone(
            @PathVariable String postalCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam long token) {
        authAppService.validateToken(token);
        List<ContainerDTO> result = containerAppService.getContainersByZone(postalCode, date)
                .stream()
                .map(container -> DtoAssembler.toContainerDTO(container, container.getReadingAt(date)))
                .toList();
        return ResponseEntity.ok(result);
    }

    /** 7. El sensor no usa token; un duplicado del día produce 409 sin sobrescritura. */
    @PostMapping("/containers/{containerId}/readings")
    @Operation(summary = "Registrar lectura del sensor", description = "Primera lectura del día: 201; repetida: 409.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lectura registrada"),
            @ApiResponse(responseCode = "400", description = "Lectura inválida, nula o ilegible"),
            @ApiResponse(responseCode = "404", description = "Contenedor inexistente"),
            @ApiResponse(responseCode = "409", description = "Ya existe lectura para el día actual")
    })
    public ResponseEntity<ContainerReadingDTO> registerReading(
            @PathVariable String containerId,
            @RequestBody SensorReadingDTO request) {
        // Antes de convertir Integer a int, distinguir explícitamente null de 0.
        if (request.packageCount() == null || request.fillLevel() == null) {
            throw new BadRequestException("La cantidad de envases y el nivel de llenado son obligatorios.");
        }
        ContainerReading reading = containerAppService.registerReading(
                containerId, request.packageCount(), request.fillLevel());
        return ResponseEntity.status(201).body(DtoAssembler.toReadingDTO(reading));
    }

    /** 8. Lista las plantas disponibles. */
    @GetMapping("/plants")
    @Operation(summary = "Consultar plantas de reciclaje")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plantas recuperadas"),
            @ApiResponse(responseCode = "400", description = "Token mal formado"),
            @ApiResponse(responseCode = "401", description = "Token no activo")
    })
    public ResponseEntity<List<PlantDTO>> getPlants(@RequestParam long token) {
        authAppService.validateToken(token);
        return ResponseEntity.ok(plantAppService.getPlants().stream()
                .map(DtoAssembler::toPlantDTO).toList());
    }

    /** 9. Consulta la capacidad total, asignada y disponible de una planta. */
    @GetMapping("/plants/{plantId}/capacity")
    @Operation(summary = "Consultar capacidad de una planta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Capacidad recuperada"),
            @ApiResponse(responseCode = "400", description = "Fecha inválida"),
            @ApiResponse(responseCode = "401", description = "Token no activo"),
            @ApiResponse(responseCode = "404", description = "Planta inexistente")
    })
    public ResponseEntity<PlantCapacityDTO> getPlantCapacity(
            @PathVariable String plantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam long token) {
        authAppService.validateToken(token);
        RecyclingPlant plant = plantAppService.getPlant(plantId);
        double totalTons = plantAppService.getTotalCapacity(plantId, date);
        double assignedTons = assignmentAppService.getAssignedTons(plantId, date);
        return ResponseEntity.ok(DtoAssembler.toPlantCapacityDTO(plant, date, totalTons, assignedTons));
    }

    /** 10. Delega toda la validación y registro de la asignación al servicio. */
    @PostMapping("/plants/{plantId}/assignments")
    @Operation(summary = "Asignar contenedores a una planta", description = "Asignación todo-o-nada, con notificación simulada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Asignación registrada"),
            @ApiResponse(responseCode = "400", description = "Fecha o selección inválida"),
            @ApiResponse(responseCode = "401", description = "Token no activo"),
            @ApiResponse(responseCode = "404", description = "Planta o contenedor inexistente"),
            @ApiResponse(responseCode = "409", description = "Conflicto de asignación o capacidad")
    })
    public ResponseEntity<AssignmentDTO> assignContainers(
            @PathVariable String plantId,
            @RequestBody AssignmentRequestDTO request,
            @RequestParam long token) {
        Employee by = authAppService.validateToken(token);
        if (request.date() == null) {
            throw new BadRequestException("La fecha de asignación es obligatoria.");
        }
        return ResponseEntity.status(201).body(DtoAssembler.toAssignmentDTO(
                assignmentAppService.assignContainers(by, plantId, request.date(), request.containerIds())));
    }

    /** 11. Lista asignaciones; el parámetro date es opcional. */
    @GetMapping("/plants/{plantId}/assignments")
    @Operation(summary = "Consultar asignaciones de una planta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asignaciones recuperadas"),
            @ApiResponse(responseCode = "400", description = "Fecha mal formada"),
            @ApiResponse(responseCode = "401", description = "Token no activo"),
            @ApiResponse(responseCode = "404", description = "Planta inexistente")
    })
    public ResponseEntity<List<AssignmentDTO>> getAssignments(
            @PathVariable String plantId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam long token) {
        authAppService.validateToken(token);
        return ResponseEntity.ok(assignmentAppService.getAssignments(plantId, date).stream()
                .map(DtoAssembler::toAssignmentDTO).toList());
    }
}
