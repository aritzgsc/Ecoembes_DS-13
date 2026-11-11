package es.deusto.sd.ecoembes.dto;

/** Datos recibidos para dar de alta un contenedor. */
public record ContainerCreateDTO(String id, String address, String postalCode, int capacity) {
}
