# Trabajo Practico LP3 - Matias Dominguez


## Descripción General
El sistema gestiona tres tipos de empleados:

- **EmpleadoTiempoCompleto**
- **EmpleadoPorHoras**
- **Contratista**

Cada tipo tiene su propia lógica de cálculo de salario, deducciones e impuestos, además de validaciones específicas.

---

##  Arquitectura
- `domain/` → Entidades y herencia JPA
- `repository/` → Repositorios JPA
- `service/` → Lógica de negocio, validaciones y persistencia en batch
- `controller/` → Endpoints REST
- `dto/` → Clases de transferencia de datos

---

## Entidades

### Persona (abstracta)
- Métodos abstractos:
    - `calcularSalario()`
    - `calcularDeducciones()`
    - `validarDatosEspecificos()`
- Métodos concretos:
    - `calcularImpuestoBase()` (10%)
    - `calcularImpuestos()` (usa template method)
    - `obtenerInformacionCompleta()`

### EmpleadoTiempoCompleto
- Campos: `departamento`, `salarioMensual`
- Deducción: 5% si es “IT”, 3% en otros
- Validaciones: salario > 0 y departamento no vacío

### EmpleadoPorHoras
- Campos: `tarifaPorHora`, `horasTrabajadas`
- Salario = tarifa × horas (+50% bonus si >40h)
- Deducción: 2%
- Validaciones: tarifa > 0, horas entre 1–80

### Contratista
- Campos: `montoPorProyecto`, `proyectosCompletados`, `fechaFinContrato`
- Sin deducciones
- Validaciones: fecha futura y proyectos ≥ 0
- Método adicional: `contratoVigente()`

---

## Endpoints REST

| Método | Endpoint | Descripción |
|--------|-----------|-------------|
| POST | `/api/empleados-tiempo-completo/batch` | Inserción en batch |
| GET | `/api/empleados-tiempo-completo/nomina` | Muestra la nómina total |
| GET | `/api/empleados-tiempo-completo/reporte` | Reporte con polimorfismo |
| GET | `/api/personas?nombre=ale` | Filtro de personas por nombre o apellido |

---

## Ejemplos de Pruebas (cURL)

### Batch válido
```bash
curl -X POST http://localhost:8080/api/empleados-tiempo-completo/batch \
  -H "Content-Type: application/json" \
  -d '{"empleadosTiempoCompleto":[{"nombre":"Carlos","apellido":"Gómez","departamento":"Ventas","salarioMensual":5000000,"fechaNacimiento":"1990-05-20","numeroCedula":"1001"}]}'
``` 

### Batch Invalido
```bash
curl -X POST http://localhost:8080/api/empleados-tiempo-completo/batch \
  -H "Content-Type: application/json" \
  -d '{"empleadosTiempoCompleto":[{"nombre":"Malo","apellido":"SinDepto","departamento":"","salarioMensual":0,"fechaNacimiento":"1995-07-15","numeroCedula":"1003"}]}'
```

### Nomina total
```bash
curl http://localhost:8080/api/empleados-tiempo-completo/nomina
```

### Reporte de empleados
```bash
curl http://localhost:8080/api/empleados-tiempo-completo/reporte
```