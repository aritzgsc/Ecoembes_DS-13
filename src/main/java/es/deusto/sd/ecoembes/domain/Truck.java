package es.deusto.sd.ecoembes.domain;

import java.util.ArrayList;
import java.util.List;

/** Camión conceptual; las rutas no se planifican en el Prototipo 1. */
public class Truck {

    private final String id;
    private final List<Route> routes;

    public Truck(String id) {
        this.id = id;
        this.routes = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    /** Asociación navegable Truck → Route (una ruta puede estar en varios camiones). */
    public List<Route> getRoutes() {
        return routes;
    }
}
