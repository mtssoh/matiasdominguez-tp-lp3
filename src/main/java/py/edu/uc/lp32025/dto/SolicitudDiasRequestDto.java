package py.edu.uc.lp32025.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import py.edu.uc.lp32025.domain.TipoDia;

public class SolicitudDiasRequestDto {

    @NotNull
    private TipoDia tipo;

    @NotNull
    @Min(1)
    private Integer cantidad;

    public TipoDia getTipo() { return tipo; }
    public void setTipo(TipoDia tipo) { this.tipo = tipo; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
