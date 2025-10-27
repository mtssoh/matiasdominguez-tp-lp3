package py.edu.uc.lp32025.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(name = "personas")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_empleado", discriminatorType = DiscriminatorType.STRING, length = 30)
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "numero_cedula", unique = true, nullable = false, length = 15)
    private String numeroCedula;


    protected Persona() {}

    protected Persona(String nombre, String apellido, LocalDate fechaNacimiento, String numeroCedula) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.numeroCedula = numeroCedula;
    }


    public abstract BigDecimal calcularSalario();


    public String obtenerInformacionCompleta() {
        return String.format("ID: %s | %s %s | C.I.: %s | Nac.: %s",
                id, nombre, apellido, numeroCedula, fechaNacimiento);
    }


    public final BigDecimal calcularImpuestos() {
        BigDecimal impuestoBase = calcularImpuestoBase();
        BigDecimal deducciones = calcularDeducciones();

        BigDecimal total = impuestoBase.subtract(
                deducciones != null ? deducciones : BigDecimal.ZERO
        );


        return total.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }


    protected BigDecimal calcularImpuestoBase() {
        BigDecimal salario = calcularSalario();
        if (salario == null) salario = BigDecimal.ZERO;
        return salario.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
    }


    protected abstract BigDecimal calcularDeducciones();


    public abstract boolean validarDatosEspecificos();



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getNumeroCedula() { return numeroCedula; }
    public void setNumeroCedula(String numeroCedula) { this.numeroCedula = numeroCedula; }
}