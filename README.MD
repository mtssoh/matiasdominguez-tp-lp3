# Trabajo Practico LP3 - Matias Dominguez

## Descripción General
El sistema gestiona una jerarquía de empleados utilizando Programación Orientada a Objetos con Spring Boot y REST.

Tipos de empleados:
- **EmpleadoTiempoCompleto**
- **EmpleadoPorHoras**
- **Contratista**
- **Gerente** (extiende EmpleadoTiempoCompleto e implementa gestión de días)

Se aplican conceptos de herencia, interfaces, polimorfismo, excepciones checked/runtime, composición de servicios, mappers y utilidades.

---

## Arquitectura de Paquetes
- `domain/` → Entidades JPA, herencia (`Persona`, `EmpleadoTiempoCompleto`, `EmpleadoPorHoras`, `Contratista`, `Gerente`) y contratos (`GestionDias`, `GestionDiasGerente`).
- `repository/` → Repositorios JPA (incluye `GerenteRepository`, `PersonaRepository`, etc.).
- `service/` → Lógica de negocio (`EmpleadoTiempoCompletoService`, `PersonaService`, `GestionDiasService`).
- `controller/` → Controladores REST que heredan de `BaseController`.
- `dto/` → Clases de transferencia de datos (incluye `NominaConDiasResponseDto`, `EmpleadoDiasReporteDto`, `GerenteResponseDto`, `ErrorResponseDto`, etc.).
- `Exception/` → Excepciones personalizadas (`DiasInsuficientesException` como checked, `EmpleadoNoEncontradoException` como runtime) y `GlobalExceptionHandler`.
- `util/` → Utilidades, por ejemplo `NominaUtils` para cálculo de días y generación de reportes.
- `mappers/` → Jerarquía de mappers (`BaseMapper`, `GerenteMapper`) para convertir entidades a DTO.

---

## Entidades y Gestión de Días

### Persona (abstracta)
- Métodos abstractos:
  - `calcularSalario()`
  - `calcularDeducciones()`
  - `validarDatosEspecificos()`
- Métodos concretos:
  - `calcularImpuestoBase()` (10%)
  - `calcularImpuestos()` (template method)
  - `obtenerInformacionCompleta()`

### EmpleadoTiempoCompleto
- Campos: `departamento`, `salarioMensual`.
- Deducción: 5% si es “IT”, 3% en otros.
- Validaciones: salario > 0 y departamento no vacío.

### EmpleadoPorHoras
- Campos: `tarifaPorHora`, `horasTrabajadas`.
- Salario = tarifa × horas (+50% bonus si >40h).
- Deducción: 2%.
- Validaciones: tarifa > 0, horas entre 1–80.

### Contratista
- Campos: `montoPorProyecto`, `proyectosCompletados`, `fechaFinContrato`.
- Sin deducciones.
- Validaciones: fecha futura y proyectos ≥ 0.
- Método adicional: `contratoVigente()`.

### Gerente
- Extiende `EmpleadoTiempoCompleto` e implementa `GestionDiasGerente`.
- Campos adicionales: `diasVacacionesDisponibles`, `diasPermisosDisponibles`, `diasSolicitadosEsteAnio`.
- Regla de negocio: solo los gerentes pueden solicitar más de 20 días al año y se controla un `limiteAnualGerente`.

---

## Excepciones y Manejo Global

- **DiasInsuficientesException** (checked): se lanza cuando un empleado/gerente no tiene días suficientes o no cumple las reglas de negocio.
- **EmpleadoNoEncontradoException** (runtime): se lanza cuando no existe el empleado solicitado.
- **GlobalExceptionHandler** (`@ControllerAdvice`): convierte las excepciones a `ErrorResponseDto { status, mensaje }` con códigos HTTP apropiados.

---

## NominaUtils y Reportes de Días

La clase `NominaUtils` provee métodos estáticos para:
- Calcular el **total de días solicitados** por todos los gerentes.
- Generar un **reporte JSON** (`List<EmpleadoDiasReporteDto>`) con los gerentes que solicitaron más de un número de días mínimo.

El endpoint `/api/empleados-tiempo-completo/nomina-con-dias` utiliza estos métodos para devolver:
- La nómina total por tipo de empleado.
- El total de días solicitados por gerentes.
- La lista de gerentes que superan el umbral configurado (20 días).

---

## Cómo Ejecutar la Aplicación

```bash
./mvnw spring-boot:run
```

Por defecto la aplicación levanta en `http://localhost:8080`.

### Swagger / OpenAPI

Gracias a `springdoc-openapi-starter-webmvc-ui` se expone documentación interactiva en:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

Desde ahí se pueden probar todos los endpoints sin necesidad de usar cURL.

---

## Endpoints REST Principales

### Personas
- `POST /api/personas` → Crear una nueva persona.
- `PUT /api/personas/{id}` → Actualizar persona existente.
- `GET /api/personas` → Listar todas las personas.
- `GET /api/personas?nombre=...` → Filtrar personas por nombre/apellido (contiene, ignore case).
- `GET /api/personas/{id}` → Obtener persona por ID.
- `DELETE /api/personas/{id}` → Eliminar persona.
- `POST /api/personas/{id}/solicitar-dias` → Solicitar días de vacaciones o permisos (usa `GestionDiasService` y valida reglas de negocio).

### Empleados Tiempo Completo
- `POST /api/empleados-tiempo-completo/batch` → Inserción en batch polimórfica (`EmpleadoTiempoCompleto`, `EmpleadoPorHoras`, `Contratista`).
- `GET /api/empleados-tiempo-completo/nomina` → Muestra la nómina total de salarios por tipo de empleado.
- `GET /api/empleados-tiempo-completo/reporte` → Reporte polimórfico con información completa, impuestos y validez.
- `GET /api/empleados-tiempo-completo/nomina-con-dias` → Nómina extendida con resumen de días solicitados por gerentes (usa `NominaUtils`).

### Gerentes
- `POST /api/gerentes` → Crear un nuevo gerente (devuelve `GerenteResponseDto` usando `GerenteMapper`).
- `GET /api/gerentes` → Listar todos los gerentes.

---

## Ejemplos de Pruebas (cURL)

### 1. Batch válido
```bash
curl -X POST http://localhost:8080/api/empleados-tiempo-completo/batch \
  -H "Content-Type: application/json" \
  -d '{"empleadosTiempoCompleto":[{"nombre":"Carlos","apellido":"Gómez","departamento":"Ventas","salarioMensual":5000000,"fechaNacimiento":"1990-05-20","numeroCedula":"1001"}]}'
```

### 2. Batch inválido (validaciones específicas)
```bash
curl -X POST http://localhost:8080/api/empleados-tiempo-completo/batch \
  -H "Content-Type: application/json" \
  -d '{"empleadosTiempoCompleto":[{"nombre":"Malo","apellido":"SinDepto","departamento":"","salarioMensual":0,"fechaNacimiento":"1995-07-15","numeroCedula":"1003"}]}'
```

### 3. Nómina total por tipo
```bash
curl http://localhost:8080/api/empleados-tiempo-completo/nomina
```

### 4. Reporte polimórfico de empleados
```bash
curl http://localhost:8080/api/empleados-tiempo-completo/reporte
```

### 5. Nómina extendida con días (usa NominaUtils)
```bash
curl http://localhost:8080/api/empleados-tiempo-completo/nomina-con-dias
```

### 6. Crear un gerente
```bash
curl -X POST http://localhost:8080/api/gerentes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Ana",
    "apellido":"Gerenta",
    "fechaNacimiento":"1985-01-10",
    "numeroCedula":"2001",
    "salarioMensual":7000000,
    "departamento":"IT",
    "diasVacacionesDisponibles":20,
    "diasPermisosDisponibles":5
  }'
```

### 7. Listar gerentes
```bash
curl http://localhost:8080/api/gerentes
```

### 8. Solicitar días (caso exitoso, usar ID válido)
```bash
curl -X POST http://localhost:8080/api/personas/1/solicitar-dias \
  -H "Content-Type: application/json" \
  -d '{"tipo":"VACACIONES","cantidad":5}' -i
```

### 9. Solicitar días con error por límite anual (gerente supera límite)
```bash
curl -X POST http://localhost:8080/api/personas/1/solicitar-dias \
  -H "Content-Type: application/json" \
  -d '{"tipo":"VACACIONES","cantidad":31}' -i
```

### 10. Solicitar días con empleado no encontrado
```bash
curl -X POST http://localhost:8080/api/personas/9999/solicitar-dias \
  -H "Content-Type: application/json" \
  -d '{"tipo":"VACACIONES","cantidad":5}' -i
```

En los casos de error se devuelve siempre un `ErrorResponseDto` con `status` y `mensaje`, manejado por el `GlobalExceptionHandler`.

---

## Escenarios completos de prueba (cURL)

A continuación se muestran algunos flujos completos típicos que se pueden copiar/pegar en una terminal.

### Escenario A: Crear gerente → Solicitar días → Ver en nómina con días

1. Crear un gerente
```bash
curl -X POST http://localhost:8080/api/gerentes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Ana",
    "apellido":"Gerenta",
    "fechaNacimiento":"1985-01-10",
    "numeroCedula":"2001",
    "salarioMensual":7000000,
    "departamento":"IT",
    "diasVacacionesDisponibles":20,
    "diasPermisosDisponibles":5
  }'
```

2. (Opcional) Ver la lista de gerentes y anotar el `id` devuelto (por ejemplo, `1`)
```bash
curl http://localhost:8080/api/gerentes
```

3. Solicitar 5 días de vacaciones para ese gerente (suponiendo `id = 1`)
```bash
curl -X POST http://localhost:8080/api/personas/1/solicitar-dias \
  -H "Content-Type: application/json" \
  -d '{"tipo":"VACACIONES","cantidad":5}' -i
```

4. Consultar la nómina extendida con días
```bash
curl http://localhost:8080/api/empleados-tiempo-completo/nomina-con-dias
```

### Escenario B: Forzar DiasInsuficientesException por límite anual (gerente)

```bash
curl -X POST http://localhost:8080/api/personas/1/solicitar-dias \
  -H "Content-Type: application/json" \
  -d '{"tipo":"VACACIONES","cantidad":31}' -i
```

Respuesta esperada (estructura):
```json
{
  "status": 400,
  "mensaje": "El gerente supera su límite anual de 30 días."
}
```

### Escenario C: Empleado no encontrado (EmpleadoNoEncontradoException)

```bash
curl -X POST http://localhost:8080/api/personas/9999/solicitar-dias \
  -H "Content-Type: application/json" \
  -d '{"tipo":"VACACIONES","cantidad":5}' -i
```

Respuesta esperada (estructura):
```json
{
  "status": 404,
  "mensaje": "Empleado no encontrado con id 9999"
}
```

### El proyecto cuenta con swagger, acceder a:
```
http://localhost:8080/swagger-ui/index.html#/
```