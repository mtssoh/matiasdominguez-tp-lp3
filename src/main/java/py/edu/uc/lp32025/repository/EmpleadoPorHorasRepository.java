package py.edu.uc.lp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.lp32025.domain.EmpleadoPorHoras;

import java.util.List;

@Repository
public interface EmpleadoPorHorasRepository extends JpaRepository<EmpleadoPorHoras, Long> {
    // Empleados con más de X horas trabajadas
    List<EmpleadoPorHoras> findByHorasTrabajadasGreaterThan(Integer horas);
}
