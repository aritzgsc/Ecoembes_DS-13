package es.deusto.sd.ecoembes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import es.deusto.sd.ecoembes.exception.BadRequestException;
import es.deusto.sd.ecoembes.exception.NotFoundException;
import es.deusto.sd.ecoembes.external.ContSocketGateway;
import es.deusto.sd.ecoembes.external.PlasSBGateway;
import es.deusto.sd.ecoembes.external.RecyclingPlantGateway;

/** Pruebas unitarias del servicio de plantas, sin contexto Spring. */
class PlantAppServiceTest {

    private PlantAppService newService() {
        return new PlantAppService(List.of(new PlasSBGateway(), new ContSocketGateway()));
    }

    @Test
    void cargaLasDosPlantasConSusIdentificadores() {
        PlantAppService service = newService();

        assertEquals(2, service.getPlants().size());
        Set<String> ids = service.getPlants().stream()
                .map(plant -> plant.getId())
                .collect(Collectors.toSet());
        assertEquals(Set.of("PLASSB", "CONTSOCKET"), ids);
        assertEquals("PlasSB Ltd.", service.getPlant("PLASSB").getName());
        assertEquals("ContSocket Ltd.", service.getPlant("CONTSOCKET").getName());
    }

    @Test
    void consultaCapacidadesSemillaYUltimoDiaPermitido() {
        PlantAppService service = newService();
        LocalDate today = LocalDate.now();

        assertEquals(0.40, service.getTotalCapacity("PLASSB", today), 1e-9);
        assertEquals(0.35, service.getTotalCapacity("PLASSB", today.plusDays(1)), 1e-9);
        assertEquals(0.30, service.getTotalCapacity("CONTSOCKET", today.plusDays(1)), 1e-9);
        assertEquals(0.50, service.getTotalCapacity("PLASSB", today.plusDays(9)), 1e-9);
        assertEquals(0.30, service.getTotalCapacity("CONTSOCKET", today.plusDays(9)), 1e-9);
    }

    @Test
    void rechazaFechasInvalidasAntesDeConsultarElGateway() {
        FakeGateway fake = new FakeGateway();
        PlantAppService service = new PlantAppService(List.of(fake));
        LocalDate today = LocalDate.now();

        assertThrows(BadRequestException.class, () -> service.getTotalCapacity("FAKE", null));
        assertThrows(BadRequestException.class, () -> service.getTotalCapacity("FAKE", today.minusDays(1)));
        assertThrows(BadRequestException.class, () -> service.getTotalCapacity("FAKE", today.plusDays(10)));
        assertEquals(0, fake.capacityCalls);
    }

    @Test
    void plantaDesconocidaProduceNotFoundAntesDeValidarFecha() {
        PlantAppService service = newService();

        assertThrows(NotFoundException.class, () -> service.getPlant("NO-EXISTE"));
        assertThrows(NotFoundException.class, () -> service.getTotalCapacity("NO-EXISTE", null));
        assertThrows(NotFoundException.class,
                () -> service.notifyAssignment("NO-EXISTE", LocalDate.now(), 1, 100));
    }

    @Test
    void ventanaMovilConservaFechasExistentesYAnadeNuevas() throws Exception {
        LocalDate today = LocalDate.now();
        comprobarVentanaMovil(new PlasSBGateway(), today, 0.35, 0.40, 0.35);
        comprobarVentanaMovil(new ContSocketGateway(), today, 0.30, 0.30, 0.30);
    }

    @Test
    void admiteUnaTerceraPlantaSinCambiarElServicio() {
        FakeGateway extra = new FakeGateway();
        PlantAppService service = new PlantAppService(
                List.of(new PlasSBGateway(), new ContSocketGateway(), extra));

        assertEquals(3, service.getPlants().size());
        assertEquals("Planta de prueba", service.getPlant("FAKE").getName());
        assertEquals(0.99, service.getTotalCapacity("FAKE", LocalDate.now()), 1e-9);
        assertEquals(1, extra.capacityCalls);
    }

    /** Simula un cambio de fecha llamando solo desde el test al método privado. */
    @SuppressWarnings("unchecked")
    private void comprobarVentanaMovil(RecyclingPlantGateway gateway, LocalDate today,
            double expectedTomorrow, double expectedDay10, double expectedDay11) throws Exception {
        Class<?> type = gateway.getClass();
        Field field = type.getDeclaredField("capacityByDate");
        field.setAccessible(true);
        Map<LocalDate, Double> capacities = (Map<LocalDate, Double>) field.get(gateway);

        assertEquals(10, capacities.size());
        assertEquals(expectedTomorrow, capacities.get(today.plusDays(1)), 1e-9);
        double tomorrowCapacity = capacities.get(today.plusDays(1));
        double day9Capacity = capacities.get(today.plusDays(9));

        Method sync = type.getDeclaredMethod("synchronizeWindow", LocalDate.class);
        sync.setAccessible(true);
        sync.invoke(gateway, today.plusDays(1));

        assertEquals(10, capacities.size());
        assertFalse(capacities.containsKey(today));
        assertEquals(tomorrowCapacity, capacities.get(today.plusDays(1)), 1e-9);
        assertEquals(day9Capacity, capacities.get(today.plusDays(9)), 1e-9);
        assertEquals(expectedDay10, capacities.get(today.plusDays(10)), 1e-9);

        sync.invoke(gateway, today.plusDays(2));
        assertEquals(10, capacities.size());
        assertFalse(capacities.containsKey(today.plusDays(1)));
        assertTrue(capacities.containsKey(today.plusDays(11)));
        assertEquals(day9Capacity, capacities.get(today.plusDays(9)), 1e-9);
        assertEquals(expectedDay11, capacities.get(today.plusDays(11)), 1e-9);
    }

    /** Gateway simulado solo para probar la ampliación y el orden de validaciones. */
    private static class FakeGateway implements RecyclingPlantGateway {
        private int capacityCalls;

        @Override
        public String getPlantId() {
            return "FAKE";
        }

        @Override
        public String getPlantName() {
            return "Planta de prueba";
        }

        @Override
        public double getCapacity(LocalDate date) {
            capacityCalls++;
            return 0.99;
        }

        @Override
        public void notifyAssignment(LocalDate date, int containerCount, int packageCount) {
            // Sin efectos externos en la prueba.
        }
    }
}
