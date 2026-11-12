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
import es.deusto.sd.ecoembes.dto.ErrorDTO;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Operation(summary = "Iniciar sesión", description = "Autentica al personal mediante email y contraseña. Para la demostración local: ana.garcia@ecoembes.es / ana123. Devuelve un token numérico que se envía como parámetro query en las operaciones del personal.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión iniciada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        long token = authAppService.login(request.email(), request.password());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    /** 2. Invalida un token; no es necesario validar la sesión dos veces. */
    @PostMapping("/auth/logout")
    @Operation(summary = "Cerrar sesión", description = "Invalida el token recibido.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sesión cerrada", content = @Content),
            @ApiResponse(responseCode = "400", description = "Token ausente o mal formado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token no activo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<Void> logout(@Parameter(description = "Token numérico de una sesión activa, obtenido mediante login. Se invalida con logout o al reiniciar.", example = "1794400000000")
            @RequestParam long token) {
        authAppService.logout(token);
        return ResponseEntity.noContent().build();
    }

    /** 3. Crea un contenedor vacío; la primera lectura procede del sensor. */
    @PostMapping("/containers")
    @Operation(summary = "Crear contenedor", description = "Da de alta un identificador único, dirección, código postal de cinco dígitos y capacidad positiva. packageCount, fillLevel, estimatedTons y readingDate son null hasta la primera lectura del sensor.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contenedor creado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContainerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token no activo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Identificador duplicado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<ContainerDTO> createContainer(@RequestBody ContainerCreateDTO request,
            @Parameter(description = "Token numérico de una sesión activa, obtenido mediante login. Se invalida con logout o al reiniciar.", example = "1794400000000")
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
            @ApiResponse(responseCode = "200", description = "Listado recuperado",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ContainerDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Token mal formado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token no activo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<List<ContainerDTO>> getContainers(@Parameter(description = "Token numérico de una sesión activa, obtenido mediante login. Se invalida con logout o al reiniciar.", example = "1794400000000")
            @RequestParam long token) {
        authAppService.validateToken(token);
        List<ContainerDTO> result = containerAppService.getAllContainers().stream()
                .map(container -> DtoAssembler.toContainerDTO(container, container.getLatestReading()))
                .toList();
        return ResponseEntity.ok(result);
    }

    /** 5. Devuelve las lecturas de un contenedor en un intervalo inclusivo. */
    @GetMapping("/containers/{containerId}/readings")
    @Operation(summary = "Consultar historial de un contenedor", description = "Devuelve las lecturas del intervalo inclusivo [from, to], ordenadas por fecha. Un intervalo sin lecturas devuelve una lista vacía.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial recuperado",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ContainerReadingDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Fechas inválidas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token no activo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Contenedor inexistente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<List<ContainerReadingDTO>> getContainerReadings(
            @Parameter(description = "Identificador del contenedor.", example = "CONT-001")
            @PathVariable String containerId,
            @Parameter(description = "Inicio inclusivo del historial, con formato ISO yyyy-MM-dd.", example = "2026-11-07")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Fin inclusivo del historial; no puede preceder a from.", example = "2026-11-11")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "Token numérico de una sesión activa, obtenido mediante login. Se invalida con logout o al reiniciar.", example = "1794400000000")
            @RequestParam long token) {
        authAppService.validateToken(token);
        List<ContainerReadingDTO> result = containerAppService.getReadings(containerId, from, to)
                .stream().map(DtoAssembler::toReadingDTO).toList();
        return ResponseEntity.ok(result);
    }

    /** 6. Consulta la última lectura a una fecha para los contenedores de una zona. */
    @GetMapping("/zones/{postalCode}/containers")
    @Operation(summary = "Consultar contenedores por zona y fecha", description = "Devuelve los contenedores de la zona con su última lectura en la fecha indicada o en una fecha anterior. Omite los contenedores sin medición hasta esa fecha.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta realizada",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ContainerDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Zona o fecha inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token no activo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<List<ContainerDTO>> getContainersByZone(
            @Parameter(description = "Código postal de cinco dígitos.", example = "48009")
            @PathVariable String postalCode,
            @Parameter(description = "Fecha de consulta ISO yyyy-MM-dd. Se utiliza la última lectura registrada hasta ese día.", example = "2026-11-11")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Token numérico de una sesión activa, obtenido mediante login. Se invalida con logout o al reiniciar.", example = "1794400000000")
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
    @Operation(summary = "Registrar lectura del sensor", description = "Operación simulada del sensor, sin token del personal. La fecha se obtiene del servidor. Acepta entre cero y la capacidad del contenedor y un nivel GREEN, ORANGE o RED. La primera lectura del día devuelve 201; un duplicado devuelve 409 y conserva la lectura original.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lectura registrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContainerReadingDTO.class))),
            @ApiResponse(responseCode = "400", description = "Lectura inválida, nula o ilegible",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Contenedor inexistente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe lectura para el día actual",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<ContainerReadingDTO> registerReading(
            @Parameter(description = "Identificador del contenedor.", example = "CONT-001")
            @PathVariable String containerId,
            @RequestBody SensorReadingDTO request) {
        ContainerReading reading = containerAppService.registerReading(
                containerId, request.packageCount(), request.fillLevel());
        return ResponseEntity.status(201).body(DtoAssembler.toReadingDTO(reading));
    }

    /** 8. Lista las plantas disponibles. */
    @GetMapping("/plants")
    @Operation(summary = "Consultar plantas de reciclaje")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plantas recuperadas",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlantDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Token mal formado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token no activo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<List<PlantDTO>> getPlants(@Parameter(description = "Token numérico de una sesión activa, obtenido mediante login. Se invalida con logout o al reiniciar.", example = "1794400000000")
            @RequestParam long token) {
        authAppService.validateToken(token);
        return ResponseEntity.ok(plantAppService.getPlants().stream()
                .map(DtoAssembler::toPlantDTO).toList());
    }

    /** 9. Consulta la capacidad total, asignada y disponible de una planta. */
    @GetMapping("/plants/{plantId}/capacity")
    @Operation(summary = "Consultar capacidad de una planta", description = "Capacidades simuladas en toneladas para hoy y los nueve días siguientes, según la fecha del servidor. availableTons = totalTons - assignedTons.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Capacidad recuperada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlantCapacityDTO.class))),
            @ApiResponse(responseCode = "400", description = "Fecha inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token no activo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Planta inexistente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<PlantCapacityDTO> getPlantCapacity(
            @Parameter(description = "Identificador de la planta simulada: PLASSB o CONTSOCKET.", example = "PLASSB")
            @PathVariable String plantId,
            @Parameter(description = "Fecha de capacidad ISO yyyy-MM-dd, entre hoy y hoy+9 según el servidor.")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Token numérico de una sesión activa, obtenido mediante login. Se invalida con logout o al reiniciar.", example = "1794400000000")
            @RequestParam long token) {
        authAppService.validateToken(token);
        RecyclingPlant plant = plantAppService.getPlant(plantId);
        double totalTons = plantAppService.getTotalCapacity(plantId, date);
        double assignedTons = assignmentAppService.getAssignedTons(plantId, date);
        return ResponseEntity.ok(DtoAssembler.toPlantCapacityDTO(plant, date, totalTons, assignedTons));
    }

    /** 10. Delega toda la validación y registro de la asignación al servicio. */
    @PostMapping("/plants/{plantId}/assignments")
    @Operation(summary = "Asignar contenedores a una planta", description = "Asignación todo-o-nada para hoy o los nueve días siguientes. La selección debe ser no vacía, sin IDs repetidos y con lecturas disponibles. Un contenedor solo puede asignarse una vez por fecha, incluso entre plantas. La carga usa la última lectura y 0,03 kg por envase; la notificación a la planta se simula.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Asignación registrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AssignmentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Fecha o selección inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token no activo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Planta o contenedor inexistente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto de asignación o capacidad",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<AssignmentDTO> assignContainers(
            @Parameter(description = "Identificador de la planta simulada: PLASSB o CONTSOCKET.", example = "PLASSB")
            @PathVariable String plantId,
            @RequestBody AssignmentRequestDTO request,
            @Parameter(description = "Token numérico de una sesión activa, obtenido mediante login. Se invalida con logout o al reiniciar.", example = "1794400000000")
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
    @Operation(summary = "Consultar asignaciones de una planta", description = "Devuelve las asignaciones de la planta y su auditoría: empleado responsable y fecha de registro. date filtra por fecha; al omitirlo se devuelve todo el registro en memoria.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asignaciones recuperadas",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AssignmentDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Fecha mal formada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token no activo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Planta inexistente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<List<AssignmentDTO>> getAssignments(
            @Parameter(description = "Identificador de la planta simulada: PLASSB o CONTSOCKET.", example = "PLASSB")
            @PathVariable String plantId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Token numérico de una sesión activa, obtenido mediante login. Se invalida con logout o al reiniciar.", example = "1794400000000")
            @RequestParam long token) {
        authAppService.validateToken(token);
        return ResponseEntity.ok(assignmentAppService.getAssignments(plantId, date).stream()
                .map(DtoAssembler::toAssignmentDTO).toList());
    }
}
