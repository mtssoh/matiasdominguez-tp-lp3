package py.edu.uc.lp32025.util;

import py.edu.uc.lp32025.domain.Gerente;
import py.edu.uc.lp32025.dto.EmpleadoDiasReporteDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilidades relacionadas a la nómina:
 *  - cálculo de total de días solicitados
 *  - generación de reportes de empleados con muchos días pedidos
 */
public final class NominaUtils {

    private NominaUtils() {
        // clase utilitaria, no instanciable
    }

    /**
     * Suma los días solicitados en el año para todos los gerentes.
     */
    public static int calcularTotalDiasSolicitados(List<Gerente> gerentes) {
        if (gerentes == null || gerentes.isEmpty()) {
            return 0;
        }
        return gerentes.stream()
                .mapToInt(g -> g.getDiasSolicitadosEsteAnio() != null ? g.getDiasSolicitadosEsteAnio() : 0)
                .sum();
    }

    /**
     * Genera una lista de reporte (que luego se serializa a JSON)
     * con los empleados que superan un mínimo de días solicitados.
     */
    public static List<EmpleadoDiasReporteDto> generarReporteEmpleadosConMasDe(
            List<Gerente> gerentes, int minDias) {

        List<EmpleadoDiasReporteDto> resultado = new ArrayList<>();
        if (gerentes == null || gerentes.isEmpty()) {
            return resultado;
        }

        for (Gerente g : gerentes) {
            int dias = g.getDiasSolicitadosEsteAnio() != null ? g.getDiasSolicitadosEsteAnio() : 0;
            if (dias > minDias) {
                resultado.add(new EmpleadoDiasReporteDto(
                        g.getId(),
                        g.getNombre() + " " + g.getApellido(),
                        dias
                ));
            }
        }
        return resultado;
    }
}
