package py.edu.uc.lp32025.domain;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(name  = "empleados_tiempo_completo")
@DiscriminatorValue("TIEMPO_COMPLETO")

public class EmpleadoTiempoCompleto extends Persona{

    @NotNull
    @Column(name = "salario_mensual", nullable = false, precision = 15, scale = 2)
    private BigDecimal salarioMensual;

    @NotNull
    @Column(name = "departamento", nullable = false, length = 60)
    private String departamento;

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


    public BigDecimal getSalarioMensual() { return salarioMensual; }
    public void setSalarioMensual(BigDecimal salarioMensual) { this.salarioMensual = salarioMensual; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }


}
