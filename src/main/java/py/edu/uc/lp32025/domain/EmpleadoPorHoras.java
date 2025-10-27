package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "empleados_por_horas")
@DiscriminatorValue("POR_HORAS")
public class EmpleadoPorHoras extends Persona {

    @NotNull
    @Column(name = "tarifa_por_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaPorHora;

    @NotNull
    @Column(name = "horas_trabajadas", nullable = false)
    private Integer horasTrabajadas;

    // ---- Constructores ----
    protected EmpleadoPorHoras() { }

    public EmpleadoPorHoras(
            String nombre,
            String apellido,
            java.time.LocalDate fechaNacimiento,
            String numeroCedula,
            BigDecimal tarifaPorHora,
            Integer horasTrabajadas
    ) {
        super(nombre, apellido, fechaNacimiento, numeroCedula);
        this.tarifaPorHora = tarifaPorHora;
        this.horasTrabajadas = horasTrabajadas;
    }

    // ---- Implementaciones requeridas ----
    @Override
    public BigDecimal calcularSalario() {
        if (tarifaPorHora == null || horasTrabajadas == null)
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        BigDecimal horas = new BigDecimal(horasTrabajadas);
        BigDecimal salarioBase = tarifaPorHora.multiply(horas);

        // Bonificación del 50% de la tarifa por cada hora extra (>40)
        if (horasTrabajadas > 40) {
            int extras = horasTrabajadas - 40;
            BigDecimal bonus = tarifaPorHora
                    .multiply(new BigDecimal("0.5"))
                    .multiply(new BigDecimal(extras));
            salarioBase = salarioBase.add(bonus);
        }

        return salarioBase.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    protected BigDecimal calcularDeducciones() {
        BigDecimal salario = calcularSalario();
        if (salario.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return salario.multiply(new BigDecimal("0.02")).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean validarDatosEspecificos() {
        boolean tarifaOk = tarifaPorHora != null && tarifaPorHora.compareTo(new BigDecimal("0")) > 0;
        boolean horasOk = horasTrabajadas != null && horasTrabajadas >= 1 && horasTrabajadas <= 80;
        return tarifaOk && horasOk;
    }

    @Override
    public String obtenerInformacionCompleta() {
        return super.obtenerInformacionCompleta() + String.format(
                " | Tipo: Por Horas | Tarifa: %s | Horas: %d | Salario: %s",
                tarifaPorHora, horasTrabajadas, calcularSalario()
        );
    }

    // ---- Getters & Setters ----
    public BigDecimal getTarifaPorHora() { return tarifaPorHora; }
    public void setTarifaPorHora(BigDecimal tarifaPorHora) { this.tarifaPorHora = tarifaPorHora; }

    public Integer getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(Integer horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }
}
