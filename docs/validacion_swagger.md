# Validación integral Swagger — Ecoembes P10

## 1. Dictamen y alcance

El Prototipo 1 supera los escenarios funcionales obligatorios ejecutados: las **once operaciones REST tienen al menos una ejecución exitosa real en Swagger**, incluida una repetición completa de éxitos sobre la versión final. Se han demostrado los rechazos del sensor sin sobrescritura, el logout efectivo, la exclusión de asignaciones duplicadas y la preservación de capacidad y asignaciones tras errores.

El registro contiene **90 casos: 89 PASS, 0 FAIL, 1 BLOCKED y 0 NOT RUN**. Los 89 casos HTTP se ejecutaron y superaron; el caso adicional I01, token alfabético, quedó bloqueado por la validación de Swagger antes de enviar HTTP. No se atribuye un código HTTP a ese intento. I03 sí demuestra un 400 real de conversión de token mediante un entero fuera del rango long.

Esta conclusión se limita al contrato funcional del encargo P10. No representa cobertura completa del software ni cobertura del 100 % de todas las entradas posibles. Se verificaron once de las once operaciones previstas y los 90 casos concretos de la [matriz](matriz_pruebas_swagger.md).

## 2. Fecha, entorno y trazabilidad

- Ejecución: **11 de noviembre de 2026**, aproximadamente **05:36–05:50**, **Europe/Madrid**, CET (UTC+1). Las cabeceras HTTP muestran UTC; createdAt refleja la hora local del servidor.
- LocalDate.now() comprobado en las lecturas semilla: **2026-11-11**. Historial: 2026-11-07…2026-11-11; hoy+1: 2026-11-12; hoy+6: 2026-11-17; hoy+9: 2026-11-20; hoy+10: 2026-11-21. Ningún bloque cruzó medianoche.
- Sistema: Windows local; Gradle informa Windows 10, versión 10.0 amd64.
- Java: Microsoft OpenJDK **21.0.12.1+1-LTS**.
- Gradle Wrapper: **9.8.2**.
- Spring Boot: **4.1.2**; Springdoc **3.2.0**; Swagger UI **5.34.0**; OpenAPI **3.1**.
- Navegador: Google Chrome local, controlado mediante Computer Use. Se comprobó acceso real al mismo localhost del servidor; no se utilizó un navegador cloud.
- Rama: **docs/p10-swagger-validation**, creada desde origin/main.
- Base: **389d66feabf41ba46de5f9215049e394e5c344b7**, merge de la PR #7. Contiene DtoAssembler y la fachada REST P9. Se verificó mediante fetch y el historial Git.
- Validación A–I: **60b32e32b4e3a4dc1032f0efa0efb4585bd5dfe8**.
- Código final validado S01–S12 y OpenAPI: **441f10d4bb62be3c72806f0a44184fe69a65587f**. El único cambio posterior al recorrido A–I es una anotación documental del campo fillLevel. Los commits posteriores de entrega incorporan documentación y evidencias, sin cambiar ese código.

El árbol inicial estaba limpio, en dev-aritz. La referencia local main estaba desactualizada; se tomó como base el main remoto actualizado, sin fusionar ni alterar main. No se encontró contrato_diseno_prototipo1_v1.5.md al buscar en el árbol del proyecto Ecoembes. Tampoco se dispuso de un enunciado oficial adicional: se aplicaron el contrato incluido en el encargo y las firmas reales del repositorio.

## 3. Compilación y arranque reproducible

Desde la raíz del proyecto, con Java 21:

~~~powershell
.\gradlew.bat clean build
.\gradlew.bat bootRun
~~~

Abrir [Swagger UI](http://localhost:8080/swagger-ui/index.html). La especificación se publica en [v3/api-docs](http://localhost:8080/v3/api-docs).

El primer acceso, antes del arranque, devolvió conexión rechazada; no se registró como prueba funcional. Tras arrancar, se comprobó HTTP 200 de OpenAPI y carga de Swagger antes de iniciar las capturas. La herramienta inicial de terminal falló por compatibilidad CET de Windows; se utilizó ejecución local de procesos para invocar el mismo Wrapper. Esto no requirió cambiar Windows ni el proyecto.

La sesión A–I arrancó sobre datos semilla. Después del último ajuste documental se detuvo el proceso propio y se repitió clean build y bootRun. S02 confirmó la restauración de los diez contenedores y los 11.950 envases. El proceso final es EcoembesApplication, PID **19104**, desde build/classes/java/main de este repositorio; se verificó su zona Europe/Madrid.

Para repetir los casos en su orden, reiniciar primero el servidor. No copiar tokens de este informe: realizar un login nuevo en Swagger usando las credenciales ficticias del encargo. No se conservan contraseñas ni valores de sesión en los documentos, JSON de resultados o capturas.

## 4. Método y evidencias

Se expandió cada operación, se pulsó **Try it out**, se rellenaron parámetros y/o JSON, y se pulsó **Execute**. Se leyó la tabla **Server response**, su código y su JSON real. Los controles y respuestas se inspeccionaron mediante Computer Use y lectura del DOM de la interfaz; no se sustituyeron las peticiones por curl, fetch o MockMvc.

Las comprobaciones independientes de v3/api-docs, procesos y logs son complementarias y están identificadas como tales. La notificación a las plantas se comprobó en la salida real del servidor, porque el contrato REST no devuelve el destinatario de esa notificación como evidencia de su ejecución.

Se guardaron **91 capturas PNG auténticas** en [evidencias/swagger/](evidencias/swagger/README.md): una vista general y una captura por cada uno de los 90 casos. Algunas filas enlazan también la captura de otra comprobación relacionada. Se recortó cada operación y se ocultaron los paneles con secretos mediante rectángulos negros rotulados. No se modificaron códigos ni respuestas. El navegador entrega JPEG; la conversión a PNG conserva el contenido capturado, con la ocultación indicada.

Los paneles de respuestas largas tienen una altura limitada en Swagger. Las imágenes conservan esa vista real; los cuerpos completos observados en el DOM se guardan en [resultados_swagger.json](evidencias/swagger/resultados_swagger.json), con tokens omitidos. Este JSON complementa las imágenes; no se presenta como una captura.

## 5. Resumen de resultados

| Bloque | Casos | PASS | FAIL | BLOCKED | NOT RUN |
|---|---:|---:|---:|---:|---:|
| A — Autenticación | 7 | 7 | 0 | 0 | 0 |
| B — Listado inicial | 1 | 1 | 0 | 0 | 0 |
| C — Historial | 3 | 3 | 0 | 0 | 0 |
| D — Alta y ausencia de lecturas | 7 | 7 | 0 | 0 | 0 |
| E — Sensor y conservación | 10 | 10 | 0 | 0 | 0 |
| F — Zonas | 4 | 4 | 0 | 0 | 0 |
| G — Plantas y capacidad | 5 | 5 | 0 | 0 | 0 |
| H — Asignaciones y atomicidad | 33 | 33 | 0 | 0 | 0 |
| I — Casos adicionales | 8 | 7 | 0 | 1 | 0 |
| S — Repetición final | 12 | 12 | 0 | 0 | 0 |
| **Total** | **90** | **89** | **0** | **1** | **0** |

La suite JUnit/MockMvc de 61 tests se contabiliza aparte; no está sumada a los 90 casos de la matriz.

## 6. Once operaciones verificadas

| Operación | Caso inicial | Repetición final | Éxito observado |
|---|---|---|---:|
| POST /api/auth/login | A01, A07 | S01 | 200 |
| POST /api/auth/logout | A05 | S11 | 204 |
| POST /api/containers | D01 | S04 | 201 |
| GET /api/containers | B01 | S02 | 200 |
| GET /api/containers/{containerId}/readings | C01 | S03 | 200 |
| GET /api/zones/{postalCode}/containers | F01 | S06 | 200 |
| POST /api/containers/{containerId}/readings | E03 | S05 | 201 |
| GET /api/plants | G01 | S07 | 200 |
| GET /api/plants/{plantId}/capacity | G02 | S08 | 200 |
| POST /api/plants/{plantId}/assignments | H01 | S09 | 201 |
| GET /api/plants/{plantId}/assignments | H03 | S10 | 200 |

El documento OpenAPI contiene exactamente once operaciones. Nueve declaran token obligatorio; login y sensor no lo requieren. La fachada mantiene esa validación de sesión. El éxito del sensor se ejecutó sin token.

## 7. Datos y cambios de estado observados

B01 confirmó diez contenedores, los ocho campos de ContainerDTO y los valores semilla; la suma es 11.950 envases, equivalentes a 0,3585 t. CONT-001 tenía 400, CONT-006 1.900 y CONT-009 1.600 envases. C01 devolvió cinco lecturas inclusivas de CONT-001: 80, 160, 240, 320 y 400.

D01 creó CONT-011 con capacidad 1.000 y cuatro campos de lectura null. D05 rechazó su asignación por falta de lecturas. D05-S y D05-C confirmaron lista vacía y capacidad 0,40 t intacta.

E01 rechazó con 409 una lectura de 500/ORANGE sobre CONT-001. E02 confirmó que conservó 400/GREEN/0,012 t. E03 registró 125/GREEN en CONT-011; E04 rechazó 140/GREEN con 409. E05 y E05-H confirmaron 125/GREEN, 0,00375 t y una sola lectura del día. F01 devolvió CONT-001, CONT-002 y CONT-011 para 48009/hoy.

H01 asignó a PLASSB CONT-003, CONT-006 y CONT-009: tres contenedores, 4.950 envases, 0,1485 t, assignedBy de Ana y createdAt presente. H02 mostró disponible 0,2515 t y H03 devolvió la misma asignación. La serialización double contiene 0.14850000000000002; se conserva ese valor real en evidencias y se compara con tolerancia 10⁻¹². No se redondeó artificialmente el JSON.

En la sesión A–I se observó una única línea de notificación PlasSB y ninguna ContSocket. La repetición final produjo otra notificación PlasSB en su propia sesión. Véase [arranque y notificaciones](evidencias/servidor-arranque-y-notificaciones.txt). No hubo comunicación real con plantas.

## 8. Errores y atomicidad

Se observaron errores JSON con message: **400** por email vacío, fechas invertidas o mal formadas, capacidad/código postal inválidos, cantidad/nivel ausente o inválido, lista vacía o repetida, fecha de asignación ausente y ventana de fechas excedida; **401** por contraseña incorrecta, token inventado o invalidado; **404** por contenedor o planta inexistentes; **409** por alta duplicada, lecturas duplicadas, contenedor sin lecturas, asignación repetida, capacidad cero e insuficiente.

El logout produjo 204 sin cuerpo; el mismo token fue rechazado con 401 en A06 y de nuevo en S12.

Se verificaron **once asignaciones rechazadas**, cada una seguida de dos consultas nuevas en Swagger: listado de asignaciones sin filtro y capacidad. D05 y H04–H13 no dejaron registros parciales ni reservaron toneladas adicionales. H13 solicitó los diez contenedores iniciales a CONTSOCKET para 2026-11-12: 0,3585 t excedían 0,30 t; el rechazo fue 409 y la capacidad siguió en 0,30 t. Las peticiones de fecha/planta inválidas se contrastaron con el listado global de la planta de referencia y su capacidad válida, según la matriz.

## 9. Auditoría y correcciones OpenAPI

No se encontraron defectos funcionales reproducibles que exigieran cambiar servicios, dominio, estado o control de errores. Se aplicaron mejoras documentales sin nuevas dependencias:

1. Las respuestas 400/401/404/409 heredaban esquemas de éxito; ahora declaran explícitamente application/json y ErrorDTO.
2. Se ampliaron la descripción general y las descripciones de token, identificadores, fechas, sensor sin token y asignación íntegra.
3. Se añadieron ejemplos y campos obligatorios documentados en los DTO de entrada; la contraseña se identifica como writeOnly y password. No se incluyen contraseñas reales en ejemplos.
4. Se documentó que los cuatro campos de lectura de ContainerDTO admiten null. La primera anotación de fillLevel combinaba tipo null con un enum que excluía null; la revisión final detectó esa contradicción. Se corrigió usando una representación documental string/null con los tres niveles en la descripción. El DTO Java sigue siendo FillLevel y SensorReadingDTO conserva su enum.

La [especificación final](evidencias/openapi-verificado.json) y la [auditoría técnica](evidencias/auditoria-openapi.json) acreditan once resúmenes, los parámetros de sesión y los esquemas de error. Se mantuvo la agrupación Ecoembes y las once rutas originales. Swagger se recargó después del cambio y S01–S12 verificaron su funcionamiento sobre la versión final.

## 10. Compilación y tests

clean build inicial: **BUILD SUCCESSFUL**, código de salida 0. Después de las mejoras y tras la última corrección nullable: **BUILD SUCCESSFUL**, código de salida 0; última ejecución completada en 19 segundos.

| Suite | Tests |
|---|---:|
| EcoembesApplicationTests | 1 |
| AssignmentDomainTest | 5 |
| DtoAssemblerTest | 4 |
| GlobalExceptionHandlerTest | 5 |
| EcoembesFacadeHttpTest | 5 |
| EcoembesFacadeTest | 13 |
| AssignmentAppServiceTest | 8 |
| AuthAppServiceTest | 6 |
| ContainerAppServiceTest | 8 |
| PlantAppServiceTest | 6 |
| **Total** | **61** |

**0 fallos, 0 errores y 0 omitidos**. No se eliminaron ni relajaron tests. No se añadieron tests que reproduzcan únicamente anotaciones; las mejoras se verificaron contra el OpenAPI generado y Swagger real.

Evidencias: [build inicial](evidencias/gradle-clean-build-inicial.txt), [build final](evidencias/gradle-clean-build-final.txt), [resumen de tests](evidencias/resumen-tests.json). La advertencia de OpenJDK sobre class data sharing durante tests no impidió el build. Las advertencias de Springdoc indican que Swagger está habilitado, comportamiento necesario para esta entrega.

## 11. Evidencias recomendadas para la defensa

- [Vista general de las once operaciones](evidencias/swagger/00_swagger_general.png).
- [Logout 204](evidencias/swagger/03_logout_204.png) y [token invalidado 401](evidencias/swagger/04_token_invalidado_401.png).
- [Alta con lectura nula](evidencias/swagger/61_final_alta_nullable_201.png).
- [Duplicado del sensor 409](evidencias/swagger/13_sensor_duplicado_409.png) y [historial conservado](evidencias/swagger/14b_sensor_historial_conservado_200.png).
- [Asignación final 201](evidencias/swagger/66_final_asignacion_201.png) y [capacidad actualizada](evidencias/swagger/18_capacidad_actualizada_200.png).
- [Asignación duplicada 409](evidencias/swagger/20_asignacion_duplicada_409.png).
- [Capacidad insuficiente 409](evidencias/swagger/21_capacidad_insuficiente_409.png), [sin asignación parcial](evidencias/swagger/21_capacidad_insuficiente_409_sin_asignacion_parcial.png) y [capacidad intacta](evidencias/swagger/21_capacidad_insuficiente_409_capacidad_intacta.png).

## 12. Limitaciones e incidencias pendientes

**I01 — BLOCKED en Swagger:** introducir un token alfabético activa «Value must be an integer» y evita la petición. El primer borrador del registro había leído la respuesta 200 de una ejecución anterior; se detectó la validación local, se limpió esa respuesta y se corrigió el registro a BLOCKED, sin atribuir al servidor un 200 inexistente para este caso. La [captura](evidencias/swagger/50_token_alfabetico_bloqueado_swagger.png) demuestra el bloqueo sin respuesta HTTP. No se desactivó la validación de Swagger. I03 cubre un 400 real de token fuera de rango.

No se dispuso del archivo rector v1.5 ni de un enunciado oficial independiente. La revisión académica debe contrastar esta entrega con esos documentos si existen fuera del árbol inspeccionado.

No se realizaron pruebas de carga, concurrencia, seguridad exhaustiva, paso de medianoche ni dispositivos físicos. No se probaron bases de datos, persistencia, frontend o comunicaciones externas reales porque están fuera del Prototipo 1. Los tests automatizados no se presentan como capturas de Swagger.

## 13. Estado de entrega y preparación para P11

Se conserva el alcance del Prototipo 1: colecciones Java en memoria, plantas y sensor simulados, patrones Façade/AppService/DTO/StateManagement y camiones/rutas sin operaciones REST. No se modificaron UML, dependencias, rutas ni lógica funcional. No se fusionó main ni se reescribió historial.

El servidor final queda en funcionamiento con once contenedores y una asignación de demostración; el último token de prueba fue invalidado. Para una demostración desde cero, reiniciar bootRun y repetir el orden A–H de la matriz.

**Valoración técnica:** preparado para la revisión P11 y la demostración presencial del Prototipo 1 en el alcance comprobado. Queda expresamente pendiente el 400 por token alfabético enviado desde Swagger, bloqueado por su propio formulario, y el contraste con el contrato v1.5 no localizado. Esas reservas no invalidan los once éxitos ni los escenarios funcionales obligatorios observados. No se afirma que todos los casos registrados estén en PASS.
