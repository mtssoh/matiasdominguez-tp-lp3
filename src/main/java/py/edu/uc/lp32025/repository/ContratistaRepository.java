package py.edu.uc.lp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.lp32025.domain.Contratista;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContratistaRepository extends JpaRepository<Contratista, Long> {

    // “Contratos vigentes”: fechaFinContrato posterior a hoy
    List<Contratista> findByFechaFinContratoAfter(LocalDate fecha);
}

