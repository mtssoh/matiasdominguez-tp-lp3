package py.edu.uc.lp32025.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.antlr.v4.runtime.misc.NotNull;
import py.edu.uc.lp32025.Exception.DiasInsuficientesException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(name  = "empleados_tiempo_completo")
@DiscriminatorValue("TIEMPO_COMPLETO")
public class EmpleadoTiempoCompleto extends Persona implements GestionDias {

    @NotNull
    @Column(name = "salario_mensual", nullable = false, precision = 15, scale = 2)
    private BigDecimal salarioMensual;

    @NotNull
    @Column(name = "departamento", nullable = false, length = 60)
    private String departamento;

    // Campos para gestión de días (empleado "normal")
    @NotNull
    @Column(name = "dias_vacaciones_disponibles", nullable = false)
    private Integer diasVacacionesDisponibles = 20;

    @NotNull
    @Column(name = "dias_permisos_disponibles", nullable = false)
    private Integer diasPermisosDisponibles = 5;

    protected EmpleadoTiempoCompleto() {}

    public EmpleadoTiempoCompleto(
            String nombre,
            String apellido,
            LocalDate fechaNacimiento,
            String numeroCedula,
            BigDecimal salarioMensual,
            String departamento
    ) {
        super(nombre, apellido, fechaNacimiento, numeroCedula);
        this.salarioMensual = salarioMensual;
        this.departamento = departamento;
    }

    // =================== Lógica salarial ===================

    @Override
    public BigDecimal calcularSalario(){
        return salarioMensual != null ?
                salarioMensual.setScale(2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String obtenerInformacionCompleta() {
        return super.obtenerInformacionCompleta() + String.format(
                " | Tipo: Tiempo Completo | Dpto: %s | Salario: %s",
                departamento, calcularSalario()
        );
    }

    @Override
    protected BigDecimal calcularDeducciones() {
        BigDecimal salario = calcularSalario();
        if (salario.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;

        boolean esIT = departamento != null && departamento.trim().equalsIgnoreCase("IT");
        BigDecimal porcentaje = esIT ? new BigDecimal("0.05") : new BigDecimal("0.03");
        return salario.multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean validarDatosEspecificos() {
        boolean salarioOk = salarioMensual != null && salarioMensual.compareTo(new BigDecimal("0")) > 0;
        boolean deptoOk = departamento != null && !departamento.trim().isEmpty();
        return salarioOk && deptoOk;
    }

    // =================== Gestión de días ===================

    @Override
    public int getDiasVacacionesDisponibles() {
        return diasVacacionesDisponibles != null ? diasVacacionesDisponibles : 0;
    }

    @Override
    public int getDiasPermisosDisponibles() {
        return diasPermisosDisponibles != null ? diasPermisosDisponibles : 0;
    }

    @Override
    public void solicitarDias(TipoDia tipo, int cantidad) throws DiasInsuficientesException {
        if (cantidad <= 0) {
            throw new DiasInsuficientesException("La cantidad de días debe ser mayor a cero");
        }

        switch (tipo) {
            case VACACIONES -> {
                if (cantidad > getDiasVacacionesDisponibles()) {
                    throw new DiasInsuficientesException("No tiene días de vacaciones suficientes");
                }
                diasVacacionesDisponibles -= cantidad;
            }
            case PERMISO -> {
                if (cantidad > getDiasPermisosDisponibles()) {
                    throw new DiasInsuficientesException("No tiene días de permiso suficientes");
                }
                diasPermisosDisponibles -= cantidad;
            }
        }
    }

    // =================== Getters / Setters ===================

    public BigDecimal getSalarioMensual() { return salarioMensual; }
    public void setSalarioMensual(BigDecimal salarioMensual) { this.salarioMensual = salarioMensual; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public Integer getDiasVacacionesDisponiblesRaw() { return diasVacacionesDisponibles; }
    public void setDiasVacacionesDisponibles(Integer diasVacacionesDisponibles) { this.diasVacacionesDisponibles = diasVacacionesDisponibles; }

    public Integer getDiasPermisosDisponiblesRaw() { return diasPermisosDisponibles; }
    public void setDiasPermisosDisponibles(Integer diasPermisosDisponibles) { this.diasPermisosDisponibles = diasPermisosDisponibles; }
}
