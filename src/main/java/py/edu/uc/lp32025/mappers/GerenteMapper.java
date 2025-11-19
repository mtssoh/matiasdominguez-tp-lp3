package py.edu.uc.lp32025.mappers;

import py.edu.uc.lp32025.domain.Gerente;
import py.edu.uc.lp32025.dto.GerenteResponseDto;

public class GerenteMapper implements BaseMapper<Gerente, GerenteResponseDto> {

    @Override
    public GerenteResponseDto toDto(Gerente g) {
        GerenteResponseDto dto = new GerenteResponseDto();
        dto.setId(g.getId());
        dto.setNombreCompleto(g.getNombre() + " " + g.getApellido());
        dto.setDepartamento(g.getDepartamento());
        dto.setSalarioMensual(g.getSalarioMensual());
        dto.setDiasVacacionesDisponibles(g.getDiasVacacionesDisponibles());
        dto.setDiasPermisosDisponibles(g.getDiasPermisosDisponibles());
        dto.setDiasSolicitadosEsteAnio(g.getDiasSolicitadosEsteAnio());
        dto.setLimiteAnualGerente(g.getLimiteAnualGerente());
        return dto;
    }
}
