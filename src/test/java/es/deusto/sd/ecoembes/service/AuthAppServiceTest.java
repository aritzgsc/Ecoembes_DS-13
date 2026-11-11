package es.deusto.sd.ecoembes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import es.deusto.sd.ecoembes.domain.Employee;
import es.deusto.sd.ecoembes.exception.BadRequestException;
import es.deusto.sd.ecoembes.exception.UnauthorizedException;
import es.deusto.sd.ecoembes.state.ServerState;

/** Pruebas unitarias de autenticación sin levantar el contexto de Spring. */
class AuthAppServiceTest {

    private ServerState serverState;
    private AuthAppService authAppService;

    @BeforeEach
    void setUp() {
        serverState = new ServerState();
        authAppService = new AuthAppService(serverState);
    }

    @Test
    void loginValidoCreaSesionYUsaHashesBcrypt() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String[][] credentials = {
                { "ana.garcia@ecoembes.es", "ana123", "Ana García" },
                { "luis.perez@ecoembes.es", "luis123", "Luis Pérez" },
                { "marta.ruiz@ecoembes.es", "marta123", "Marta Ruiz" }
        };

        for (String[] credential : credentials) {
            long token = authAppService.login(credential[0], credential[1]);
            Employee employee = authAppService.validateToken(token);

            assertTrue(serverState.getEmployee(token).isPresent());
            assertEquals(credential[0], employee.getEmail());
            assertEquals(credential[2], employee.getName());
            assertNotEquals(credential[1], employee.getPasswordHash());
            assertTrue(encoder.matches(credential[1], employee.getPasswordHash()));
        }
    }

    @Test
    void contrasenaOEmailIncorrectosRechazanLogin() {
        assertThrows(UnauthorizedException.class,
                () -> authAppService.login("ana.garcia@ecoembes.es", "incorrecta"));
        assertThrows(UnauthorizedException.class,
                () -> authAppService.login("desconocido@ecoembes.es", "ana123"));
    }

    @Test
    void credencialesAusentesOEnBlancoDanBadRequest() {
        assertThrows(BadRequestException.class, () -> authAppService.login(null, "ana123"));
        assertThrows(BadRequestException.class, () -> authAppService.login("  ", "ana123"));
        assertThrows(BadRequestException.class,
                () -> authAppService.login("ana.garcia@ecoembes.es", null));
        assertThrows(BadRequestException.class,
                () -> authAppService.login("ana.garcia@ecoembes.es", ""));
    }

    @Test
    void validarTokenDesconocidoDaUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> authAppService.validateToken(-1L));
    }

    @Test
    void logoutInvalidaSesionYNoPermiteRepetirlo() {
        long token = authAppService.login("ana.garcia@ecoembes.es", "ana123");

        authAppService.logout(token);

        assertFalse(serverState.getEmployee(token).isPresent());
        assertThrows(UnauthorizedException.class, () -> authAppService.validateToken(token));
        assertThrows(UnauthorizedException.class, () -> authAppService.logout(token));
    }

    @Test
    void loginsConsecutivosRecibenTokensDiferentes() {
        Set<Long> tokens = new HashSet<>();
        for (int i = 0; i < 40; i++) {
            long token = authAppService.login("ana.garcia@ecoembes.es", "ana123");
            assertTrue(tokens.add(token), "No debe repetirse un token activo.");
        }

        assertEquals(40, tokens.size());
    }
}
