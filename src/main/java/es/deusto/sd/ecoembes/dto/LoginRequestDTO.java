package es.deusto.sd.ecoembes.dto;

/** Credenciales que recibe la API al iniciar sesión. */
public record LoginRequestDTO(String email, String password) {
}
