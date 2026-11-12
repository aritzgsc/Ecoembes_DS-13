package es.deusto.sd.ecoembes.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import es.deusto.sd.ecoembes.domain.Container;
import es.deusto.sd.ecoembes.domain.ContainerReading;
import es.deusto.sd.ecoembes.domain.FillLevel;
import es.deusto.sd.ecoembes.exception.BadRequestException;
import es.deusto.sd.ecoembes.exception.ConflictException;
import es.deusto.sd.ecoembes.exception.NotFoundException;

/** Gestiona los contenedores y sus lecturas simuladas en memoria. */
@Service
public class ContainerAppService {

    // El monitor del servicio protege el mapa y la unicidad de las altas.
    private final Map<String, Container> containers = new LinkedHashMap<>();

    public ContainerAppService() {
        // Datos ficticios de demostración (id, dirección, CP, capacidad, envases hoy).
        addSeed("CONT-001", "Calle Ercilla 10, Bilbao", "48009", 2000, 400);
        addSeed("CONT-002", "Gran Vía 25, Bilbao", "48009", 2000, 1200);
        addSeed("CONT-003", "Calle Licenciado Poza 30, Bilbao", "48011", 1500, 1450);
        addSeed("CONT-004", "Avenida de las Universidades 24, Bilbao", "48007", 2500, 900);
        addSeed("CONT-005", "Calle Rodríguez Arias 8, Bilbao", "48007", 1800, 1500);
        addSeed("CONT-006", "Calle Dato 10, Vitoria-Gasteiz", "01001", 2000, 1900);
        addSeed("CONT-007", "Calle Postas 5, Vitoria-Gasteiz", "01001", 1500, 700);
        addSeed("CONT-008", "Paseo de Salamanca 3, Donostia", "20003", 2200, 1300);
        addSeed("CONT-009", "Calle Loyola 7, Donostia", "20003", 1600, 1600);
        addSeed("CONT-010", "Avenida de Navarra 12, Donostia", "20013", 2000, 1000);
    }

    /** Devuelve los contenedores en el orden en el que se registraron. */
    public synchronized List<Container> getAllContainers() {
        return new ArrayList<>(containers.values());
    }

    /** Da de alta un contenedor vacío, sin inventar una primera lectura. */
    public synchronized Container createContainer(String id, String address, String postalCode, int capacity) {
        if (id == null || id.isBlank() || address == null || address.isBlank()
                || !validPostalCode(postalCode) || capacity <= 0) {
            throw new BadRequestException("Identificador, dirección, código postal o capacidad incorrectos.");
        }
        if (containers.containsKey(id)) {
            throw new ConflictException("Ya existe el contenedor " + id + ".");
        }
        Container container = new Container(id, address, postalCode, capacity);
        containers.put(id, container);
        return container;
    }

    /** Busca un contenedor por id o lanza 404 si no existe. */
    public synchronized Container getContainer(String id) {
        Container container = containers.get(id);
        if (container == null) {
            throw new NotFoundException("No existe el contenedor " + id + ".");
        }
        return container;
    }

    /** Registra la primera lectura de hoy; jamás sobrescribe una existente. */
    public synchronized ContainerReading registerReading(
            String containerId, Integer packageCount, FillLevel level) {

        Container container = getContainer(containerId); // 404 antes de validar contenido.

        if (packageCount == null || packageCount < 0
                || packageCount > container.getCapacity() || level == null) {
            throw new BadRequestException(
                    "Cantidad de envases o nivel de llenado incorrecto.");
        }

        ContainerReading reading = new ContainerReading(
                LocalDate.now(), packageCount, level);

        container.addReading(reading); // 409 si ya hay una medición para hoy.
        return reading;
    }

    /** Consulta el historial por intervalo inclusivo y ordenado por fecha. */
    public synchronized List<ContainerReading> getReadings(String containerId, LocalDate from, LocalDate to) {
        Container container = getContainer(containerId); // 404 tiene prioridad.
        if (from == null || to == null || from.isAfter(to)) {
            throw new BadRequestException("El intervalo de fechas no es válido.");
        }
        return container.getReadingsBetween(from, to);
    }

    /** Devuelve los contenedores de la zona con alguna medición hasta date. */
    public synchronized List<Container> getContainersByZone(String postalCode, LocalDate date) {
        if (!validPostalCode(postalCode) || date == null) {
            throw new BadRequestException("Código postal o fecha incorrectos.");
        }
        return containers.values().stream()
                .filter(c -> postalCode.equals(c.getPostalCode()))
                .filter(c -> c.getReadingAt(date).isPresent())
                .toList();
    }

    private boolean validPostalCode(String postalCode) {
        return postalCode != null && postalCode.matches("[0-9]{5}");
    }

    private void addSeed(String id, String address, String postalCode, int capacity, int countToday) {
        Container container = new Container(id, address, postalCode, capacity);
        LocalDate today = LocalDate.now();
        // Cinco lecturas de hoy-4 hasta hoy, con nivel derivado solo para la semilla.
        for (int day = 4; day >= 0; day--) {
            int count = countToday * (5 - day) / 5;
            double fraction = (double) count / capacity;
            FillLevel level = fraction < 0.50 ? FillLevel.GREEN
                    : fraction < 0.85 ? FillLevel.ORANGE : FillLevel.RED;
            container.addReading(new ContainerReading(today.minusDays(day), count, level));
        }
        containers.put(id, container);
    }
}
