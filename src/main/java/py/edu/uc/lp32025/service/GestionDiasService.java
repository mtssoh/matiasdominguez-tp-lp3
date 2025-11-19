package py.edu.uc.lp32025.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import py.edu.uc.lp32025.Exception.DiasInsuficientesException;
import py.edu.uc.lp32025.Exception.EmpleadoNoEncontradoException;
import py.edu.uc.lp32025.domain.*;
import py.edu.uc.lp32025.repository.PersonaRepository;

@Service
public class GestionDiasService {

    private final PersonaRepository personaRepository;

    public GestionDiasService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    /**
     * Regla de negocio:
     * - Busca la persona por ID, si no existe -> EmpleadoNoEncontradoException.
     * - Verifica que implemente GestionDias, si no -> DiasInsuficientesException (o IllegalState).
     * - Si NO es gerente, no puede solicitar más de 20 días en una sola operación.
     * - Si es gerente, puede pedir más de 20, pero no superar su límite anual.
     * - Delegamos el descuento real de días a solicitarDias(...) de la entidad.
     */
    @Transactional
    public void solicitarDias(Long personaId, TipoDia tipo, int cantidad) throws DiasInsuficientesException {

        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() ->
                        new EmpleadoNoEncontradoException("Empleado no encontrado con id " + personaId));

        if (!(persona instanceof GestionDias gestionDias)) {
            throw new DiasInsuficientesException("El empleado no admite gestión de días de vacaciones/permisos.");
        }

        // Regla: solo los gerentes pueden solicitar más de 20 días en el año.
        // Para simplificar: si NO es gerente y pide más de 20 en una sola solicitud -> error.
        if (!(persona instanceof Gerente) && cantidad > 20) {
            throw new DiasInsuficientesException("Solo los gerentes pueden solicitar más de 20 días al año.");
        }

        // Si es gerente, además podemos validar contra su límite anual
        if (persona instanceof Gerente gerente) {
            int actual = gerente.getDiasSolicitadosEsteAnio() != null ? gerente.getDiasSolicitadosEsteAnio() : 0;
            int nuevoTotal = actual + cantidad;

            if (nuevoTotal > gerente.getLimiteAnualGerente()) {
                throw new DiasInsuficientesException(
                        "El gerente supera su límite anual de " + gerente.getLimiteAnualGerente() + " días.");
            }

            // Si pasa estas reglas, delegamos en la lógica del modelo
            gestionDias.solicitarDias(tipo, cantidad);
            // El cambio queda persistido gracias a JPA + @Transactional
        } else {
            // Empleado "normal" que implementa GestionDias (si en el futuro agregas otros)
            gestionDias.solicitarDias(tipo, cantidad);
        }

        // Persistimos el estado actualizado
        personaRepository.save(persona);
    }
}
