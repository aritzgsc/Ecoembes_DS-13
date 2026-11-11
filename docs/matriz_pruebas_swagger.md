# Matriz de pruebas Swagger — P10

Fecha: **11-11-2026**, zona **Europe/Madrid (CET, UTC+1)**. Día del servidor: **2026-11-11**.

Universo: 90 casos registrados; **89 PASS, 0 FAIL, 1 BLOCKED, 0 NOT RUN**. Los 89 casos HTTP se enviaron pulsando Execute en Swagger mediante Computer Use. I01 quedó bloqueado antes del envío por validación de Swagger; no se atribuye al servidor ninguna respuesta para ese intento.

La suite automatizada de 61 tests y las comprobaciones técnicas de OpenAPI/logs se contabilizan aparte en el [informe](validacion_swagger.md). Todas las respuestas reales, con credenciales omitidas, se conservan en [resultados_swagger.json](evidencias/swagger/resultados_swagger.json). Las capturas de listados largos muestran la porción visible del panel; el JSON complementario conserva su contenido completo leído de Swagger.

A–I: commit **60b32e32b4e3a4dc1032f0efa0efb4585bd5dfe8**. S01–S12: commit final **441f10d4bb62be3c72806f0a44184fe69a65587f**, tras ajustar exclusivamente la documentación de fillLevel nullable y reiniciar.

Los sufijos **-S** y **-C** son peticiones nuevas después de cada asignación rechazada: verifican respectivamente el listado completo de asignaciones de la planta y su capacidad. Si la fecha o planta solicitada no existe, se usa PLASSB/hoy como referencia y el listado sin filtro confirma la ausencia de registros para otras fechas. Comparación de toneladas con tolerancia absoluta de 10⁻¹² por la representación double.

## A — Autenticación

| ID | Endpoint | Escenario | Datos de entrada | Esperado | Obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| A01 | POST /api/auth/login | Login correcto de Ana | Correo de Ana y contraseña ficticia (omitida) | 200; token numérico | 200; token numérico recibido (oculto) | PASS | [PNG](evidencias/swagger/01_login_200.png) |
| A02 | POST /api/auth/login | Contraseña incorrecta | Ana; contraseña incorrecta omitida | 401; ErrorDTO con message | 401; ErrorDTO.message: Correo o contraseña incorrectos. | PASS | [PNG](evidencias/swagger/02_login_401.png) |
| A03 | POST /api/auth/login | Email vacío | email vacío; contraseña omitida | 400; ErrorDTO con message | 400; ErrorDTO.message: El correo y la contraseña son obligatorios. | PASS | [PNG](evidencias/swagger/23_login_email_vacio_400.png) |
| A04 | GET /api/containers | Token numérico inventado | Token numérico inactivo (omitido) | 401; ErrorDTO con message | 401; ErrorDTO.message: El token no está activo. | PASS | [PNG](evidencias/swagger/24_token_inventado_401.png) |
| A05 | POST /api/auth/logout | Logout correcto | Sesión activa de Ana (token omitido) | 204; sin cuerpo | 204; cuerpo vacío | PASS | [PNG](evidencias/swagger/03_logout_204.png) |
| A06 | GET /api/containers | Reutilizar token invalidado | Misma sesión después del logout (token omitido) | 401; sesión inactiva | 401; ErrorDTO.message: El token no está activo. | PASS | [PNG](evidencias/swagger/04_token_invalidado_401.png) |
| A07 | POST /api/auth/login | Nuevo login para continuar | Ana; contraseña omitida | 200; nuevo token numérico | 200; token numérico recibido (oculto) | PASS | [PNG](evidencias/swagger/25_nuevo_login_200.png) |

## B — Listado inicial

| ID | Endpoint | Escenario | Datos de entrada | Esperado | Obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| B01 | GET /api/containers | Listado y datos semilla | Sesión activa de Ana | 200; 10 contenedores, 11.950 envases, 0,3585 t y ocho campos ContainerDTO | 200; 10 contenedores; 11950 envases; CONT-001: 400/GREEN/0.012 t; CONT-002: 1200/ORANGE/0.036 t; CONT-003: 1450/RED/0.0435 t; CONT-004: 900/GREEN/0.027 t; CONT-005: 1500/ORANGE/0.045 t; CONT-006: 1900/RED/0.057 t; CONT-007: 700/GREEN/0.021 t; CONT-008: 1300/ORANGE/0.039 t; CONT-009: 1600/RED/0.048 t; CONT-010: 1000/ORANGE/0.03 t | PASS | [PNG](evidencias/swagger/05_contenedores_listado_200.png) |

## C — Historial

| ID | Endpoint | Escenario | Datos de entrada | Esperado | Obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| C01 | GET /api/containers/{containerId}/readings | Historial inclusivo de cinco días | CONT-001; 2026-11-07…2026-11-11 | 200; 5 lecturas: 80,160,240,320,400 | 200; 5 lecturas; 2026-11-07: 80 GREEN; 2026-11-08: 160 GREEN; 2026-11-09: 240 GREEN; 2026-11-10: 320 GREEN; 2026-11-11: 400 GREEN | PASS | [PNG](evidencias/swagger/09_historial_200.png) |
| C02 | GET /api/containers/{containerId}/readings | Intervalo invertido | CONT-001; from=2026-11-11; to=2026-11-07 | 400; ErrorDTO con message | 400; ErrorDTO.message: El intervalo de fechas no es válido. | PASS | [PNG](evidencias/swagger/10_historial_fecha_invalida_400.png) |
| C03 | GET /api/containers/{containerId}/readings | Contenedor inexistente | CONT-999; intervalo válido | 404; ErrorDTO con message | 404; ErrorDTO.message: No existe el contenedor CONT-999. | PASS | [PNG](evidencias/swagger/26_historial_inexistente_404.png) |

## D — Alta y asignación sin lectura

| ID | Endpoint | Escenario | Datos de entrada | Esperado | Obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| D01 | POST /api/containers | Alta sin lecturas | {"id":"CONT-011","address":"Calle de Prueba 11, Bilbao","postalCode":"48009","capacity":1000} | 201; cuatro campos de lectura null | 201; CONT-011; packageCount=null, fillLevel=null, estimatedTons=null, readingDate=null | PASS | [PNG](evidencias/swagger/06_contenedor_alta_201.png) |
| D02 | POST /api/containers | Alta duplicada | Mismo CONT-011 | 409; ErrorDTO con message | 409; ErrorDTO.message: Ya existe el contenedor CONT-011. | PASS | [PNG](evidencias/swagger/07_contenedor_duplicado_409.png) |
| D03 | POST /api/containers | Capacidad cero | CONT-012; capacity=0 | 400; ErrorDTO con message | 400; ErrorDTO.message: Identificador, dirección, código postal o capacidad incorrectos. | PASS | [PNG](evidencias/swagger/27_alta_capacidad_cero_400.png) |
| D04 | POST /api/containers | Código postal de cuatro dígitos | CONT-012; postalCode=4800 | 400; ErrorDTO con message | 400; ErrorDTO.message: Identificador, dirección, código postal o capacidad incorrectos. | PASS | [PNG](evidencias/swagger/28_alta_cp_invalido_400.png) |
| D05 | POST /api/plants/{plantId}/assignments | Asignar contenedor sin lecturas | PLASSB; 2026-11-11; [CONT-011] | 409; sin lecturas | 409; ErrorDTO.message: El contenedor CONT-011 no tiene lecturas registradas. | PASS | [PNG](evidencias/swagger/08_contenedor_sin_lectura.png) |
| D05-S | GET /api/plants/{plantId}/assignments | Sin asignaciones tras D05 | PLASSB; sin filtro date | 200; listado sin cambios | 200; 0 asignaciones | PASS | [PNG](evidencias/swagger/29_sin_asignacion_tras_sin_lectura_200.png) |
| D05-C | GET /api/plants/{plantId}/capacity | Capacidad intacta tras D05 | PLASSB; 2026-11-11 | 200; capacidad sin cambios | 200; total=0.4; asignado=0; disponible=0.4 t | PASS | [PNG](evidencias/swagger/30_capacidad_tras_sin_lectura_200.png) |

## E — Sensor y conservación de estado

| ID | Endpoint | Escenario | Datos de entrada | Esperado | Obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| E01 | POST /api/containers/{containerId}/readings | Duplicado de lectura semilla de hoy | CONT-001; packageCount=500; fillLevel=ORANGE; sin token | 409; duplicado sin sobrescritura | 409; ErrorDTO.message: Ya existe una lectura para el contenedor CONT-001 en la fecha 2026-11-11. | PASS | [PNG](evidencias/swagger/31_sensor_duplicado_semilla_409.png) |
| E02 | GET /api/containers | Semilla conservada tras duplicado | Listado; comprobar CONT-001 | 200; CONT-001 = 400, GREEN, 0,012 t | 200; 11 contenedores; 11950 envases; CONT-001: 400/GREEN/0.012 t; CONT-002: 1200/ORANGE/0.036 t; CONT-003: 1450/RED/0.0435 t; CONT-004: 900/GREEN/0.027 t; CONT-005: 1500/ORANGE/0.045 t; CONT-006: 1900/RED/0.057 t; CONT-007: 700/GREEN/0.021 t; CONT-008: 1300/ORANGE/0.039 t; CONT-009: 1600/RED/0.048 t; CONT-010: 1000/ORANGE/0.03 t; CONT-011: null/null/null t | PASS | [PNG](evidencias/swagger/32_sensor_semilla_conservada_200.png) |
| E03 | POST /api/containers/{containerId}/readings | Primera lectura sin token | CONT-011; packageCount=125; fillLevel=GREEN | 201; 125, GREEN, fecha de hoy; sin token | 201; 2026-11-11; 125; GREEN | PASS | [PNG](evidencias/swagger/12_sensor_primera_lectura_201.png) |
| E04 | POST /api/containers/{containerId}/readings | Segunda lectura del mismo día | CONT-011; packageCount=140; fillLevel=GREEN | 409; duplicado sin sobrescritura | 409; ErrorDTO.message: Ya existe una lectura para el contenedor CONT-011 en la fecha 2026-11-11. | PASS | [PNG](evidencias/swagger/13_sensor_duplicado_409.png) |
| E05 | GET /api/containers | Primera lectura conservada | Listado; comprobar CONT-011 | 200; CONT-011 = 125, GREEN, 0,00375 t | 200; 11 contenedores; 12075 envases; CONT-001: 400/GREEN/0.012 t; CONT-002: 1200/ORANGE/0.036 t; CONT-003: 1450/RED/0.0435 t; CONT-004: 900/GREEN/0.027 t; CONT-005: 1500/ORANGE/0.045 t; CONT-006: 1900/RED/0.057 t; CONT-007: 700/GREEN/0.021 t; CONT-008: 1300/ORANGE/0.039 t; CONT-009: 1600/RED/0.048 t; CONT-010: 1000/ORANGE/0.03 t; CONT-011: 125/GREEN/0.00375 t | PASS | [PNG](evidencias/swagger/14_sensor_estado_conservado_200.png) · [PNG](evidencias/swagger/14b_sensor_historial_conservado_200.png) |
| E06 | POST /api/containers/{containerId}/readings | Cantidad superior a capacidad | CONT-011; packageCount=1001; GREEN | 400; ErrorDTO con message | 400; ErrorDTO.message: Cantidad de envases o nivel de llenado incorrecto. | PASS | [PNG](evidencias/swagger/33_sensor_exceso_capacidad_400.png) |
| E07 | POST /api/containers/{containerId}/readings | Cantidad nula | CONT-011; packageCount=null; GREEN | 400; ErrorDTO con message | 400; ErrorDTO.message: La cantidad de envases y el nivel de llenado son obligatorios. | PASS | [PNG](evidencias/swagger/34_sensor_cantidad_nula_400.png) |
| E08 | POST /api/containers/{containerId}/readings | Cantidad omitida | CONT-011; solo fillLevel=GREEN | 400; ErrorDTO con message | 400; ErrorDTO.message: La cantidad de envases y el nivel de llenado son obligatorios. | PASS | [PNG](evidencias/swagger/35_sensor_cantidad_omitida_400.png) |
| E09 | POST /api/containers/{containerId}/readings | Contenedor de sensor inexistente | CONT-999; 125; GREEN | 404; ErrorDTO con message | 404; ErrorDTO.message: No existe el contenedor CONT-999. | PASS | [PNG](evidencias/swagger/36_sensor_inexistente_404.png) |
| E05-H | GET /api/containers/{containerId}/readings | Historial conserva única lectura de CONT-011 | CONT-011; from=to=2026-11-11 | 200; una única lectura de hoy: 125, GREEN | 200; 1 lecturas; 2026-11-11: 125 GREEN | PASS | [PNG](evidencias/swagger/14b_sensor_historial_conservado_200.png) |

## F — Consulta por zona

| ID | Endpoint | Escenario | Datos de entrada | Esperado | Obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| F01 | GET /api/zones/{postalCode}/containers | Zona con CONT-011 ya leído | postalCode=48009; date=2026-11-11 | 200; CONT-001, CONT-002, CONT-011 | 200; CONT-001, CONT-002, CONT-011; CONT-001: 400; CONT-002: 1200; CONT-011: 125 | PASS | [PNG](evidencias/swagger/11_zona_200.png) |
| F02 | GET /api/zones/{postalCode}/containers | Zona sin contenedores | postalCode=99999; date=2026-11-11 | 200; lista vacía | 200; ;  | PASS | [PNG](evidencias/swagger/37_zona_vacia_200.png) |
| F03 | GET /api/zones/{postalCode}/containers | Fecha anterior al historial | postalCode=48009; date=2026-11-06 | 200; lista vacía | 200; ;  | PASS | [PNG](evidencias/swagger/38_zona_sin_historial_200.png) |
| F04 | GET /api/zones/{postalCode}/containers | Código postal inválido | postalCode=4800; date=2026-11-11 | 400; ErrorDTO con message | 400; ErrorDTO.message: Código postal o fecha incorrectos. | PASS | [PNG](evidencias/swagger/39_zona_cp_invalido_400.png) |

## G — Plantas y capacidad

| ID | Endpoint | Escenario | Datos de entrada | Esperado | Obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| G01 | GET /api/plants | Dos plantas simuladas | Sesión de Ana | 200; PLASSB y CONTSOCKET | 200; CONTSOCKET, PLASSB | PASS | [PNG](evidencias/swagger/15_plantas_listado_200.png) |
| G02 | GET /api/plants/{plantId}/capacity | Capacidad inicial antes de asignaciones | PLASSB; date=2026-11-11 | 200; total 0,40, asignado 0, disponible 0,40 t | 200; total=0.4; asignado=0; disponible=0.4 t | PASS | [PNG](evidencias/swagger/16_capacidad_inicial_200.png) |
| G03 | GET /api/plants/{plantId}/capacity | Planta inexistente | NO-PLANT; date=2026-11-11 | 404; ErrorDTO con message | 404; ErrorDTO.message: No existe la planta: NO-PLANT | PASS | [PNG](evidencias/swagger/40_planta_inexistente_404.png) |
| G04 | GET /api/plants/{plantId}/capacity | Fecha fuera de ventana por exceso | PLASSB; date=2026-11-21 | 400; ErrorDTO con message | 400; ErrorDTO.message: La fecha debe estar entre hoy y los próximos nueve días. | PASS | [PNG](evidencias/swagger/41_capacidad_fecha_fuera_400.png) |
| G05 | GET /api/plants/{plantId}/capacity | Fecha fuera de ventana por defecto | PLASSB; date=2026-11-10 | 400; ErrorDTO con message | 400; ErrorDTO.message: La fecha debe estar entre hoy y los próximos nueve días. | PASS | [PNG](evidencias/swagger/42_capacidad_fecha_pasada_400.png) |

## H — Asignaciones y atomicidad

| ID | Endpoint | Escenario | Datos de entrada | Esperado | Obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| H01 | POST /api/plants/{plantId}/assignments | Asignación correcta de Ana | PLASSB; 2026-11-11; [CONT-003,CONT-006,CONT-009] | 201; 3 contenedores, 4.950 envases, 0,1485 t, Ana y createdAt | 201; 3 contenedores; 4950 envases; 0.14850000000000002 t; assignedBy=ana.garcia@ecoembes.es; createdAt=2026-11-11T05:42:32.0280997 | PASS | [PNG](evidencias/swagger/17_asignacion_correcta_201.png) |
| H02 | GET /api/plants/{plantId}/capacity | Capacidad descontada tras H01 | PLASSB; 2026-11-11 | 200; asignado 0,1485, disponible 0,2515 t | 200; total=0.4; asignado=0.14850000000000002; disponible=0.2515 t | PASS | [PNG](evidencias/swagger/18_capacidad_actualizada_200.png) |
| H03 | GET /api/plants/{plantId}/assignments | Consultar asignación registrada | PLASSB; date=2026-11-11 | 200; asignación idéntica a H01 | 200; 1 asignaciones; id=1; 4950 envases; 0.14850000000000002 t | PASS | [PNG](evidencias/swagger/19_asignaciones_listado_200.png) |
| H04 | POST /api/plants/{plantId}/assignments | Duplicado entre plantas el mismo día | CONTSOCKET; 2026-11-11; [CONT-003] | 409; ErrorDTO con message | 409; ErrorDTO.message: El contenedor CONT-003 ya está asignado para la fecha 2026-11-11. | PASS | [PNG](evidencias/swagger/20_asignacion_duplicada_409.png) |
| H04-S | GET /api/plants/{plantId}/assignments | Sin asignación parcial tras H04 | CONTSOCKET; sin filtro date | 200; listado sin cambios | 200; 0 asignaciones | PASS | [PNG](evidencias/swagger/20_asignacion_duplicada_409_sin_asignacion_parcial.png) |
| H04-C | GET /api/plants/{plantId}/capacity | Sin descuento de capacidad tras H04 | CONTSOCKET; 2026-11-11 | 200; capacidad sin cambios | 200; total=0.3; asignado=0; disponible=0.3 t | PASS | [PNG](evidencias/swagger/20_asignacion_duplicada_409_capacidad_intacta.png) |
| H05 | POST /api/plants/{plantId}/assignments | Lista vacía | PLASSB; hoy; [] | 400; ErrorDTO con message | 400; ErrorDTO.message: Indica contenedores sin repetir identificadores. | PASS | [PNG](evidencias/swagger/43_asignacion_lista_vacia_400.png) |
| H05-S | GET /api/plants/{plantId}/assignments | Sin asignación parcial tras H05 | PLASSB; sin filtro date | 200; listado sin cambios | 200; 1 asignaciones; id=1; 4950 envases; 0.14850000000000002 t | PASS | [PNG](evidencias/swagger/43_asignacion_lista_vacia_400_sin_asignacion_parcial.png) |
| H05-C | GET /api/plants/{plantId}/capacity | Sin descuento de capacidad tras H05 | PLASSB; 2026-11-11 | 200; capacidad sin cambios | 200; total=0.4; asignado=0.14850000000000002; disponible=0.2515 t | PASS | [PNG](evidencias/swagger/43_asignacion_lista_vacia_400_capacidad_intacta.png) |
| H06 | POST /api/plants/{plantId}/assignments | Identificadores repetidos | PLASSB; hoy; [CONT-002,CONT-002] | 400; ErrorDTO con message | 400; ErrorDTO.message: Indica contenedores sin repetir identificadores. | PASS | [PNG](evidencias/swagger/44_asignacion_ids_repetidos_400.png) |
| H06-S | GET /api/plants/{plantId}/assignments | Sin asignación parcial tras H06 | PLASSB; sin filtro date | 200; listado sin cambios | 200; 1 asignaciones; id=1; 4950 envases; 0.14850000000000002 t | PASS | [PNG](evidencias/swagger/44_asignacion_ids_repetidos_400_sin_asignacion_parcial.png) |
| H06-C | GET /api/plants/{plantId}/capacity | Sin descuento de capacidad tras H06 | PLASSB; 2026-11-11 | 200; capacidad sin cambios | 200; total=0.4; asignado=0.14850000000000002; disponible=0.2515 t | PASS | [PNG](evidencias/swagger/44_asignacion_ids_repetidos_400_capacidad_intacta.png) |
| H07 | POST /api/plants/{plantId}/assignments | Selección con contenedor inexistente | PLASSB; hoy; [CONT-002,CONT-999] | 404; ErrorDTO con message | 404; ErrorDTO.message: No existe el contenedor CONT-999. | PASS | [PNG](evidencias/swagger/45_asignacion_contenedor_inexistente_404.png) |
| H07-S | GET /api/plants/{plantId}/assignments | Sin asignación parcial tras H07 | PLASSB; sin filtro date | 200; listado sin cambios | 200; 1 asignaciones; id=1; 4950 envases; 0.14850000000000002 t | PASS | [PNG](evidencias/swagger/45_asignacion_contenedor_inexistente_404_sin_asignacion_parcial.png) |
| H07-C | GET /api/plants/{plantId}/capacity | Sin descuento de capacidad tras H07 | PLASSB; 2026-11-11 | 200; capacidad sin cambios | 200; total=0.4; asignado=0.14850000000000002; disponible=0.2515 t | PASS | [PNG](evidencias/swagger/45_asignacion_contenedor_inexistente_404_capacidad_intacta.png) |
| H08 | POST /api/plants/{plantId}/assignments | Fecha de asignación nula | PLASSB; date=null; [CONT-002] | 400; ErrorDTO con message | 400; ErrorDTO.message: La fecha de asignación es obligatoria. | PASS | [PNG](evidencias/swagger/46_asignacion_fecha_nula_400.png) |
| H08-S | GET /api/plants/{plantId}/assignments | Sin asignación parcial tras H08 | PLASSB; sin filtro date | 200; listado sin cambios | 200; 1 asignaciones; id=1; 4950 envases; 0.14850000000000002 t | PASS | [PNG](evidencias/swagger/46_asignacion_fecha_nula_400_sin_asignacion_parcial.png) |
| H08-C | GET /api/plants/{plantId}/capacity | Sin descuento de capacidad tras H08 | PLASSB; 2026-11-11 | 200; capacidad sin cambios | 200; total=0.4; asignado=0.14850000000000002; disponible=0.2515 t | PASS | [PNG](evidencias/swagger/46_asignacion_fecha_nula_400_capacidad_intacta.png) |
| H09 | POST /api/plants/{plantId}/assignments | Fecha de asignación omitida | PLASSB; sin date; [CONT-002] | 400; ErrorDTO con message | 400; ErrorDTO.message: La fecha de asignación es obligatoria. | PASS | [PNG](evidencias/swagger/47_asignacion_fecha_omitida_400.png) |
| H09-S | GET /api/plants/{plantId}/assignments | Sin asignación parcial tras H09 | PLASSB; sin filtro date | 200; listado sin cambios | 200; 1 asignaciones; id=1; 4950 envases; 0.14850000000000002 t | PASS | [PNG](evidencias/swagger/47_asignacion_fecha_omitida_400_sin_asignacion_parcial.png) |
| H09-C | GET /api/plants/{plantId}/capacity | Sin descuento de capacidad tras H09 | PLASSB; 2026-11-11 | 200; capacidad sin cambios | 200; total=0.4; asignado=0.14850000000000002; disponible=0.2515 t | PASS | [PNG](evidencias/swagger/47_asignacion_fecha_omitida_400_capacidad_intacta.png) |
| H10 | POST /api/plants/{plantId}/assignments | Asignar a planta inexistente | NO-PLANT; hoy; [CONT-002] | 404; ErrorDTO con message | 404; ErrorDTO.message: No existe la planta: NO-PLANT | PASS | [PNG](evidencias/swagger/48_asignacion_planta_inexistente_404.png) |
| H10-S | GET /api/plants/{plantId}/assignments | Sin asignación parcial tras H10 | PLASSB; sin filtro date | 200; listado sin cambios | 200; 1 asignaciones; id=1; 4950 envases; 0.14850000000000002 t | PASS | [PNG](evidencias/swagger/48_asignacion_planta_inexistente_404_sin_asignacion_parcial.png) |
| H10-C | GET /api/plants/{plantId}/capacity | Sin descuento de capacidad tras H10 | PLASSB; 2026-11-11 | 200; capacidad sin cambios | 200; total=0.4; asignado=0.14850000000000002; disponible=0.2515 t | PASS | [PNG](evidencias/swagger/48_asignacion_planta_inexistente_404_capacidad_intacta.png) |
| H11 | POST /api/plants/{plantId}/assignments | Fecha hoy más diez días | PLASSB; 2026-11-21; [CONT-002] | 400; ErrorDTO con message | 400; ErrorDTO.message: La fecha debe estar entre hoy y los próximos nueve días. | PASS | [PNG](evidencias/swagger/22_asignacion_fecha_invalida_400.png) |
| H11-S | GET /api/plants/{plantId}/assignments | Sin asignación parcial tras H11 | PLASSB; sin filtro date | 200; listado sin cambios | 200; 1 asignaciones; id=1; 4950 envases; 0.14850000000000002 t | PASS | [PNG](evidencias/swagger/22_asignacion_fecha_invalida_400_sin_asignacion_parcial.png) |
| H11-C | GET /api/plants/{plantId}/capacity | Sin descuento de capacidad tras H11 | PLASSB; 2026-11-11 | 200; capacidad sin cambios | 200; total=0.4; asignado=0.14850000000000002; disponible=0.2515 t | PASS | [PNG](evidencias/swagger/22_asignacion_fecha_invalida_400_capacidad_intacta.png) |
| H12 | POST /api/plants/{plantId}/assignments | Capacidad cero de PLASSB | PLASSB; 2026-11-17; [CONT-002] | 409; ErrorDTO con message | 409; ErrorDTO.message: La planta no tiene capacidad suficiente para la asignación. | PASS | [PNG](evidencias/swagger/49_asignacion_capacidad_cero_409.png) |
| H12-S | GET /api/plants/{plantId}/assignments | Sin asignación parcial tras H12 | PLASSB; sin filtro date | 200; listado sin cambios | 200; 1 asignaciones; id=1; 4950 envases; 0.14850000000000002 t | PASS | [PNG](evidencias/swagger/49_asignacion_capacidad_cero_409_sin_asignacion_parcial.png) |
| H12-C | GET /api/plants/{plantId}/capacity | Sin descuento de capacidad tras H12 | PLASSB; 2026-11-17 | 200; capacidad sin cambios | 200; total=0; asignado=0; disponible=0 t | PASS | [PNG](evidencias/swagger/49_asignacion_capacidad_cero_409_capacidad_intacta.png) |
| H13 | POST /api/plants/{plantId}/assignments | Capacidad insuficiente para diez contenedores | CONTSOCKET; 2026-11-12; CONT-001…CONT-010; 0,3585 t > 0,30 t | 409; ErrorDTO con message | 409; ErrorDTO.message: La planta no tiene capacidad suficiente para la asignación. | PASS | [PNG](evidencias/swagger/21_capacidad_insuficiente_409.png) |
| H13-S | GET /api/plants/{plantId}/assignments | Sin asignación parcial tras H13 | CONTSOCKET; sin filtro date | 200; listado sin cambios | 200; 0 asignaciones | PASS | [PNG](evidencias/swagger/21_capacidad_insuficiente_409_sin_asignacion_parcial.png) |
| H13-C | GET /api/plants/{plantId}/capacity | Sin descuento de capacidad tras H13 | CONTSOCKET; 2026-11-12 | 200; capacidad sin cambios | 200; total=0.3; asignado=0; disponible=0.3 t | PASS | [PNG](evidencias/swagger/21_capacidad_insuficiente_409_capacidad_intacta.png) |

## I — Casos adicionales

| ID | Endpoint | Escenario | Datos de entrada | Esperado | Obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| I01 | GET /api/containers | Token mal formado | Token no numérico de prueba | 400; ErrorDTO con message | Sin petición HTTP: Swagger muestra «Value must be an integer» | BLOCKED | [PNG](evidencias/swagger/50_token_alfabetico_bloqueado_swagger.png) |
| I02 | GET /api/containers/{containerId}/readings | Fecha mal formada | CONT-001; from=11-11-2026; to=2026-11-11 | 400; ErrorDTO con message | 400; ErrorDTO.message: Los datos de la petición no tienen un formato válido | PASS | [PNG](evidencias/swagger/51_fecha_malformada_400.png) |
| I03 | GET /api/containers | Token entero fuera del rango long | Entero de 25 dígitos fuera del rango int64 | 400; ErrorDTO con message | 400; ErrorDTO.message: Los datos de la petición no tienen un formato válido | PASS | [PNG](evidencias/swagger/52_token_fuera_rango_400.png) |
| I04 | POST /api/containers/{containerId}/readings | Nivel de llenado desconocido | CONT-011; 125; fillLevel=BLUE | 400; ErrorDTO con message | 400; ErrorDTO.message: Los datos de la petición no tienen un formato válido | PASS | [PNG](evidencias/swagger/53_sensor_enum_invalido_400.png) |
| I05 | POST /api/containers/{containerId}/readings | Nivel de llenado nulo | CONT-011; 125; fillLevel=null | 400; ErrorDTO con message | 400; ErrorDTO.message: La cantidad de envases y el nivel de llenado son obligatorios. | PASS | [PNG](evidencias/swagger/54_sensor_nivel_nulo_400.png) |
| I06 | GET /api/plants/{plantId}/capacity | Extremo válido hoy más nueve días | CONTSOCKET; 2026-11-20 | 200; total 0,30, asignado 0 | 200; total=0.3; asignado=0; disponible=0.3 t | PASS | [PNG](evidencias/swagger/55_capacidad_limite_200.png) |
| I07 | GET /api/plants/{plantId}/assignments | Estado final de CONTSOCKET sin asignaciones | CONTSOCKET; sin filtro date | 200; lista vacía | 200; 0 asignaciones | PASS | [PNG](evidencias/swagger/56_asignaciones_otra_planta_vacias_200.png) |
| I08 | GET /api/plants/{plantId}/assignments | Estado final PLASSB con única asignación | PLASSB; sin filtro date | 200; única asignación H01 | 200; 1 asignaciones; id=1; 4950 envases; 0.14850000000000002 t | PASS | [PNG](evidencias/swagger/57_asignaciones_estado_final_200.png) |

## S — Repetición sobre la versión final

| ID | Endpoint | Escenario | Datos de entrada | Esperado | Obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| S01 | POST /api/auth/login | Login en versión final tras reinicio | Ana; contraseña omitida | 200 | 200; token numérico recibido (oculto) | PASS | [PNG](evidencias/swagger/58_final_login_200.png) |
| S02 | GET /api/containers | Semilla restaurada en versión final | Sesión de Ana; diez contenedores | 200; semilla restaurada: 10 contenedores y 11.950 envases | 200; 10 contenedores; 11950 envases; CONT-001: 400/GREEN/0.012 t; CONT-002: 1200/ORANGE/0.036 t; CONT-003: 1450/RED/0.0435 t; CONT-004: 900/GREEN/0.027 t; CONT-005: 1500/ORANGE/0.045 t; CONT-006: 1900/RED/0.057 t; CONT-007: 700/GREEN/0.021 t; CONT-008: 1300/ORANGE/0.039 t; CONT-009: 1600/RED/0.048 t; CONT-010: 1000/ORANGE/0.03 t | PASS | [PNG](evidencias/swagger/59_final_listado_semilla_200.png) |
| S03 | GET /api/containers/{containerId}/readings | Historial en versión final | CONT-001; 2026-11-07…2026-11-11 | 200; 5 lecturas: 80,160,240,320,400 | 200; 5 lecturas; 2026-11-07: 80 GREEN; 2026-11-08: 160 GREEN; 2026-11-09: 240 GREEN; 2026-11-10: 320 GREEN; 2026-11-11: 400 GREEN | PASS | [PNG](evidencias/swagger/60_final_historial_200.png) |
| S04 | POST /api/containers | Alta con nivel nulo en versión final | {"id":"CONT-011","address":"Calle de Prueba 11, Bilbao","postalCode":"48009","capacity":1000} | 201; cuatro campos de lectura null | 201; CONT-011; packageCount=null, fillLevel=null, estimatedTons=null, readingDate=null | PASS | [PNG](evidencias/swagger/61_final_alta_nullable_201.png) |
| S05 | POST /api/containers/{containerId}/readings | Sensor en versión final sin token | CONT-011; 125; GREEN | 201; 125, GREEN, sin token | 201; 2026-11-11; 125; GREEN | PASS | [PNG](evidencias/swagger/62_final_sensor_201.png) |
| S06 | GET /api/zones/{postalCode}/containers | Zona en versión final | 48009; 2026-11-11 | 200; tres contenedores de zona 48009 | 200; CONT-001, CONT-002, CONT-011; CONT-001: 400; CONT-002: 1200; CONT-011: 125 | PASS | [PNG](evidencias/swagger/63_final_zona_200.png) |
| S07 | GET /api/plants | Plantas en versión final | Sesión de Ana | 200; dos plantas | 200; CONTSOCKET, PLASSB | PASS | [PNG](evidencias/swagger/64_final_plantas_200.png) |
| S08 | GET /api/plants/{plantId}/capacity | Capacidad inicial tras reinicio final | PLASSB; 2026-11-11 | 200; disponible 0,40, asignado 0 | 200; total=0.4; asignado=0; disponible=0.4 t | PASS | [PNG](evidencias/swagger/65_final_capacidad_200.png) |
| S09 | POST /api/plants/{plantId}/assignments | Asignación en versión final | PLASSB; hoy; [CONT-003,CONT-006,CONT-009] | 201; 3 contenedores, 4.950 envases, 0,1485 t, Ana y createdAt | 201; 3 contenedores; 4950 envases; 0.14850000000000002 t; assignedBy=ana.garcia@ecoembes.es; createdAt=2026-11-11T05:49:16.702523 | PASS | [PNG](evidencias/swagger/66_final_asignacion_201.png) |
| S10 | GET /api/plants/{plantId}/assignments | Consultar asignación en versión final | PLASSB; date=2026-11-11 | 200; una asignación idéntica a S09 | 200; 1 asignaciones; id=1; 4950 envases; 0.14850000000000002 t | PASS | [PNG](evidencias/swagger/67_final_consulta_asignacion_200.png) |
| S11 | POST /api/auth/logout | Logout en versión final | Sesión activa final de Ana; token omitido | 204; sin cuerpo | 204; cuerpo vacío | PASS | [PNG](evidencias/swagger/68_final_logout_204.png) |
| S12 | GET /api/containers | Token final invalidado | Misma sesión después de S11; token omitido | 401; token invalidado | 401; ErrorDTO.message: El token no está activo. | PASS | [PNG](evidencias/swagger/69_final_token_invalidado_401.png) |
