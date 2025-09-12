package py.edu.uc.lp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.domain.Persona;
import py.edu.uc.lp32025.Service.PersonaService;
import py.edu.uc.lp32025.Exception.FechaNacimientoInvalidaException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/personas")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    // --- Crear una persona ---
    @PostMapping
    public ResponseEntity<?> crearPersona(@RequestBody Persona persona) {
        try {
            Persona guardada = personaService.guardarPersona(persona);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
        } catch (FechaNacimientoInvalidaException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("ERROR", ex.getMessage()));
        }
    }

    // --- Listar todas ---
    @GetMapping
    public List<Persona> obtenerPersonas() {
        return personaService.listarPersonas();
    }

    // --- Obtener por ID ---
    @GetMapping("/{id}")
    public ResponseEntity<Persona> obtenerPersonaPorId(@PathVariable Long id) {
        Optional<Persona> persona = personaService.obtenerPersonaPorId(id);
        return persona.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Actualizar ---
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPersona(@PathVariable Long id, @RequestBody Persona datos) {
        Optional<Persona> personaOptional = personaService.obtenerPersonaPorId(id);
        if (!personaOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Persona persona = personaOptional.get();
        persona.setNombre(datos.getNombre());
        persona.setApellido(datos.getApellido());
        persona.setFechaNacimiento(datos.getFechaNacimiento());
        persona.setNumeroCedula(datos.getNumeroCedula());

        try {
            Persona actualizada = personaService.guardarPersona(persona);
            return ResponseEntity.ok(actualizada);
        } catch (FechaNacimientoInvalidaException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("ERROR", ex.getMessage()));
        }
    }

    // --- Eliminar ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPersona(@PathVariable Long id) {
        boolean eliminado = personaService.eliminarPersona(id);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // --- Clase interna para JSON de error ---
    public static class ErrorResponse {
        private String estado;
        private String mensaje;

        public ErrorResponse(String estado, String mensaje) {
            this.estado = estado;
            this.mensaje = mensaje;
        }

        public String getEstado() { return estado; }
        public String getMensaje() { return mensaje; }
    }
}
