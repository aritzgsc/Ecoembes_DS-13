package es.deusto.sd.ecoembes.domain;

/** Representa a una persona del equipo de Ecoembes autorizada para usar el sistema. */
public class Employee {

    private final long id;
    private final String name;
    private final String email;
    private final String passwordHash;

    public Employee(long id, String name, String email, String passwordHash) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    /** Devuelve el hash BCrypt, nunca la contraseña original. */
    public String getPasswordHash() {
        return passwordHash;
    }
}
