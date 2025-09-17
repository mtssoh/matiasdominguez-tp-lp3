package py.edu.uc.lp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.Exception.FechaNacimientoInvalidaException;
import py.edu.uc.lp32025.Service.PersonaService;
import py.edu.uc.lp32025.domain.Persona;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    // --- Crear o actualizar ---
    @PostMapping
    public ResponseEntity<?> guardarPersona(@RequestBody Persona persona) {
        try {
            return ResponseEntity.ok(personaService.guardarPersona(persona));
        } catch (FechaNacimientoInvalidaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- Listar todas ---
    @GetMapping
    public List<Persona> listarPersonas() {
        return personaService.listarPersonas();
    }

    // --- Obtener por ID ---
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPersonaPorId(@PathVariable Long id) {
        Optional<Persona> persona = personaService.obtenerPersonaPorId(id);
        return persona.isPresent() ? ResponseEntity.ok(persona.get())
                : ResponseEntity.notFound().build();
    }

    // --- Eliminar ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPersona(@PathVariable Long id) {
        return personaService.eliminarPersona(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
