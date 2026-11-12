# Arquitectura de Ecoembes

[← Volver al README](../README.md)

Este documento presenta la arquitectura implementada para el **Prototipo 1** del servidor Ecoembes, su organización interna y los modelos UML utilizados como referencia de implementación.

> **Estado del Prototipo 1:** once operaciones REST implementadas. Las ejecuciones registradas el 11/11/2026 incluyen 63 pruebas JUnit, 72 escenarios HTTP y 38 ejecuciones desde Swagger UI. El estado funcional y las sesiones se mantienen en memoria.

## Visión general

El sistema conecta tres tipos de participantes:

1. **Personal de Ecoembes**, que inicia sesión, consulta información y asigna contenedores a plantas de reciclaje.
2. **Sensores de contenedores**, que transmiten una lectura diaria con el número estimado de envases y su nivel de llenado.
3. **Plantas de reciclaje**, que informan de su capacidad por fecha y reciben las notificaciones de las asignaciones.

La aplicación expone una **API REST** y separa la lógica de negocio de los mecanismos HTTP y de las integraciones externas. En este primer prototipo, las plantas se simulan mediante adaptadores internos; no se realizan conexiones reales a sus sistemas.

## Organización del código

El código Java se distribuye en los siguientes paquetes, bajo `es.deusto.sd.ecoembes`:

| Paquete | Responsabilidad | Componentes del diseño |
| --- | --- | --- |
| `facade` | Recibir peticiones HTTP, validar su entrada y construir respuestas. | `EcoembesFacade` |
| `service` | Orquestar casos de uso y aplicar reglas de negocio. | `AuthAppService`, `ContainerAppService`, `PlantAppService`, `AssignmentAppService` |
| `dto` | Separar los datos de entrada y salida de la API del modelo interno. | DTOs de autenticación, contenedores, lecturas, plantas y asignaciones; `DtoAssembler` |
| `domain` | Representar entidades y relaciones del negocio. | `Employee`, `Container`, `ContainerReading`, `FillLevel`, `RecyclingPlant`, `Assignment`, `Truck`, `Route` |
| `state` | Mantener las sesiones activas y el estado compartido necesario. | `ServerState` |
| `external` | Encapsular las particularidades de cada planta. | `RecyclingPlantGateway`, `PlasSBGateway`, `ContSocketGateway` |
| `exception` | Traducir errores de aplicación en respuestas HTTP coherentes. | Excepciones específicas y `GlobalExceptionHandler` |

### Responsabilidades y dependencias

- **Façade:** implementa los endpoints y transforma datos entre HTTP y los servicios. La lógica de negocio permanece fuera del controlador.
- **Servicios de aplicación:** coordinan operaciones, consultan el estado y utilizan los adaptadores externos cuando es necesario.
- **Dominio:** contiene los conceptos de negocio y sus relaciones sin depender del transporte REST.
- **DTOs:** permiten evolucionar la representación pública de la API sin exponer directamente las entidades del dominio.
- **Estado:** conserva en memoria las sesiones activas. Los datos funcionales del prototipo se mantienen también en colecciones en memoria gestionadas por los servicios.
- **Integraciones:** una interfaz común oculta los detalles específicos de cada planta y facilita incorporar otras sin cambiar las operaciones públicas.

Se emplea **inyección de dependencias por constructor** para comunicar los componentes de Spring.

## Diagramas UML

Las imágenes SVG se muestran **directamente en esta página**, sin necesidad de descargar archivos. También están disponibles los PNG y los modelos editables de Visual Paradigm (`.vpp`).

### Diagrama de clases

El diagrama de clases representa la Façade, los servicios de aplicación, los DTO, el estado compartido, las entidades del dominio y las interfaces de integración con las plantas.

![Diagrama de clases de Ecoembes](diagramas/svg/Diagrama%20de%20Clases.svg)

[Ver SVG](diagramas/svg/Diagrama%20de%20Clases.svg) · [Ver PNG](diagramas/png/Diagrama%20de%20Clases.png) · [Modelo editable de Visual Paradigm](visual-paradigm/Diagrama%20de%20Clases.vpp)

### Diagramas de secuencia

Los cuatro flujos pueden desplegarse individualmente para mantener la documentación legible.

<details>
<summary><strong>SD-01 · Sesión, alta y consulta de contenedores</strong> — Autenticación, alta y consultas del estado de los contenedores.</summary>

![SD-01 · Sesión, alta y consulta de contenedores](diagramas/svg/Diagrama%20de%20Secuencia%2001%20-%20Sesi%C3%B3n%2C%20alta%20y%20consulta%20de%20contenedores.svg)

[Ver SVG](diagramas/svg/Diagrama%20de%20Secuencia%2001%20-%20Sesi%C3%B3n%2C%20alta%20y%20consulta%20de%20contenedores.svg) · [Ver PNG](diagramas/png/Diagrama%20de%20Secuencia%2001%20-%20Sesi%C3%B3n%2C%20alta%20y%20consulta%20de%20contenedores.png) · [Modelo editable de Visual Paradigm](visual-paradigm/Diagrama%20de%20Secuencia%2001%20-%20Sesi%C3%B3n%2C%20alta%20y%20consulta%20de%20contenedores.vpp)

</details>

<details>
<summary><strong>SD-02 · Capacidad, asignación y auditoría</strong> — Consulta de capacidad, asignación de contenedores y trazabilidad.</summary>

![SD-02 · Capacidad, asignación y auditoría](diagramas/svg/Diagrama%20de%20Secuencia%2002%20-%20Capacidad%2C%20asignaci%C3%B3n%20y%20auditor%C3%ADa.svg)

[Ver SVG](diagramas/svg/Diagrama%20de%20Secuencia%2002%20-%20Capacidad%2C%20asignaci%C3%B3n%20y%20auditor%C3%ADa.svg) · [Ver PNG](diagramas/png/Diagrama%20de%20Secuencia%2002%20-%20Capacidad%2C%20asignaci%C3%B3n%20y%20auditor%C3%ADa.png) · [Modelo editable de Visual Paradigm](visual-paradigm/Diagrama%20de%20Secuencia%2002%20-%20Capacidad%2C%20asignaci%C3%B3n%20y%20auditor%C3%ADa.vpp)

</details>

<details>
<summary><strong>SD-03 · Logout e invalidación del token</strong> — Cierre de sesión e invalidación del token.</summary>

![SD-03 · Logout e invalidación del token](diagramas/svg/Diagrama%20de%20Secuencia%2003%20-%20Logout%20e%20invalidaci%C3%B3n%20del%20token.svg)

[Ver SVG](diagramas/svg/Diagrama%20de%20Secuencia%2003%20-%20Logout%20e%20invalidaci%C3%B3n%20del%20token.svg) · [Ver PNG](diagramas/png/Diagrama%20de%20Secuencia%2003%20-%20Logout%20e%20invalidaci%C3%B3n%20del%20token.png) · [Modelo editable de Visual Paradigm](visual-paradigm/Diagrama%20de%20Secuencia%2003%20-%20Logout%20e%20invalidaci%C3%B3n%20del%20token.vpp)

</details>

<details>
<summary><strong>SD-04 · Registro de lectura del sensor</strong> — Recepción y validación de la lectura diaria.</summary>

![SD-04 · Registro de lectura del sensor](diagramas/svg/Diagrama%20de%20Secuencia%2004%20-%20Registro%20de%20lectura%20del%20sensor.svg)

[Ver SVG](diagramas/svg/Diagrama%20de%20Secuencia%2004%20-%20Registro%20de%20lectura%20del%20sensor.svg) · [Ver PNG](diagramas/png/Diagrama%20de%20Secuencia%2004%20-%20Registro%20de%20lectura%20del%20sensor.png) · [Modelo editable de Visual Paradigm](visual-paradigm/Diagrama%20de%20Secuencia%2004%20-%20Registro%20de%20lectura%20del%20sensor.vpp)

</details>

Los diagramas se exportan desde Visual Paradigm 18.1. Cuando se modifique un modelo, deben actualizarse las exportaciones SVG y PNG para conservar su correspondencia.

## Flujos funcionales

### Autenticación y sesiones

El personal de Ecoembes se autentica con correo electrónico y contraseña. Si las credenciales son válidas, el servidor genera un token de sesión que permite acceder a las operaciones reservadas al personal. El token no caduca automáticamente: se invalida al cerrar sesión o al reiniciar el servidor.

En este prototipo las sesiones se guardan en memoria. El token se pasa como parámetro en las solicitudes del personal, de acuerdo con las convenciones del ejercicio; no se considera un esquema apropiado para un despliegue de producción.

### Contenedores y lecturas

Un contenedor dispone de identificador, dirección, código postal y capacidad máxima estimada en número de envases. Al darlo de alta no se inventa ninguna medición inicial: su estado de llenado permanece sin lectura hasta que el sensor transmite una.

Las lecturas contienen una cantidad estimada de envases y uno de los niveles `GREEN`, `ORANGE` o `RED`. La fecha se determina en el servidor. Se contempla **una lectura por contenedor y día**; los duplicados para el mismo día se rechazan.

El personal puede consultar el historial de un contenedor dentro de un intervalo de fechas y obtener el estado de los contenedores de una zona postal en una fecha determinada.

### Plantas y capacidad disponible

Cada planta declara una capacidad diaria expresada en **toneladas**. La capacidad disponible se obtiene descontando la carga ya asignada para esa fecha. La consulta y asignación se limitan a la ventana prevista de **diez días** (hoy y los nueve siguientes).

En el prototipo, `PlasSBGateway` y `ContSocketGateway` simulan las dos plantas. La interfaz `RecyclingPlantGateway` permite desacoplar sus particularidades de los servicios de aplicación.

### Asignación y auditoría

El personal selecciona una planta, una fecha y uno o varios contenedores. El servidor comprueba que los contenedores tengan lecturas disponibles, que no estén asignados dos veces para la misma fecha y que su carga estimada no supere la capacidad de la planta.

La asignación es **atómica desde el punto de vista funcional**: se acepta el conjunto completo o se rechaza, sin procesar una asignación parcial. Para expresar la carga en toneladas se utiliza, como simplificación del prototipo, una equivalencia estimada de **0,03 kg por envase**.

Se registra la identidad del empleado responsable y se notifica a la planta seleccionada el número de contenedores y la cantidad estimada de envases. En el Prototipo 1 esa notificación se simula.

## Concurrencia del estado

`ContainerAppService` sincroniza las operaciones sobre su colección de contenedores; `Container` protege el historial de lecturas, incluida la comprobación y el alta de la primera lectura diaria. `AssignmentAppService` sincroniza la validación y el registro de asignaciones, de modo que una asignación rechazada no deja reservas parciales. Estas garantías se aplican a la ejecución local en memoria: no constituyen un mecanismo de coordinación entre varias instancias del servidor.

## API REST

La interfaz HTTP se organiza en las siguientes operaciones:

| Área | Método | Endpoint | Descripción |
| --- | --- | --- | --- |
| Autenticación | `POST` | `/api/auth/login` | Iniciar sesión. |
| Autenticación | `POST` | `/api/auth/logout` | Cerrar sesión. |
| Contenedores | `POST` | `/api/containers` | Registrar un contenedor. |
| Contenedores | `GET` | `/api/containers` | Obtener el listado de contenedores. |
| Lecturas | `GET` | `/api/containers/{containerId}/readings` | Consultar lecturas por intervalo. |
| Zonas | `GET` | `/api/zones/{postalCode}/containers` | Consultar contenedores de una zona a una fecha. |
| Sensores | `POST` | `/api/containers/{containerId}/readings` | Registrar una lectura del sensor. |
| Plantas | `GET` | `/api/plants` | Listar plantas de reciclaje. |
| Plantas | `GET` | `/api/plants/{plantId}/capacity` | Obtener capacidad disponible por fecha. |
| Asignaciones | `POST` | `/api/plants/{plantId}/assignments` | Registrar una asignación. |
| Asignaciones | `GET` | `/api/plants/{plantId}/assignments` | Consultar las asignaciones registradas. |

Estas once operaciones están implementadas y pueden explorarse desde [Swagger UI](http://localhost:8080/swagger-ui/index.html) con el servidor iniciado. Los errores previstos utilizan `ErrorDTO`. La descripción detallada de parámetros, respuestas y reglas de negocio se recoge en la [memoria técnica del Prototipo 1](entrega/Ecoembes%20-%20P1%20-%20Grupo%2013.pdf).

## Pruebas y evidencias

Se han documentado tres modalidades de comprobación sobre el Prototipo 1:

| Modalidad | Ejecución registrada | Evidencia |
| --- | --- | --- |
| Pruebas automatizadas JUnit | 63 pruebas en 12 suites; 0 fallos, 0 errores y 0 omitidas | [Resumen JUnit](evidencias/pruebas/resumen_junit.json) · [Gradle](evidencias/pruebas/gradle_clean_build.txt) |
| Validación funcional HTTP | 72 escenarios correctos; 0 fallidos | [Resultados HTTP](evidencias/http/resultados.json) · [Script reproducible](validacion/validar_api.mjs) |
| Swagger UI | 38 ejecuciones con respuesta esperada y 39 capturas (incluida la vista general) | [Índice de capturas](evidencias/swagger/README.md) · [Registro UI](evidencias/swagger/resultados_ui.json) |

Los registros corresponden a pruebas realizadas el **11/11/2026**. La batería HTTP comprueba también la conservación del estado ante errores, el rechazo de lecturas duplicadas y la ausencia de asignaciones parciales. Las capturas muestran ejemplos de ejecución, no 39 pruebas adicionales. La [especificación OpenAPI](evidencias/http/openapi.json) se incluye como referencia de la API exportada.

## Límites del Prototipo 1

- **Sin base de datos:** almacenamiento y sesiones en memoria; los datos se pierden al reiniciar el proceso.
- **Integraciones simuladas:** las comunicaciones reales HTTP/socket con las plantas quedan para fases posteriores.
- **Sin interfaz gráfica de negocio:** la API se valida a través de Swagger.
- **Rutas y camiones:** se contemplan en el modelo conceptual, pero no se desarrolla su planificación operativa en esta fase.
- **Sin automatización horaria real del sensor:** la comunicación diaria se representa mediante solicitudes simuladas.

## Tecnologías de referencia

Java 21 · Spring Boot 4.1.2 · Spring Web MVC · Jakarta Validation · Springdoc OpenAPI · Gradle 9.8.2 · Visual Paradigm 18.1.

---

[← Volver al README](../README.md)
