# Evidencias de Swagger UI

**Fecha de ejecución:** 11 de noviembre de 2026. **Servidor:** `http://localhost:8080`.

Esta carpeta reúne **39 capturas PNG**: una vista general de la API y evidencias de **38 peticiones ejecutadas desde Swagger UI**. El registro [`resultados_ui.json`](resultados_ui.json) conserva los resultados de las ejecuciones (37 entradas en `results` y el primer inicio de sesión en `initialLogin`).

Las capturas proceden de la interfaz del navegador durante la ejecución real de peticiones al servidor Spring Boot. Se recortaron para mejorar su legibilidad, sin alterar los códigos HTTP ni los cuerpos de respuesta. Las credenciales y tokens mostrados corresponden exclusivamente al entorno local de demostración.

Algunas respuestas JSON exceden el área visible de la captura —por ejemplo, las imágenes 05, 11 y 25—. Por tanto, esas imágenes representan una vista parcial; los registros de ejecución complementan su contenido. Las capturas corresponden a la ejecución histórica indicada y no sustituyen a las pruebas automatizadas.

## Índice de imágenes

| Archivo | Escenario | Operación | HTTP | Contenido visible |
|---|---|---|---|---|
| [00_swagger_general.png](00_swagger_general.png) | Vista general API Servidor Ecoembes | `11 operaciones en http://localhost:8080` | — | Once operaciones legibles; descripción de alcance P1. |
| [01_login_200.png](01_login_200.png) | Login válido | `POST /api/auth/login` | 200 | Token numérico visible. |
| [02_login_401.png](02_login_401.png) | Credenciales incorrectas | `POST /api/auth/login` | 401 | ErrorDTO: correo o contraseña incorrectos. |
| [03_logout_204.png](03_logout_204.png) | Cierre de sesión | `POST /api/auth/logout` | 204 | Respuesta sin cuerpo. |
| [04_token_invalidado_401.png](04_token_invalidado_401.png) | Token cerrado | `GET /api/containers` | 401 | ErrorDTO: el token no está activo. |
| [05_contenedores_listado_200.png](05_contenedores_listado_200.png) | Listado inicial | `GET /api/containers` | 200 | JSON en panel desplazable: CONT-001 400/GREEN, CONT-002 1200/ORANGE y comienzo de CONT-003. No muestra el listado completo de diez. |
| [06_contenedor_alta_201.png](06_contenedor_alta_201.png) | Alta de CONT-011 | `POST /api/containers` | 201 | Entrada y DTO completos; cuatro campos derivados null. |
| [07_contenedor_duplicado_409.png](07_contenedor_duplicado_409.png) | Alta duplicada CONT-011 | `POST /api/containers` | 409 | ErrorDTO de identificador existente. |
| [08_contenedor_sin_lectura.png](08_contenedor_sin_lectura.png) | Historial inicialmente vacío | `GET /api/containers/CONT-011/readings` | 200 | Intervalo 2026-11-07 a 2026-11-11; lista vacía. |
| [09_historial_200.png](09_historial_200.png) | Cinco lecturas históricas | `GET /api/containers/CONT-001/readings` | 200 | Cinco objetos completos: 80,160,240,320,400 del 7 al 11 de noviembre. |
| [10_historial_fecha_invalida_400.png](10_historial_fecha_invalida_400.png) | Intervalo invertido | `GET /api/containers/CONT-001/readings` | 400 | from 2026-11-11, to 2026-11-07; ErrorDTO de intervalo inválido. |
| [11_zona_200.png](11_zona_200.png) | Consulta CP 48009 | `GET /api/zones/48009/containers` | 200 | date 2026-11-11. Se ven CONT-001, CONT-002 y CONT-011 con 125; final del tercer objeto fuera del área desplazable. |
| [12_sensor_primera_lectura_201.png](12_sensor_primera_lectura_201.png) | Primera lectura | `POST /api/containers/CONT-011/readings` | 201 | Entrada 125/GREEN; salida completa fecha 2026-11-11. |
| [13_sensor_duplicado_409.png](13_sensor_duplicado_409.png) | Lectura repetida | `POST /api/containers/CONT-011/readings` | 409 | Entrada 140/GREEN y ErrorDTO de lectura existente. |
| [14_sensor_estado_conservado_200.png](14_sensor_estado_conservado_200.png) | Lectura conservada | `GET /api/containers/CONT-011/readings` | 200 | Un único objeto 125/GREEN, conservado tras 409. |
| [15_plantas_listado_200.png](15_plantas_listado_200.png) | Listado de plantas | `GET /api/plants` | 200 | Dos objetos completos: CONTSOCKET y PLASSB. |
| [16_capacidad_inicial_200.png](16_capacidad_inicial_200.png) | Capacidad inicial PLASSB | `GET /api/plants/PLASSB/capacity` | 200 | 2026-11-11; total 0.4, asignado 0, disponible 0.4. |
| [17_asignacion_correcta_201.png](17_asignacion_correcta_201.png) | Asignación correcta | `POST /api/plants/PLASSB/assignments` | 201 | CONT-003/006/009; 3 contenedores,4950 envases,0.14850000000000002 t; empleado y registro visibles. |
| [18_capacidad_actualizada_200.png](18_capacidad_actualizada_200.png) | Capacidad tras asignar | `GET /api/plants/PLASSB/capacity` | 200 | Disponible 0.2515 t; valor asignado double sin alterar. |
| [19_asignaciones_listado_200.png](19_asignaciones_listado_200.png) | Listado de asignaciones | `GET /api/plants/PLASSB/assignments` | 200 | Una asignación completa, id1, 4950 envases y auditoría. |
| [20_asignacion_duplicada_409.png](20_asignacion_duplicada_409.png) | Duplicado entre plantas | `POST /api/plants/CONTSOCKET/assignments` | 409 | CONT-003 ya asignado para 2026-11-11. |
| [21_capacidad_insuficiente_409.png](21_capacidad_insuficiente_409.png) | Capacidad insuficiente | `POST /api/plants/CONTSOCKET/assignments` | 409 | Fecha 2026-11-12 y diez IDs visibles; ErrorDTO capacidad insuficiente. |
| [22_asignacion_fecha_invalida_400.png](22_asignacion_fecha_invalida_400.png) | Fecha nula | `POST /api/plants/PLASSB/assignments` | 400 | date null visible; ErrorDTO fecha obligatoria. |
| [23_login_reanudado_200.png](23_login_reanudado_200.png) | Nuevo login | `POST /api/auth/login` | 200 | Credenciales ficticias y token numérico visibles. |
| [24_sensor_semilla_duplicado_409.png](24_sensor_semilla_duplicado_409.png) | Duplicado sobre semilla | `POST /api/containers/CONT-001/readings` | 409 | Entrada 500/ORANGE; ErrorDTO lectura existente del 2026-11-11. |
| [25_sensor_semilla_conservado_200.png](25_sensor_semilla_conservado_200.png) | Estado semilla conservado | `GET /api/containers` | 200 | CONT-001 conserva 400/GREEN. Panel de listado desplazable, no muestra los diez completos. |
| [26_capacidad_cero_409.png](26_capacidad_cero_409.png) | Capacidad cero | `POST /api/plants/PLASSB/assignments` | 409 | Fecha 2026-11-17, CONT-001; ErrorDTO capacidad insuficiente. |
| [27_seleccion_vacia_400.png](27_seleccion_vacia_400.png) | Selección vacía | `POST /api/plants/PLASSB/assignments` | 400 | containerIds [] visible; ErrorDTO selección inválida. |
| [28_sensor_capacidad_400.png](28_sensor_capacidad_400.png) | Cantidad sobre capacidad | `POST /api/containers/CONT-011/readings` | 400 | Entrada 1001/RED; ErrorDTO cantidad o nivel incorrectos. |
| [29_sensor_nulo_400.png](29_sensor_nulo_400.png) | Cantidad null | `POST /api/containers/CONT-011/readings` | 400 | packageCount null visible; ErrorDTO campos obligatorios. |
| [30_sensor_omitido_400.png](30_sensor_omitido_400.png) | Cantidad omitida | `POST /api/containers/CONT-011/readings` | 400 | Solo fillLevel GREEN en entrada; ErrorDTO campos obligatorios. |
| [31_sensor_nivel_invalido_400.png](31_sensor_nivel_invalido_400.png) | Nivel inválido | `POST /api/containers/CONT-011/readings` | 400 | 10/BLUE visible; ErrorDTO formato inválido. |
| [32_historial_inexistente_404.png](32_historial_inexistente_404.png) | Contenedor inexistente | `GET /api/containers/NO-EXISTE/readings` | 404 | ErrorDTO no existe contenedor. |
| [33_planta_inexistente_404.png](33_planta_inexistente_404.png) | Planta inexistente | `GET /api/plants/NO-EXISTE/capacity` | 404 | ErrorDTO no existe planta. |
| [34_alta_auxiliar_sin_lectura_201.png](34_alta_auxiliar_sin_lectura_201.png) | Alta auxiliar CONT-012 | `POST /api/containers` | 201 | DTO completo con cuatro campos derivados null. |
| [35_asignacion_sin_lectura_409.png](35_asignacion_sin_lectura_409.png) | Asignación sin lectura | `POST /api/plants/PLASSB/assignments` | 409 | CONT-001 y CONT-012 en entrada; ErrorDTO CONT-012 sin lecturas. |
| [36_capacidad_sin_cambios_200.png](36_capacidad_sin_cambios_200.png) | Capacidad conservada | `GET /api/plants/PLASSB/capacity` | 200 | Disponible 0.2515 t, mismo valor tras fallos. |
| [37_asignaciones_sin_parciales_200.png](37_asignaciones_sin_parciales_200.png) | Sin asignaciones parciales | `GET /api/plants/PLASSB/assignments` | 200 | Permanece una asignación, id1, de CONT-003/006/009. |
| [38_logout_final_204.png](38_logout_final_204.png) | Logout final | `POST /api/auth/logout` | 204 | Sin cuerpo de respuesta. |

## Informes relacionados

- [Resultados de las ejecuciones en Swagger UI](resultados_ui.json).
- [Resultados de las 72 validaciones HTTP](../http/resultados.json).
- [Especificación OpenAPI exportada](../http/openapi.json).
- [Notificaciones simuladas observadas](../http/notificaciones_simuladas.txt).
- [Registro de compilación y pruebas Gradle](../pruebas/gradle_clean_build.txt).
- [Resumen de las 63 pruebas JUnit](../pruebas/resumen_junit.json).

Las **39 capturas** no representan 39 pruebas adicionales: documentan visualmente 38 peticiones y la vista general. Los escenarios HTTP y las ejecuciones de Swagger se registraron por separado.
