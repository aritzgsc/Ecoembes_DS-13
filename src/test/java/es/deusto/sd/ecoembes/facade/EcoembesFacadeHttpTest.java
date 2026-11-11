package es.deusto.sd.ecoembes.facade;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import es.deusto.sd.ecoembes.exception.GlobalExceptionHandler;
import es.deusto.sd.ecoembes.external.ContSocketGateway;
import es.deusto.sd.ecoembes.external.PlasSBGateway;
import es.deusto.sd.ecoembes.service.AssignmentAppService;
import es.deusto.sd.ecoembes.service.AuthAppService;
import es.deusto.sd.ecoembes.service.ContainerAppService;
import es.deusto.sd.ecoembes.service.PlantAppService;
import es.deusto.sd.ecoembes.state.ServerState;

/** Comprueba rutas HTTP, conversión JSON, parámetros y respuestas del controlador. */
class EcoembesFacadeHttpTest {

    private MockMvc mvc;
    private String token;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        AuthAppService auth = new AuthAppService(new ServerState());
        ContainerAppService containers = new ContainerAppService();
        PlantAppService plants = new PlantAppService(List.of(new PlasSBGateway(), new ContSocketGateway()));
        AssignmentAppService assignments = new AssignmentAppService(containers, plants);
        mvc = MockMvcBuilders.standaloneSetup(new EcoembesFacade(auth, containers, plants, assignments))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        token = String.valueOf(auth.login("ana.garcia@ecoembes.es", "ana123"));
    }

    @Test
    void onceEndpointsRespondenConSusMetodosYCodigosHttp() throws Exception {
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"ana.garcia@ecoembes.es\",\"password\":\"ana123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        mvc.perform(post("/api/containers")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"CONT-011\",\"address\":\"Calle Nueva 1\",\"postalCode\":\"48009\",\"capacity\":1000}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("CONT-011"));

        mvc.perform(get("/api/containers").param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[10].id").value("CONT-011"));

        mvc.perform(get("/api/containers/CONT-001/readings")
                .param("from", today.minusDays(4).toString())
                .param("to", today.toString())
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[4].packageCount").value(400));

        mvc.perform(get("/api/zones/48009/containers")
                .param("date", today.toString())
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("CONT-001"));

        // El sensor NO necesita token y acepta cero envases.
        mvc.perform(post("/api/containers/CONT-011/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"packageCount\":0,\"fillLevel\":\"GREEN\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.packageCount").value(0));

        mvc.perform(get("/api/plants").param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("PLASSB"));

        mvc.perform(get("/api/plants/PLASSB/capacity")
                .param("date", today.toString()).param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTons").value(0.4));

        mvc.perform(post("/api/plants/PLASSB/assignments")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"" + today + "\",\"containerIds\":[\"CONT-001\"]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPackages").value(400))
                .andExpect(jsonPath("$.assignedBy").value("ana.garcia@ecoembes.es"));

        mvc.perform(get("/api/plants/PLASSB/assignments").param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].plantId").value("PLASSB"));

        mvc.perform(post("/api/auth/logout").param("token", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void sensorRepetido409YNoSeSobrescribe() throws Exception {
        mvc.perform(post("/api/containers")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"CONT-011\",\"address\":\"Nueva\",\"postalCode\":\"48009\",\"capacity\":1000}"))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/containers/CONT-011/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"packageCount\":100,\"fillLevel\":\"GREEN\"}"))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/containers/CONT-011/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"packageCount\":999,\"fillLevel\":\"RED\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());

        mvc.perform(get("/api/containers/CONT-011/readings")
                .param("from", today.toString()).param("to", today.toString())
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].packageCount").value(100))
                .andExpect(jsonPath("$[0].fillLevel").value("GREEN"))
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    void tokenMalFormadoNuloEInvalidoDevuelven400O401() throws Exception {
        mvc.perform(get("/api/containers"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
        mvc.perform(get("/api/containers").param("token", "abc"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/containers").param("token", "-1"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/containers/CONT-001/readings")
                .param("from", "fecha-mala").param("to", today.toString()).param("token", token))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ json-invalido"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void lecturasNulasYAsignacionConFechaNulaSon400() throws Exception {
        // El campo packageCount ausente o null prevalece sobre un contenedor desconocido.
        mvc.perform(post("/api/containers/NO-EXISTE/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fillLevel\":\"GREEN\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/containers/NO-EXISTE/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"packageCount\":null,\"fillLevel\":\"GREEN\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/containers/CONT-001/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"packageCount\":0,\"fillLevel\":null}"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/plants/PLASSB/assignments")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"containerIds\":[\"CONT-001\"]}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/plants/PLASSB/assignments")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":null,\"containerIds\":[\"CONT-001\"]}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/plants/PLASSB/assignments")
                .param("token", "-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":null,\"containerIds\":[\"CONT-001\"]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void asignacionDuplicada409YFiltroDeFechaOpcional() throws Exception {
        String body = "{\"date\":\"" + today + "\",\"containerIds\":[\"CONT-001\"]}";
        mvc.perform(post("/api/plants/PLASSB/assignments")
                .param("token", token).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/plants/CONTSOCKET/assignments")
                .param("token", token).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());

        mvc.perform(get("/api/plants/PLASSB/assignments")
                .param("date", today.toString()).param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].containerIds[0]").value("CONT-001"));

        mvc.perform(get("/api/plants/PLASSB/capacity")
                .param("date", today.toString()).param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedTons").value(0.012));
    }
}
