package py.edu.uc.lp32025.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class GerenteResponseDto {
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String nombreCompleto;

    @Getter
    @Setter
    private String departamento;

    @Getter
    @Setter
    private BigDecimal salarioMensual;

    @Getter
    @Setter
    private Integer diasVacacionesDisponibles;

    @Getter
    @Setter
    private Integer diasPermisosDisponibles;

    @Getter
    @Setter
    private Integer diasSolicitadosEsteAnio;

    @Getter
    @Setter
    private Integer limiteAnualGerente;

    // getters y setters
}
