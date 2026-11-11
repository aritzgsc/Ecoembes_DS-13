package es.deusto.sd.ecoembes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.deusto.sd.ecoembes.domain.Container;
import es.deusto.sd.ecoembes.domain.ContainerReading;
import es.deusto.sd.ecoembes.domain.FillLevel;
import es.deusto.sd.ecoembes.exception.BadRequestException;
import es.deusto.sd.ecoembes.exception.ConflictException;
import es.deusto.sd.ecoembes.exception.NotFoundException;

/** Pruebas del servicio sin arrancar el contexto de Spring. */
class ContainerAppServiceTest {

    private ContainerAppService service;

    @BeforeEach
    void setUp() {
        service = new ContainerAppService();
    }

    @Test
    void semillaContieneDiezContenedoresYCincoLecturasCadaUno() {
        List<Container> containers = service.getAllContainers();
        assertEquals(10, containers.size());
        for (Container container : containers) {
            assertEquals(5, container.getReadings().size());
            assertEquals(LocalDate.now(), container.getLatestReading().orElseThrow().getDate());
        }
        int countToday = containers.stream()
                .mapToInt(c -> c.getLatestReading().orElseThrow().getPackageCount())
                .sum();
        assertEquals(11950, countToday);
        assertEquals(0.3585, countToday * ContainerReading.PACKAGE_WEIGHT_KG / 1000, 1e-12);
    }

    @Test
    void historialDeCont001EsCorrectoEInclusivo() {
        LocalDate today = LocalDate.now();
        Container container = service.getContainer("CONT-001");
        List<ContainerReading> readings = service.getReadings("CONT-001", today.minusDays(4), today);
        assertEquals(List.of(80, 160, 240, 320, 400),
                readings.stream().map(ContainerReading::getPackageCount).toList());
        assertTrue(readings.stream().allMatch(r -> r.getFillLevel() == FillLevel.GREEN));
        assertEquals(400, container.getLatestReading().orElseThrow().getPackageCount());
        assertEquals(320, container.getReadingAt(today.minusDays(1)).orElseThrow().getPackageCount());
        assertTrue(container.getReadingAt(today.minusDays(5)).isEmpty());
        assertEquals(3, service.getReadings("CONT-001", today.minusDays(3), today.minusDays(1)).size());
        assertEquals(0.012, container.getLatestReading().orElseThrow().getTons(), 1e-12);
    }

    @Test
    void nuevoContenedorNoTieneLecturasYNoApareceEnZonaHastaMedirse() {
        LocalDate today = LocalDate.now();
        Container container = service.createContainer("CONT-011", "Calle Inventada 1", "48009", 1000);
        assertTrue(container.getLatestReading().isEmpty());
        assertFalse(service.getContainersByZone("48009", today).contains(container));

        ContainerReading reading = service.registerReading("CONT-011", 125, FillLevel.GREEN);
        assertEquals(today, reading.getDate());
        assertEquals(125, container.getLatestReading().orElseThrow().getPackageCount());
        assertTrue(service.getContainersByZone("48009", today).contains(container));
    }

    @Test
    void altaRechazaDuplicadosYCamposInvalidos() {
        assertThrows(ConflictException.class, () -> service.createContainer("CONT-001", "Calle 1", "48009", 1000));
        assertThrows(BadRequestException.class, () -> service.createContainer("X", "Calle 1", "48009", 0));
        assertThrows(BadRequestException.class, () -> service.createContainer("X", "Calle 1", "4800", 1000));
        assertThrows(BadRequestException.class, () -> service.createContainer(" ", "Calle 1", "48009", 1000));
        assertThrows(BadRequestException.class, () -> service.createContainer("X", " ", "48009", 1000));
    }

    @Test
    void sensorValidaExistenciaYCapacidadAntesDeComprobarDuplicados() {
        assertThrows(NotFoundException.class,
                () -> service.registerReading("CONT-999", -1, FillLevel.GREEN));
        assertThrows(BadRequestException.class,
                () -> service.registerReading("CONT-001", 2001, FillLevel.GREEN));
        assertThrows(BadRequestException.class,
                () -> service.registerReading("CONT-001", 500, null));
        Container container = service.createContainer("CONT-011", "Calle 1", "48009", 1000);
        assertEquals(0, service.registerReading("CONT-011", 0, FillLevel.GREEN).getPackageCount());
        assertEquals(0, container.getLatestReading().orElseThrow().getPackageCount());
    }

    @Test
    void sensorDuplicadoHoyDaConflictoYSinSobrescritura() {
        Container container = service.getContainer("CONT-001");
        assertThrows(ConflictException.class,
                () -> service.registerReading("CONT-001", 500, FillLevel.ORANGE));
        assertEquals(5, container.getReadings().size());
        assertEquals(400, container.getLatestReading().orElseThrow().getPackageCount());
        assertEquals(FillLevel.GREEN, container.getLatestReading().orElseThrow().getFillLevel());
        assertEquals(0.012, container.getLatestReading().orElseThrow().getTons(), 1e-12);

        Container nuevo = service.createContainer("CONT-011", "Calle 1", "48009", 1000);
        service.registerReading("CONT-011", 125, FillLevel.GREEN);
        assertThrows(ConflictException.class,
                () -> service.registerReading("CONT-011", 140, FillLevel.GREEN));
        assertEquals(1, nuevo.getReadings().size());
        assertEquals(125, nuevo.getLatestReading().orElseThrow().getPackageCount());
    }

    @Test
    void zonaSeFiltraPorCodigoPostalYFecha() {
        LocalDate today = LocalDate.now();
        assertEquals(List.of("CONT-001", "CONT-002"),
                service.getContainersByZone("48009", today).stream().map(Container::getId).toList());
        assertTrue(service.getContainersByZone("99999", today).isEmpty());
        assertTrue(service.getContainersByZone("48009", today.minusDays(10)).isEmpty());
        assertThrows(BadRequestException.class,
                () -> service.getContainersByZone("4800", today));
    }

    @Test
    void historialRechazaFechasIncoherentesYContenedorDesconocido() {
        LocalDate today = LocalDate.now();
        assertThrows(NotFoundException.class,
                () -> service.getReadings("CONT-999", today, today.minusDays(1)));
        assertThrows(BadRequestException.class,
                () -> service.getReadings("CONT-001", today, today.minusDays(1)));
        assertThrows(BadRequestException.class,
                () -> service.getReadings("CONT-001", null, today));
    }
}
