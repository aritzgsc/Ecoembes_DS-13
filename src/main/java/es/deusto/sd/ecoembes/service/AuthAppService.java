package es.deusto.sd.ecoembes.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import es.deusto.sd.ecoembes.domain.Employee;
import es.deusto.sd.ecoembes.exception.BadRequestException;
import es.deusto.sd.ecoembes.exception.UnauthorizedException;
import es.deusto.sd.ecoembes.state.ServerState;

/** Gestiona la autenticación del personal y el ciclo de vida de sus sesiones. */
@Service
public class AuthAppService {

    private final ServerState serverState;
    private final List<Employee> employees;

    public AuthAppService(ServerState serverState) {
        this.serverState = serverState;

        // Datos de prueba del Prototipo 1. Solo guardamos hashes en memoria.
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.employees = List.of(
                new Employee(1L, "Ana García", "ana.garcia@ecoembes.es", encoder.encode("ana123")),
                new Employee(2L, "Luis Pérez", "luis.perez@ecoembes.es", encoder.encode("luis123")),
                new Employee(3L, "Marta Ruiz", "marta.ruiz@ecoembes.es", encoder.encode("marta123"))
        );
    }

    /** Comprueba las credenciales y devuelve el token de una nueva sesión. */
    public long login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new BadRequestException("El correo y la contraseña son obligatorios.");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        for (Employee employee : employees) {
            if (employee.getEmail().equalsIgnoreCase(email)
                    && encoder.matches(password, employee.getPasswordHash())) {
                return serverState.addSession(employee);
            }
        }

        throw new UnauthorizedException("Correo o contraseña incorrectos.");
    }

    /** Cierra una sesión; si el token no existe, rechaza la operación. */
    public void logout(long token) {
        if (!serverState.removeSession(token)) {
            throw new UnauthorizedException("El token no está activo.");
        }
    }

    /** Devuelve el empleado autenticado o rechaza el token recibido. */
    public Employee validateToken(long token) {
        return serverState.getEmployee(token)
                .orElseThrow(() -> new UnauthorizedException("El token no está activo."));
    }
}
