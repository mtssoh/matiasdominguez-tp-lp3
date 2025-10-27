package py.edu.uc.lp32025.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

public class BatchResponseDto {

    @NotNull @Min(0)
    private Integer totalProcesados;

    @NotNull @Min(0)
    private Integer totalGuardados;

    @NotNull
    private List<String> errores;

    public BatchResponseDto() { }

    public BatchResponseDto(Integer totalProcesados, Integer totalGuardados, List<String> errores) {
        this.totalProcesados = totalProcesados;
        this.totalGuardados = totalGuardados;
        this.errores = (errores == null) ? Collections.emptyList() : errores;
    }

    public Integer getTotalProcesados() { return totalProcesados; }
    public void setTotalProcesados(Integer totalProcesados) { this.totalProcesados = totalProcesados; }

    public Integer getTotalGuardados() { return totalGuardados; }
    public void setTotalGuardados(Integer totalGuardados) { this.totalGuardados = totalGuardados; }

    public List<String> getErrores() { return errores; }
    public void setErrores(List<String> errores) { this.errores = errores; }

    // Fábricas de conveniencia
    public static BatchResponseDto ok(int procesados, int guardados) {
        return new BatchResponseDto(procesados, guardados, List.of());
    }
    public static BatchResponseDto conErrores(int procesados, int guardados, List<String> errores) {
        return new BatchResponseDto(procesados, guardados, errores);
    }
    public static BatchResponseDto vacio() {
        return new BatchResponseDto(0, 0, List.of());
    }
}
