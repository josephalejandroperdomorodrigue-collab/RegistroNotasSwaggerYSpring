# Lista de estudiantes

Aplicacion de Spring Boot para registrar estudiantes desde un cuestionario en
la terminal. Tambien incluye Swagger para probar la API.

## Requisito

- JDK 25

No necesita MySQL ni otra base de datos. Los estudiantes se guardan en memoria
mientras la aplicacion esta abierta; al detenerla, se borran.

## Ejecutar en Windows

Abre Git Bash en la carpeta del proyecto y ejecuta:

```bash
./mvnw test
./mvnw spring-boot:run
```

Responde las preguntas que aparecen en la terminal. Para las preguntas de
comportamiento escribe `s` o `n`; para las notas, usa numeros separados por
comas, por ejemplo `4.2, 4.8`. El cuestionario aparece inmediatamente al
arrancar; no tienes que presionar Enter para iniciarlo.

Al terminar, muestra cuántos estudiantes se registraron, sus nombres, notas,
promedio y promedio ajustado. El promedio ajustado resta 0.2 por comelón que no
comparte su comida, resta 0.5 por cansón y suma 0.3 por ayudador. Dormilón se
menciona en el resumen, pero no cambia la nota. El resultado se limita al rango
de 0 a 5.

Swagger tambien esta disponible en
<http://localhost:8080/swagger-ui.html>. Para parar la aplicacion, presiona
`Ctrl+C`.

## Operaciones de la API

| Metodo | Ruta | Accion |
|---|---|---|
| POST | `/api/estudiantes` | CREATE: registrar un estudiante |
| GET | `/api/estudiantes` | READ: ver todos los estudiantes |
| GET | `/api/estudiantes/{id}` | READ: ver un estudiante |
| PUT | `/api/estudiantes/{id}` | UPDATE: actualizar un estudiante |
| DELETE | `/api/estudiantes/{id}` | DELETE: eliminar un estudiante |

El docente ingresa el `ID` del estudiante; el sistema asigna un ID interno para
consultar, actualizar o eliminar el registro. Las notas deben estar entre 0 y 5.
