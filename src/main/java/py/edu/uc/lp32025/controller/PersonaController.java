package py.edu.uc.lp32025.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.Exception.DiasInsuficientesException;
import py.edu.uc.lp32025.domain.Persona;
import py.edu.uc.lp32025.dto.SolicitudDiasRequestDto;
import py.edu.uc.lp32025.service.GestionDiasService;
import py.edu.uc.lp32025.service.PersonaService;

import java.util.List;
import java.util.Optional;

@Tag(name = "Personas", description = "Operaciones generales sobre personas")
@RestController
@RequestMapping("/api/personas")
public class PersonaController extends BaseController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private GestionDiasService gestionDiasService;

    // ------------------------------
    //      CREAR PERSONA
    // ------------------------------
    @Operation(summary = "Crear una nueva persona")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Persona creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public Object guardarPersona(@RequestBody Persona persona) {
        return ok(personaService.guardarPersona(persona));
    }

    // ------------------------------
    //      ACTUALIZAR PERSONA
    // ------------------------------
    @Operation(summary = "Actualizar una persona por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Persona actualizada"),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada")
    })
    @PutMapping("/{id}")
    public Object actualizarPersona(
            @PathVariable Long id,
            @RequestBody Persona persona) {

        persona.setId(id);
        return ok(personaService.guardarPersona(persona));
    }

    // ------------------------------
    //      LISTAR PERSONAS
    // ------------------------------
    @Operation(summary = "Listar todas las personas")
    @GetMapping
    public List<Persona> listarPersonas() {
        return personaService.listarPersonas();
    }

    // ------------------------------
    //      FILTRAR POR NOMBRE
    // ------------------------------
    @Operation(summary = "Filtrar personas por nombre (contiene, ignore case)")
    @GetMapping(params = "nombre")
    public List<Persona> filtrarPorNombre(@RequestParam String nombre) {
        return personaService.filtrarPorNombre(nombre);
    }

    // ------------------------------
    //      OBTENER POR ID
    // ------------------------------
    @Operation(summary = "Obtener una persona por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Persona encontrada"),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada")
    })
    @GetMapping("/{id}")
    public Object obtenerPersonaPorId(@PathVariable Long id) {

        Optional<Persona> persona = personaService.obtenerPersonaPorId(id);

        return persona
                .<Object>map(this::ok)
                .orElseGet(this::notFound);
    }

    // ------------------------------
    //      ELIMINAR PERSONA
    // ------------------------------
    @Operation(summary = "Eliminar una persona por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Persona eliminada"),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada")
    })
    @DeleteMapping("/{id}")
    public Object eliminarPersona(@PathVariable Long id) {
        return personaService.eliminarPersona(id)
                ? noContent()
                : notFound();
    }

    // ------------------------------
    //      SOLICITAR DÍAS
    // ------------------------------
    @Operation(summary = "Solicitar días de vacaciones o permisos")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Días solicitados correctamente"),
            @ApiResponse(responseCode = "400", description = "Días insuficientes o datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @PostMapping("/{id}/solicitar-dias")
    public Object solicitarDias(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudDiasRequestDto request)
            throws DiasInsuficientesException {

        gestionDiasService.solicitarDias(id, request.getTipo(), request.getCantidad());
        return noContent();
    }
}
