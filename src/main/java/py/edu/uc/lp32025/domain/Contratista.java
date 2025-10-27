package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(name = "contratistas")
@DiscriminatorValue("CONTRATISTA")
public class Contratista extends Persona {

    @NotNull
    @Column(name = "monto_por_proyecto", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoPorProyecto;

    @NotNull
    @Column(name = "proyectos_completados", nullable = false)
    private Integer proyectosCompletados;

    @NotNull
    @Column(name = "fecha_fin_contrato", nullable = false)
    private LocalDate fechaFinContrato;

    // ---- Constructores ----
    protected Contratista() { }

    public Contratista(
            String nombre,
            String apellido,
            LocalDate fechaNacimiento,
            String numeroCedula,
            BigDecimal montoPorProyecto,
            Integer proyectosCompletados,
            LocalDate fechaFinContrato
    ) {
        super(nombre, apellido, fechaNacimiento, numeroCedula);
        this.montoPorProyecto = montoPorProyecto;
        this.proyectosCompletados = proyectosCompletados;
        this.fechaFinContrato = fechaFinContrato;
    }

    // ---- Implementaciones requeridas ----
    @Override
    public BigDecimal calcularSalario() {
        if (montoPorProyecto == null || proyectosCompletados == null)
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = montoPorProyecto.multiply(new BigDecimal(proyectosCompletados));
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    protected BigDecimal calcularDeducciones() {
        // Sin deducciones para contratistas
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean validarDatosEspecificos() {
        boolean montoOk = montoPorProyecto != null && montoPorProyecto.compareTo(BigDecimal.ZERO) > 0;
        boolean proyectosOk = proyectosCompletados != null && proyectosCompletados >= 0;
        boolean fechaOk = fechaFinContrato != null && !fechaFinContrato.isBefore(LocalDate.now()); // futura o hoy no vencido
        return montoOk && proyectosOk && fechaOk;
    }

    @Override
    public String obtenerInformacionCompleta() {
        return super.obtenerInformacionCompleta() + String.format(
                " | Tipo: Contratista | Monto/Proyecto: %s | Proy.: %d | Fin Contrato: %s | Salario: %s | Vigente: %s",
                montoPorProyecto, proyectosCompletados, fechaFinContrato, calcularSalario(), contratoVigente() ? "Sí" : "No"
        );
    }

    // ---- Método adicional ----
    /** Retorna true si el contrato no ha vencido (fecha de fin >= hoy). */
    public boolean contratoVigente() {
        return fechaFinContrato != null && !fechaFinContrato.isBefore(LocalDate.now());
    }

    // ---- Getters & Setters ----
    public BigDecimal getMontoPorProyecto() { return montoPorProyecto; }
    public void setMontoPorProyecto(BigDecimal montoPorProyecto) { this.montoPorProyecto = montoPorProyecto; }

    public Integer getProyectosCompletados() { return proyectosCompletados; }
    public void setProyectosCompletados(Integer proyectosCompletados) { this.proyectosCompletados = proyectosCompletados; }

    public LocalDate getFechaFinContrato() { return fechaFinContrato; }
    public void setFechaFinContrato(LocalDate fechaFinContrato) { this.fechaFinContrato = fechaFinContrato; }
}
