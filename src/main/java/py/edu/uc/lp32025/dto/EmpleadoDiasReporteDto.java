package py.edu.uc.lp32025.dto;

public class EmpleadoDiasReporteDto {

    private Long idEmpleado;
    private String nombreCompleto;
    private int diasSolicitados;

    public EmpleadoDiasReporteDto(Long idEmpleado, String nombreCompleto, int diasSolicitados) {
        this.idEmpleado = idEmpleado;
        this.nombreCompleto = nombreCompleto;
        this.diasSolicitados = diasSolicitados;
    }

    public Long getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(Long idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public int getDiasSolicitados() { return diasSolicitados; }
    public void setDiasSolicitados(int diasSolicitados) { this.diasSolicitados = diasSolicitados; }
}
