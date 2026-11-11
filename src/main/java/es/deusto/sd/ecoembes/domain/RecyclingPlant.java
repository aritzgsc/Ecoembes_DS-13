package es.deusto.sd.ecoembes.domain;

/** Representa una planta de reciclaje disponible para recibir contenedores. */
public class RecyclingPlant {

    private final String id;
    private final String name;

    public RecyclingPlant(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
