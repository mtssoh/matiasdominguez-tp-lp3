package py.edu.uc.lp32025.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.edu.uc.lp32025.Exception.FechaNacimientoInvalidaException;
import py.edu.uc.lp32025.domain.Persona;
import py.edu.uc.lp32025.repository.PersonaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    // --- Crear o actualizar ---

    public Persona guardarPersona(Persona persona) {
        if (persona.getFechaNacimiento() == null) {
            throw new FechaNacimientoInvalidaException("La fecha de nacimiento no puede ser nula");
        }

        if (persona.getFechaNacimiento().isAfter(java.time.LocalDate.now())) {
            throw new FechaNacimientoInvalidaException("La fecha de nacimiento no puede ser en el futuro");
        }

        return personaRepository.save(persona);
    }


    // --- Listar todas ---
    public List<Persona> listarPersonas() {
        return personaRepository.findAll();
    }

    // --- Obtener por ID ---
    public Optional<Persona> obtenerPersonaPorId(Long id) {
        return personaRepository.findById(id);
    }

    // --- Eliminar por ID ---
    public boolean eliminarPersona(Long id) {
        if (!personaRepository.existsById(id)) {
            return false;
        }
        personaRepository.deleteById(id);
        return true;
    }
}
