package py.edu.uc.lp32025.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class NominaConDiasResponseDto {

    // lo que ya tenías: monto total por tipo de empleado
    private Map<String, BigDecimal> nominaPorTipoEmpleado;

    // nuevo: total de días solicitados por gerentes
    private int totalDiasSolicitadosGerentes;

    // nuevo: reporte de gerentes que superan un mínimo de días
    private List<EmpleadoDiasReporteDto> gerentesConMasDeMinDias;

    public NominaConDiasResponseDto() { }

    public NominaConDiasResponseDto(Map<String, BigDecimal> nominaPorTipoEmpleado,
                                    int totalDiasSolicitadosGerentes,
                                    List<EmpleadoDiasReporteDto> gerentesConMasDeMinDias) {
        this.nominaPorTipoEmpleado = nominaPorTipoEmpleado;
        this.totalDiasSolicitadosGerentes = totalDiasSolicitadosGerentes;
        this.gerentesConMasDeMinDias = gerentesConMasDeMinDias;
    }

    public Map<String, BigDecimal> getNominaPorTipoEmpleado() {
        return nominaPorTipoEmpleado;
    }

    public void setNominaPorTipoEmpleado(Map<String, BigDecimal> nominaPorTipoEmpleado) {
        this.nominaPorTipoEmpleado = nominaPorTipoEmpleado;
    }

    public int getTotalDiasSolicitadosGerentes() {
        return totalDiasSolicitadosGerentes;
    }

    public void setTotalDiasSolicitadosGerentes(int totalDiasSolicitadosGerentes) {
        this.totalDiasSolicitadosGerentes = totalDiasSolicitadosGerentes;
    }

    public List<EmpleadoDiasReporteDto> getGerentesConMasDeMinDias() {
        return gerentesConMasDeMinDias;
    }

    public void setGerentesConMasDeMinDias(List<EmpleadoDiasReporteDto> gerentesConMasDeMinDias) {
        this.gerentesConMasDeMinDias = gerentesConMasDeMinDias;
    }
}
