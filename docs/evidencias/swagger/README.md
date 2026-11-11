# Evidencias auténticas de Swagger — P10

Capturas del navegador Chrome local sobre http://localhost:8080/swagger-ui/index.html. **91 PNG**, obtenidos mediante Computer Use el 11-11-2026. No se han reconstruido interfaces ni respuestas.

Se capturó la página real y se recortó la operación hasta su respuesta. La conversión JPEG→PNG es sin cambios de contenido. Los rectángulos negros rotulados TOKEN OCULTO, CONTRASEÑA OCULTA o CREDENCIALES OCULTAS ocultan únicamente campos o paneles que contienen secretos. No se conserva ninguna imagen original sin ocultación. En los listados largos, el panel de Swagger limita la altura visible: consultar también [resultados_swagger.json](resultados_swagger.json), extraído de las respuestas reales de la interfaz.

La numeración se amplía con sufijos para las verificaciones de atomicidad. Los nombres no determinan el orden temporal: lo determina la [matriz](../../matriz_pruebas_swagger.md) y el registro JSON. La vista general no constituye una petición HTTP.

| Imagen | Caso | Qué demuestra |
|---|---|---|
| [00_swagger_general.png](00_swagger_general.png) | General | Descripción general, once operaciones y esquemas publicados |
| [01_login_200.png](01_login_200.png) | A01 | Login correcto de Ana; 200 |
| [02_login_401.png](02_login_401.png) | A02 | Contraseña incorrecta; 401 |
| [03_logout_204.png](03_logout_204.png) | A05 | Logout correcto; 204 |
| [04_token_invalidado_401.png](04_token_invalidado_401.png) | A06 | Reutilizar token invalidado; 401 |
| [05_contenedores_listado_200.png](05_contenedores_listado_200.png) | B01 | Listado y datos semilla; 200 |
| [06_contenedor_alta_201.png](06_contenedor_alta_201.png) | D01 | Alta sin lecturas; 201 |
| [07_contenedor_duplicado_409.png](07_contenedor_duplicado_409.png) | D02 | Alta duplicada; 409 |
| [08_contenedor_sin_lectura.png](08_contenedor_sin_lectura.png) | D05 | Asignar contenedor sin lecturas; 409 |
| [09_historial_200.png](09_historial_200.png) | C01 | Historial inclusivo de cinco días; 200 |
| [10_historial_fecha_invalida_400.png](10_historial_fecha_invalida_400.png) | C02 | Intervalo invertido; 400 |
| [11_zona_200.png](11_zona_200.png) | F01 | Zona con CONT-011 ya leído; 200 |
| [12_sensor_primera_lectura_201.png](12_sensor_primera_lectura_201.png) | E03 | Primera lectura sin token; 201 |
| [13_sensor_duplicado_409.png](13_sensor_duplicado_409.png) | E04 | Segunda lectura del mismo día; 409 |
| [14_sensor_estado_conservado_200.png](14_sensor_estado_conservado_200.png) | E05 | Primera lectura conservada; 200 |
| [14b_sensor_historial_conservado_200.png](14b_sensor_historial_conservado_200.png) | E05, E05-H | Primera lectura conservada; 200 |
| [15_plantas_listado_200.png](15_plantas_listado_200.png) | G01 | Dos plantas simuladas; 200 |
| [16_capacidad_inicial_200.png](16_capacidad_inicial_200.png) | G02 | Capacidad inicial antes de asignaciones; 200 |
| [17_asignacion_correcta_201.png](17_asignacion_correcta_201.png) | H01 | Asignación correcta de Ana; 201 |
| [18_capacidad_actualizada_200.png](18_capacidad_actualizada_200.png) | H02 | Capacidad descontada tras H01; 200 |
| [19_asignaciones_listado_200.png](19_asignaciones_listado_200.png) | H03 | Consultar asignación registrada; 200 |
| [20_asignacion_duplicada_409.png](20_asignacion_duplicada_409.png) | H04 | Duplicado entre plantas el mismo día; 409 |
| [20_asignacion_duplicada_409_capacidad_intacta.png](20_asignacion_duplicada_409_capacidad_intacta.png) | H04-C | Sin descuento de capacidad tras H04; 200 |
| [20_asignacion_duplicada_409_sin_asignacion_parcial.png](20_asignacion_duplicada_409_sin_asignacion_parcial.png) | H04-S | Sin asignación parcial tras H04; 200 |
| [21_capacidad_insuficiente_409.png](21_capacidad_insuficiente_409.png) | H13 | Capacidad insuficiente para diez contenedores; 409 |
| [21_capacidad_insuficiente_409_capacidad_intacta.png](21_capacidad_insuficiente_409_capacidad_intacta.png) | H13-C | Sin descuento de capacidad tras H13; 200 |
| [21_capacidad_insuficiente_409_sin_asignacion_parcial.png](21_capacidad_insuficiente_409_sin_asignacion_parcial.png) | H13-S | Sin asignación parcial tras H13; 200 |
| [22_asignacion_fecha_invalida_400.png](22_asignacion_fecha_invalida_400.png) | H11 | Fecha hoy más diez días; 400 |
| [22_asignacion_fecha_invalida_400_capacidad_intacta.png](22_asignacion_fecha_invalida_400_capacidad_intacta.png) | H11-C | Sin descuento de capacidad tras H11; 200 |
| [22_asignacion_fecha_invalida_400_sin_asignacion_parcial.png](22_asignacion_fecha_invalida_400_sin_asignacion_parcial.png) | H11-S | Sin asignación parcial tras H11; 200 |
| [23_login_email_vacio_400.png](23_login_email_vacio_400.png) | A03 | Email vacío; 400 |
| [24_token_inventado_401.png](24_token_inventado_401.png) | A04 | Token numérico inventado; 401 |
| [25_nuevo_login_200.png](25_nuevo_login_200.png) | A07 | Nuevo login para continuar; 200 |
| [26_historial_inexistente_404.png](26_historial_inexistente_404.png) | C03 | Contenedor inexistente; 404 |
| [27_alta_capacidad_cero_400.png](27_alta_capacidad_cero_400.png) | D03 | Capacidad cero; 400 |
| [28_alta_cp_invalido_400.png](28_alta_cp_invalido_400.png) | D04 | Código postal de cuatro dígitos; 400 |
| [29_sin_asignacion_tras_sin_lectura_200.png](29_sin_asignacion_tras_sin_lectura_200.png) | D05-S | Sin asignaciones tras D05; 200 |
| [30_capacidad_tras_sin_lectura_200.png](30_capacidad_tras_sin_lectura_200.png) | D05-C | Capacidad intacta tras D05; 200 |
| [31_sensor_duplicado_semilla_409.png](31_sensor_duplicado_semilla_409.png) | E01 | Duplicado de lectura semilla de hoy; 409 |
| [32_sensor_semilla_conservada_200.png](32_sensor_semilla_conservada_200.png) | E02 | Semilla conservada tras duplicado; 200 |
| [33_sensor_exceso_capacidad_400.png](33_sensor_exceso_capacidad_400.png) | E06 | Cantidad superior a capacidad; 400 |
| [34_sensor_cantidad_nula_400.png](34_sensor_cantidad_nula_400.png) | E07 | Cantidad nula; 400 |
| [35_sensor_cantidad_omitida_400.png](35_sensor_cantidad_omitida_400.png) | E08 | Cantidad omitida; 400 |
| [36_sensor_inexistente_404.png](36_sensor_inexistente_404.png) | E09 | Contenedor de sensor inexistente; 404 |
| [37_zona_vacia_200.png](37_zona_vacia_200.png) | F02 | Zona sin contenedores; 200 |
| [38_zona_sin_historial_200.png](38_zona_sin_historial_200.png) | F03 | Fecha anterior al historial; 200 |
| [39_zona_cp_invalido_400.png](39_zona_cp_invalido_400.png) | F04 | Código postal inválido; 400 |
| [40_planta_inexistente_404.png](40_planta_inexistente_404.png) | G03 | Planta inexistente; 404 |
| [41_capacidad_fecha_fuera_400.png](41_capacidad_fecha_fuera_400.png) | G04 | Fecha fuera de ventana por exceso; 400 |
| [42_capacidad_fecha_pasada_400.png](42_capacidad_fecha_pasada_400.png) | G05 | Fecha fuera de ventana por defecto; 400 |
| [43_asignacion_lista_vacia_400.png](43_asignacion_lista_vacia_400.png) | H05 | Lista vacía; 400 |
| [43_asignacion_lista_vacia_400_capacidad_intacta.png](43_asignacion_lista_vacia_400_capacidad_intacta.png) | H05-C | Sin descuento de capacidad tras H05; 200 |
| [43_asignacion_lista_vacia_400_sin_asignacion_parcial.png](43_asignacion_lista_vacia_400_sin_asignacion_parcial.png) | H05-S | Sin asignación parcial tras H05; 200 |
| [44_asignacion_ids_repetidos_400.png](44_asignacion_ids_repetidos_400.png) | H06 | Identificadores repetidos; 400 |
| [44_asignacion_ids_repetidos_400_capacidad_intacta.png](44_asignacion_ids_repetidos_400_capacidad_intacta.png) | H06-C | Sin descuento de capacidad tras H06; 200 |
| [44_asignacion_ids_repetidos_400_sin_asignacion_parcial.png](44_asignacion_ids_repetidos_400_sin_asignacion_parcial.png) | H06-S | Sin asignación parcial tras H06; 200 |
| [45_asignacion_contenedor_inexistente_404.png](45_asignacion_contenedor_inexistente_404.png) | H07 | Selección con contenedor inexistente; 404 |
| [45_asignacion_contenedor_inexistente_404_capacidad_intacta.png](45_asignacion_contenedor_inexistente_404_capacidad_intacta.png) | H07-C | Sin descuento de capacidad tras H07; 200 |
| [45_asignacion_contenedor_inexistente_404_sin_asignacion_parcial.png](45_asignacion_contenedor_inexistente_404_sin_asignacion_parcial.png) | H07-S | Sin asignación parcial tras H07; 200 |
| [46_asignacion_fecha_nula_400.png](46_asignacion_fecha_nula_400.png) | H08 | Fecha de asignación nula; 400 |
| [46_asignacion_fecha_nula_400_capacidad_intacta.png](46_asignacion_fecha_nula_400_capacidad_intacta.png) | H08-C | Sin descuento de capacidad tras H08; 200 |
| [46_asignacion_fecha_nula_400_sin_asignacion_parcial.png](46_asignacion_fecha_nula_400_sin_asignacion_parcial.png) | H08-S | Sin asignación parcial tras H08; 200 |
| [47_asignacion_fecha_omitida_400.png](47_asignacion_fecha_omitida_400.png) | H09 | Fecha de asignación omitida; 400 |
| [47_asignacion_fecha_omitida_400_capacidad_intacta.png](47_asignacion_fecha_omitida_400_capacidad_intacta.png) | H09-C | Sin descuento de capacidad tras H09; 200 |
| [47_asignacion_fecha_omitida_400_sin_asignacion_parcial.png](47_asignacion_fecha_omitida_400_sin_asignacion_parcial.png) | H09-S | Sin asignación parcial tras H09; 200 |
| [48_asignacion_planta_inexistente_404.png](48_asignacion_planta_inexistente_404.png) | H10 | Asignar a planta inexistente; 404 |
| [48_asignacion_planta_inexistente_404_capacidad_intacta.png](48_asignacion_planta_inexistente_404_capacidad_intacta.png) | H10-C | Sin descuento de capacidad tras H10; 200 |
| [48_asignacion_planta_inexistente_404_sin_asignacion_parcial.png](48_asignacion_planta_inexistente_404_sin_asignacion_parcial.png) | H10-S | Sin asignación parcial tras H10; 200 |
| [49_asignacion_capacidad_cero_409.png](49_asignacion_capacidad_cero_409.png) | H12 | Capacidad cero de PLASSB; 409 |
| [49_asignacion_capacidad_cero_409_capacidad_intacta.png](49_asignacion_capacidad_cero_409_capacidad_intacta.png) | H12-C | Sin descuento de capacidad tras H12; 200 |
| [49_asignacion_capacidad_cero_409_sin_asignacion_parcial.png](49_asignacion_capacidad_cero_409_sin_asignacion_parcial.png) | H12-S | Sin asignación parcial tras H12; 200 |
| [50_token_alfabetico_bloqueado_swagger.png](50_token_alfabetico_bloqueado_swagger.png) | I01 | Token mal formado; bloqueo local de Swagger, sin HTTP |
| [51_fecha_malformada_400.png](51_fecha_malformada_400.png) | I02 | Fecha mal formada; 400 |
| [52_token_fuera_rango_400.png](52_token_fuera_rango_400.png) | I03 | Token entero fuera del rango long; 400 |
| [53_sensor_enum_invalido_400.png](53_sensor_enum_invalido_400.png) | I04 | Nivel de llenado desconocido; 400 |
| [54_sensor_nivel_nulo_400.png](54_sensor_nivel_nulo_400.png) | I05 | Nivel de llenado nulo; 400 |
| [55_capacidad_limite_200.png](55_capacidad_limite_200.png) | I06 | Extremo válido hoy más nueve días; 200 |
| [56_asignaciones_otra_planta_vacias_200.png](56_asignaciones_otra_planta_vacias_200.png) | I07 | Estado final de CONTSOCKET sin asignaciones; 200 |
| [57_asignaciones_estado_final_200.png](57_asignaciones_estado_final_200.png) | I08 | Estado final PLASSB con única asignación; 200 |
| [58_final_login_200.png](58_final_login_200.png) | S01 | Login en versión final tras reinicio; 200 |
| [59_final_listado_semilla_200.png](59_final_listado_semilla_200.png) | S02 | Semilla restaurada en versión final; 200 |
| [60_final_historial_200.png](60_final_historial_200.png) | S03 | Historial en versión final; 200 |
| [61_final_alta_nullable_201.png](61_final_alta_nullable_201.png) | S04 | Alta con nivel nulo en versión final; 201 |
| [62_final_sensor_201.png](62_final_sensor_201.png) | S05 | Sensor en versión final sin token; 201 |
| [63_final_zona_200.png](63_final_zona_200.png) | S06 | Zona en versión final; 200 |
| [64_final_plantas_200.png](64_final_plantas_200.png) | S07 | Plantas en versión final; 200 |
| [65_final_capacidad_200.png](65_final_capacidad_200.png) | S08 | Capacidad inicial tras reinicio final; 200 |
| [66_final_asignacion_201.png](66_final_asignacion_201.png) | S09 | Asignación en versión final; 201 |
| [67_final_consulta_asignacion_200.png](67_final_consulta_asignacion_200.png) | S10 | Consultar asignación en versión final; 200 |
| [68_final_logout_204.png](68_final_logout_204.png) | S11 | Logout en versión final; 204 |
| [69_final_token_invalidado_401.png](69_final_token_invalidado_401.png) | S12 | Token final invalidado; 401 |

## Archivos complementarios

- [resultados_swagger.json](resultados_swagger.json): entradas resumidas, cuerpos realmente leídos, resultado y commit de cada caso; tokens y contraseñas omitidos.
- [openapi-verificado.json](../openapi-verificado.json): especificación generada por el servidor final; no contiene sesiones.
- [servidor-arranque-y-notificaciones.txt](../servidor-arranque-y-notificaciones.txt): prueba complementaria de arranque y notificación simulada; no es una captura Swagger.
- [resumen-tests.json](../resumen-tests.json): recuento de tests Gradle, separado de los casos Swagger.

- [integridad_capturas.json](integridad_capturas.json): dimensiones, tamaño y SHA-256 de cada PNG para verificar que no cambia tras la entrega.
- [entorno.txt](../entorno.txt): versiones y proceso del servidor comprobado.
- [auditoria-openapi.json](../auditoria-openapi.json): verificación complementaria de la especificación generada.
