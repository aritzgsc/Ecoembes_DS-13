package es.deusto.sd.ecoembes.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import es.deusto.sd.ecoembes.exception.ConflictException;

/** Verifica la unicidad diaria también con solicitudes simultáneas. */
class ContainerConcurrencyTest {
    @Test
    void lecturaDiariaEsAtomicaEntreSensoresConcurrentes() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(16);
        try {
            for (int round = 0; round < 100; round++) {
                Container container = new Container("RACE", "Calle 1", "48009", 1000);
                LocalDate today = LocalDate.now();
                for (int day = 50; day > 0; day--) {
                    container.addReading(new ContainerReading(today.minusDays(day), 0, FillLevel.GREEN));
                }
                CyclicBarrier start = new CyclicBarrier(16);
                AtomicInteger accepted = new AtomicInteger();
                List<Future<?>> futures = new ArrayList<>();
                for (int thread = 0; thread < 16; thread++) {
                    final int count = thread;
                    futures.add(pool.submit(() -> {
                        start.await(10, TimeUnit.SECONDS);
                        try {
                            container.addReading(new ContainerReading(today, count, FillLevel.GREEN));
                            accepted.incrementAndGet();
                        } catch (ConflictException expected) {
                            // Todas las peticiones posteriores deben rechazarse como duplicadas.
                        }
                        return null;
                    }));
                }
                for (Future<?> future : futures) {
                    future.get(10, TimeUnit.SECONDS);
                }
                assertEquals(1, accepted.get(), "Solo una lectura diaria debe aceptarse");
                assertEquals(51, container.getReadings().size());
                assertEquals(1, container.getReadingsBetween(today, today).size());
                assertTrue(container.getLatestReading().isPresent());
            }
        } finally {
            pool.shutdownNow();
        }
    }
}
