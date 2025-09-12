package py.edu.uc.lp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.lp32025.domain.Persona;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    // Opcional: puedes agregar métodos personalizados si los necesitas
    // Ejemplo: List<Persona> findByNombre(String nombre);
}
