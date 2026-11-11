# Flujo de trabajo con Git

El repositorio mantiene una rama estable (`main`) y una rama de trabajo por integrante (`dev-<usuario>`). Los cambios se incorporan a `main` mediante **pull requests**, después de comprobar que el proyecto compila y supera las pruebas.

## Ramas

- `main`: versión estable e integrada del proyecto.
- `dev-aritz`, `dev-iker`, `dev-oier`, `dev-ander`: ramas personales para desarrollar y registrar cambios.

No se utiliza una rama intermedia `develop`. Cada integrante trabaja con su propia identidad y el historial refleja las contribuciones reales.

## Crear una rama personal (una sola vez)

En la raíz del repositorio, partiendo del primer commit en `main`:

```powershell
git status
git remote -v
git switch -c dev-aritz
git push -u origin dev-aritz
```

Sustituir `dev-aritz` por el nombre de la rama de cada integrante. Si la rama ya existe localmente, usar `git switch dev-aritz` en lugar de `git switch -c ...`.

`git switch -c` conserva normalmente los cambios locales pendientes cuando no hay un conflicto al cambiar de rama. Verificar siempre `git status` antes de continuar.

## Ciclo de trabajo

1. Trabajar en la rama personal y revisar los cambios con `git status` y `git diff`.
2. Antes de registrar un cambio, ejecutar:

   ```powershell
   .\gradlew.bat clean build
   ```

   `build` ejecuta también la tarea de pruebas `test`. Los informes se generan en `build/reports/tests/test/index.html`.

3. Revisar los archivos que se van a incluir:

   ```powershell
   git add .
   git diff --cached --check
   git status
   ```

4. Crear el commit con un mensaje descriptivo y subir la rama:

   ```powershell
   git commit -m "feat(base): configurar OpenAPI y gestion global de errores"
   git push
   ```

5. Abrir en GitHub una **Pull Request** con base `main` y compare `dev-aritz`. Revisar los cambios y realizar el merge cuando estén comprobados.

Los commits posteriores de la misma rama se registran normalmente; **no es necesario crear una rama nueva para cada commit**.

## Sincronización después de un merge

Cuando `main` recibe cambios:

```powershell
git fetch origin
git switch dev-aritz
git merge origin/main
```

Resolver con cuidado cualquier conflicto de integración antes de crear otro commit. No forzar cambios en ramas compartidas (`git push --force`).

## Convenciones de commits

Se recomiendan mensajes claros con prefijos como `feat`, `fix`, `test`, `docs` y `chore`.

Ejemplos:

- `feat(auth): implementar inicio de sesion`
- `feat(containers): registrar lecturas del sensor`
- `test(api): verificar respuestas de error`
- `docs(uml): actualizar diagramas de secuencia`
