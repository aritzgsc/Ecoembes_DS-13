package es.deusto.sd.ecoembes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

/** Las altas concurrentes mantienen la unicidad del identificador. */
class ContainerCreationConcurrencyTest {
    @Test
    void soloUnAltaConcurrentePuedeAceptarElMismoIdentificador() throws Exception {
        ContainerAppService service = new ContainerAppService();
        ExecutorService pool = Executors.newFixedThreadPool(16);
        try {
            for (int round = 0; round < 100; round++) {
                String id = "CONCURRENT-" + round;
                CyclicBarrier start = new CyclicBarrier(16);
                AtomicInteger accepted = new AtomicInteger();
                List<Future<?>> futures = new ArrayList<>();
                for (int thread = 0; thread < 16; thread++) {
                    futures.add(pool.submit(() -> {
                        start.await(10, TimeUnit.SECONDS);
                        try {
                            service.createContainer(id, "Calle 1", "48009", 1000);
                            accepted.incrementAndGet();
                        } catch (ConflictException expected) {
                            // El identificador ya fue registrado por otra solicitud.
                        }
                        return null;
                    }));
                }
                for (Future<?> future : futures) {
                    future.get(10, TimeUnit.SECONDS);
                }
                assertEquals(1, accepted.get(), "Solo una solicitud debe crear el identificador");
                assertEquals(round + 11, service.getAllContainers().size());
            }
        } finally {
            pool.shutdownNow();
        }
    }
}
