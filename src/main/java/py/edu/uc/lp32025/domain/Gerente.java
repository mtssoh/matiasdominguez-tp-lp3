package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;
import py.edu.uc.lp32025.Exception.DiasInsuficientesException;

@Entity
@Table(name = "gerentes")
@DiscriminatorValue("GERENTE")
public class Gerente extends EmpleadoTiempoCompleto implements GestionDiasGerente {

    @NotNull
    @Column(name = "dias_vacaciones_disponibles", nullable = false)
    private Integer diasVacacionesDisponibles = 20;

    @NotNull
    @Column(name = "dias_permisos_disponibles", nullable = false)
    private Integer diasPermisosDisponibles = 5;

    @NotNull
    @Column(name = "dias_solicitados_anio", nullable = false)
    private Integer diasSolicitadosEsteAnio = 0;

    protected Gerente() { super(); }

    public Gerente(
            String nombre,
            String apellido,
            java.time.LocalDate fechaNacimiento,
            String numeroCedula,
            java.math.BigDecimal salarioMensual,
            String departamento,
            Integer diasVacacionesDisponibles,
            Integer diasPermisosDisponibles
    ) {
        super(nombre, apellido, fechaNacimiento, numeroCedula, salarioMensual, departamento);
        this.diasVacacionesDisponibles = diasVacacionesDisponibles;
        this.diasPermisosDisponibles = diasPermisosDisponibles;
    }

    // ============= Gestión de días =============

    @Override
    public int getDiasVacacionesDisponibles() {
        return diasVacacionesDisponibles;
    }

    @Override
    public int getDiasPermisosDisponibles() {
        return diasPermisosDisponibles;
    }

    @Override
    public void solicitarDias(TipoDia tipo, int cantidad) throws DiasInsuficientesException {
        if (cantidad <= 0)
            throw new DiasInsuficientesException("La cantidad de días debe ser mayor a cero");

        switch (tipo) {
            case VACACIONES -> {
                if (cantidad > diasVacacionesDisponibles)
                    throw new DiasInsuficientesException("No tiene días de vacaciones suficientes");
                diasVacacionesDisponibles -= cantidad;
            }
            case PERMISO -> {
                if (cantidad > diasPermisosDisponibles)
                    throw new DiasInsuficientesException("No tiene días de permiso suficientes");
                diasPermisosDisponibles -= cantidad;
            }
        }

        diasSolicitadosEsteAnio += cantidad;
    }

    // ============= Propiedad exclusiva de gerentes =============

    @Override
    public int getLimiteAnualGerente() {
        return 30; // ejemplo: permitido hasta 30 al año
    }

    // ============= Getters y setters extra =============

    public Integer getDiasSolicitadosEsteAnio() {
        return diasSolicitadosEsteAnio;
    }

    public void setDiasSolicitadosEsteAnio(Integer diasSolicitadosEsteAnio) {
        this.diasSolicitadosEsteAnio = diasSolicitadosEsteAnio;
    }
}
