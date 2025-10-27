package py.edu.uc.lp32025.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ReporteEmpleadoDto {

    @NotBlank
    private String tipo; // nombre de la clase (EmpleadoTiempoCompleto, etc.)

    @NotBlank
    private String informacionCompleta; // salida de obtenerInformacionCompleta()

    @NotNull
    private BigDecimal impuestos; // salida de calcularImpuestos()

    @NotNull
    private Boolean valido; // salida de validarDatosEspecificos()

    public ReporteEmpleadoDto() { }

    public ReporteEmpleadoDto(String tipo, String informacionCompleta, BigDecimal impuestos, Boolean valido) {
        this.tipo = tipo;
        this.informacionCompleta = informacionCompleta;
        this.impuestos = impuestos;
        this.valido = valido;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getInformacionCompleta() { return informacionCompleta; }
    public void setInformacionCompleta(String informacionCompleta) { this.informacionCompleta = informacionCompleta; }

    public BigDecimal getImpuestos() { return impuestos; }
    public void setImpuestos(BigDecimal impuestos) { this.impuestos = impuestos; }

    public Boolean getValido() { return valido; }
    public void setValido(Boolean valido) { this.valido = valido; }
}
