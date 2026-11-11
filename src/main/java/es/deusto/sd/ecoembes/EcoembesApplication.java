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
        description = "Prototipo 1: datos semilla y sesiones en memoria, restaurados al reiniciar; plantas simuladas sin HTTP/socket real. Personal: token numérico activo en query, excepto login. Sensor: POST de lecturas sin token. Fechas yyyy-MM-dd según LocalDate.now() del servidor. Errores JSON con campo message. Peso por envase: 0,03 kg."))
public class EcoembesApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcoembesApplication.class, args);
    }
}
