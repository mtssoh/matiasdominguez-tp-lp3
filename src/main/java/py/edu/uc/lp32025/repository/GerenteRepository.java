package py.edu.uc.lp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.lp32025.domain.Gerente;

@Repository
public interface GerenteRepository extends JpaRepository<Gerente, Long> {
    // Más adelante podemos agregar métodos específicos, por ahora no hace falta.
}
