# Spring Boot : Application PetClinic

## Features  

### 1.  Unit Test  --> Tag v1.0.0
### 2.  Integration Test  --> Tag v2.0.0
mvn clean test -Dspring.profiles.active=h2

---

## Lab 12 — Pruebas de Integración (capa web)

Ejecutar todas las pruebas:

```bash
./mvnw clean test -Dspring.profiles.active=h2
```

Cada entidad tiene dos clases de prueba en `src/test/java/com/tecsup/petclinic/webs/`:

- `XxxControllerTest`: integración real con `@SpringBootTest` + `MockMvc`. La petición pasa por controlador → servicio → repositorio → H2 y usa los datos de `schema.sql` / `data.sql`. Cada prueba es `@Transactional`, así que se revierte al terminar y no altera los datos semilla.
- `XxxControllerMockitoTest`: mismo controlador con el servicio reemplazado por `@MockitoBean`. Solo valida la lógica del controlador y el contrato JSON. Los objetos esperados se crean en `TObjectCreator`.

| Ejercicio | Rama | Endpoints | Integración | Mockito |
|---|---|---|---|---|
| 1. Vet | `feature/vet` | `/vets`, `/vets/{id}`, `/vets?active=`, `/vets/{id}/deactivate`, `/vets/{id}/activate` | 10 | 10 |
| 2. Owner | `feature/owner` | `/owners`, `/owners/{id}`, `/owners?lastName=` (valida `lastName` → 400) | 9 | 9 |
| 3. Specialty | `feature/specialty` | `/specialties`, `?name=`, `?office=` (valida horario `openHour < closeHour`, rango 0–23 → 400) | 10 | 10 |
| 4. Visit | `feature/visit` | `/visits`, `?petId=`, `?vetId=`, `?from=&to=`, `/visits/pet/{id}/total-cost`, `/visits/pet/{id}/count` | 14 | 12 |
| 5. Type | `feature/type` | `/types`, `?active=`, `/types/report/pet-count` (excluye tipos inactivos) | 9 | 10 |
| 6. VetSpecialty | `feature/vet-specialty` | `/vet-specialties`, `/vets/{id}/specialties`, `/specialties/{id}/vets?minExperience=`, `.../primary`, `.../experience`, `.../certification` (duplicado → 409) | 14 | 12 |

Total: **150 pruebas** (25 originales de Pet + 125 nuevas), todas en verde.

### Flujo Git

- Cada ejercicio se desarrolló en su rama `feature/...` y se integró a `main` con `git merge --no-ff`.
- **Conflicto resuelto:** `feature/visit` y `feature/type` salieron del mismo commit de `main` y ambas agregaron imports y un bloque de datos al final de `TObjectCreator.java`. Al fusionar `feature/type` después de `feature/visit`, Git marcó el conflicto. Se resolvió conservando ambos bloques (VISIT + TYPE) y ordenando los imports (commit `Merge branch 'feature/type'`).
