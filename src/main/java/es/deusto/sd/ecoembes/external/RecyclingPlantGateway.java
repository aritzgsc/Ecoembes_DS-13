package es.deusto.sd.ecoembes.external;

import java.time.LocalDate;

/** Contrato común para consultar y notificar a una planta externa. */
public interface RecyclingPlantGateway {

    String getPlantId();

    String getPlantName();

    double getCapacity(LocalDate date);

    void notifyAssignment(LocalDate date, int containerCount, int packageCount);
}
