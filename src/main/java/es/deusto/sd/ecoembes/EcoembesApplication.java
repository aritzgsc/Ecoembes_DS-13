package es.deusto.sd.ecoembes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/** Punto de entrada del servidor Ecoembes. */
@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "API Servidor Ecoembes",
        version = "1.0",
        description = "Servidor Ecoembes · Prototipo 1 · Diseño de Software · Grupo 13. "
                + "Once operaciones REST con estado y sesiones exclusivamente en memoria: al reiniciar se restauran los datos semilla. "
                + "El personal obtiene un token numérico mediante login y lo envía como parámetro query token. "
                + "El sensor registra una lectura diaria sin token del personal; los duplicados se rechazan sin sobrescribir. "
                + "Las capacidades y notificaciones de PlasSB y ContSocket se simulan; no hay comunicaciones externas reales. "
                + "Fechas ISO yyyy-MM-dd; capacidades en toneladas (0,03 kg por envase). "
                + "Los errores previstos se devuelven como ErrorDTO, con el campo message."))
public class EcoembesApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcoembesApplication.class, args);
    }
}
