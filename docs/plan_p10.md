# Plan de ejecución P10

Contrato: encargo P10 del 11-11-2026; contrato v1.5 no localizado en la carpeta Ecoembes.

1. Verificar PR #7 y crear rama desde origin/main; ejecutar clean build.
2. Corregir únicamente documentación OpenAPI en EcoembesApplication, EcoembesFacade y DTO: ErrorDTO para errores, parámetros y ejemplos, nulabilidad y alcance P1.
3. Recompilar, arrancar bootRun y comprobar once operaciones y esquemas en /v3/api-docs.
4. Ejecutar por Swagger A–H en orden, capturar resultados reales y ocultar credenciales; comprobar invariantes tras rechazos.
5. Guardar matriz, índice e informe con recuentos y límites observados; revisar Git y crear commits sin fusionar main.

No se cambia arquitectura, persistencia, rutas ni comportamiento funcional. Las anotaciones se verifican en OpenAPI generado y Swagger; los 61 tests existentes se vuelven a ejecutar.
