package es.deusto.sd.ecoembes.state;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import es.deusto.sd.ecoembes.domain.Employee;

/** Mantiene en memoria las sesiones del personal que ha iniciado sesión. */
@Component
public class ServerState {

    private final Map<Long, Employee> activeSessions = new ConcurrentHashMap<>();

    /** Crea una sesión con un token basado en el instante actual. */
    public long addSession(Employee employee) {
        long token = System.currentTimeMillis();

        // Dos inicios de sesión en el mismo milisegundo no deben compartir token.
        while (activeSessions.putIfAbsent(token, employee) != null) {
            token++;
        }

        return token;
    }

    /** Busca la persona asociada a un token activo. */
    public Optional<Employee> getEmployee(long token) {
        return Optional.ofNullable(activeSessions.get(token));
    }

    /** Elimina una sesión y devuelve si existía. */
    public boolean removeSession(long token) {
        return activeSessions.remove(token) != null;
    }
}
