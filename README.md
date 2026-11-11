# Ecoembes

**Sistema de gestión de contenedores y plantas de reciclaje**  
*Diseño de Software · Universidad de Deusto*

Ecoembes es un servidor basado en **Java y Spring Boot** que proporciona una API REST para gestionar contenedores inteligentes, consultar sus lecturas de llenado y coordinar la asignación de residuos a plantas de reciclaje según la capacidad disponible.

El desarrollo se organiza en prototipos incrementales. El **Prototipo 1** se centra en el diseño del servidor, su API y la simulación de los sistemas externos, sin persistencia en base de datos.

[**Arquitectura y decisiones de diseño**](docs/ARCHITECTURE.md) · [**Diagramas UML**](docs/ARCHITECTURE.md#diagramas-uml) · [**API REST prevista**](docs/ARCHITECTURE.md#api-rest-prevista)

> **Estado actual:** estructura del proyecto inicializada y servidor Spring Boot en funcionamiento. Las operaciones de negocio se incorporarán durante el desarrollo del prototipo.

## Funcionalidades

El diseño del Prototipo 1 contempla:

- **Autenticación del personal:** inicio y cierre de sesión mediante correo electrónico, contraseña y token temporal.
- **Gestión de contenedores:** registro y listado de contenedores, con su ubicación y capacidad.
- **Lecturas de sensores:** recepción diaria de la cantidad estimada de envases y del nivel de llenado (`GREEN`, `ORANGE` o `RED`).
- **Consultas de información:** historial por intervalo de fechas y estado de contenedores agrupados por zona postal.
- **Gestión de plantas:** consulta de capacidad de tratamiento disponible para fechas concretas.
- **Asignaciones y auditoría:** selección de contenedores, validación de capacidad y registro de la persona responsable.
- **Integraciones extensibles:** dos plantas simuladas detrás de una interfaz común, preparada para incorporar nuevas implementaciones.

## Diseño UML

La arquitectura se documenta con un diagrama de clases y cuatro diagramas de secuencia. **Todos los SVG se pueden visualizar directamente en la [documentación de arquitectura](docs/ARCHITECTURE.md#diagramas-uml)**, sin descargar archivos.

| Diagrama | Visualización y archivos |
| --- | --- |
| **Diagrama de clases** | [Ver en ARCHITECTURE.md](docs/ARCHITECTURE.md#diagrama-de-clases) · [SVG](docs/diagramas/svg/Diagrama%20de%20Clases.svg) · [PNG](docs/diagramas/png/Diagrama%20de%20Clases.png) · [VPP](docs/visual-paradigm/Diagrama%20de%20Clases.vpp) |
| **SD-01 · Sesión y contenedores** | [Ver en ARCHITECTURE.md](docs/ARCHITECTURE.md#diagramas-de-secuencia) · [SVG](docs/diagramas/svg/Diagrama%20de%20Secuencia%2001%20-%20Sesi%C3%B3n%2C%20alta%20y%20consulta%20de%20contenedores.svg) · [PNG](docs/diagramas/png/Diagrama%20de%20Secuencia%2001%20-%20Sesi%C3%B3n%2C%20alta%20y%20consulta%20de%20contenedores.png) · [VPP](docs/visual-paradigm/Diagrama%20de%20Secuencia%2001%20-%20Sesi%C3%B3n%2C%20alta%20y%20consulta%20de%20contenedores.vpp) |
| **SD-02 · Capacidad y asignación** | [Ver en ARCHITECTURE.md](docs/ARCHITECTURE.md#diagramas-de-secuencia) · [SVG](docs/diagramas/svg/Diagrama%20de%20Secuencia%2002%20-%20Capacidad%2C%20asignaci%C3%B3n%20y%20auditor%C3%ADa.svg) · [PNG](docs/diagramas/png/Diagrama%20de%20Secuencia%2002%20-%20Capacidad%2C%20asignaci%C3%B3n%20y%20auditor%C3%ADa.png) · [VPP](docs/visual-paradigm/Diagrama%20de%20Secuencia%2002%20-%20Capacidad%2C%20asignaci%C3%B3n%20y%20auditor%C3%ADa.vpp) |
| **SD-03 · Cierre de sesión** | [Ver en ARCHITECTURE.md](docs/ARCHITECTURE.md#diagramas-de-secuencia) · [SVG](docs/diagramas/svg/Diagrama%20de%20Secuencia%2003%20-%20Logout%20e%20invalidaci%C3%B3n%20del%20token.svg) · [PNG](docs/diagramas/png/Diagrama%20de%20Secuencia%2003%20-%20Logout%20e%20invalidaci%C3%B3n%20del%20token.png) · [VPP](docs/visual-paradigm/Diagrama%20de%20Secuencia%2003%20-%20Logout%20e%20invalidaci%C3%B3n%20del%20token.vpp) |
| **SD-04 · Lectura de sensor** | [Ver en ARCHITECTURE.md](docs/ARCHITECTURE.md#diagramas-de-secuencia) · [SVG](docs/diagramas/svg/Diagrama%20de%20Secuencia%2004%20-%20Registro%20de%20lectura%20del%20sensor.svg) · [PNG](docs/diagramas/png/Diagrama%20de%20Secuencia%2004%20-%20Registro%20de%20lectura%20del%20sensor.png) · [VPP](docs/visual-paradigm/Diagrama%20de%20Secuencia%2004%20-%20Registro%20de%20lectura%20del%20sensor.vpp) |

Los archivos fuente de Visual Paradigm se conservan en [`docs/visual-paradigm/`](docs/visual-paradigm/).

## Tecnologías

| Área | Tecnología |
| --- | --- |
| Lenguaje | **Java 21** |
| Framework | **Spring Boot 4.1.2** |
| API REST | Spring Web MVC |
| Validación | Jakarta Validation |
| Documentación de API | Springdoc OpenAPI / Swagger UI |
| Gestión de contraseñas | BCrypt (`spring-security-crypto`) |
| Construcción | **Gradle 9.8.2**, mediante Gradle Wrapper |
| Pruebas | JUnit Platform |
| Modelado UML | Visual Paradigm 18.1 |

## Ejecución local

**Requisitos:** JDK 21 y conexión a Internet para la primera descarga de dependencias. No es necesario instalar Gradle por separado.

Desde la raíz del repositorio:

**Windows (PowerShell)**

```powershell
.\gradlew.bat bootRun
```

**Linux / macOS**

```bash
./gradlew bootRun
```

Una vez iniciada la aplicación:

| Servicio | Dirección |
| --- | --- |
| Servidor HTTP | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Especificación OpenAPI | http://localhost:8080/v3/api-docs |

> La ruta `/` puede devolver `404` porque no se ha definido una página de inicio. Para explorar la API se utiliza Swagger UI.

Para ejecutar las pruebas:

```powershell
.\gradlew.bat test
```

## Estructura del proyecto

```text
src/main/java/es/deusto/sd/ecoembes/
├── EcoembesApplication.java
├── facade/       # API REST
├── service/      # Casos de uso y reglas de aplicación
├── dto/          # Datos de entrada y salida
├── domain/       # Modelo de dominio
├── state/        # Sesiones y estado en memoria
├── external/     # Integraciones con plantas
└── exception/    # Errores y respuestas HTTP
```

Las responsabilidades de cada paquete se desarrollan en la **[documentación de arquitectura](docs/ARCHITECTURE.md#organización-del-código)**.

## Alcance del Prototipo 1

El estado del servidor se mantiene **en memoria**, y las comunicaciones con las plantas externas se **simulan**. La persistencia, las comunicaciones reales con los sistemas de las plantas y la interfaz gráfica de usuario se reservan para prototipos posteriores.
