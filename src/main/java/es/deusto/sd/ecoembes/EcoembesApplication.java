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
        description = "API REST para gestionar contenedores, lecturas y plantas de reciclaje"))
public class EcoembesApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcoembesApplication.class, args);
    }
}
