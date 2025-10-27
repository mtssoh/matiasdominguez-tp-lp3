package py.edu.uc.lp32025.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import py.edu.uc.lp32025.domain.Contratista;
import py.edu.uc.lp32025.domain.EmpleadoPorHoras;
import py.edu.uc.lp32025.domain.EmpleadoTiempoCompleto;

public class BatchEmpleadosRequest {

    // Al menos una de las listas debería venir con datos; la regla de negocio la validamos en el controlador/servicio.
    @Valid
    @Size(max = 1000, message = "No se permiten más de 1000 empleados de tiempo completo por request")
    private List<EmpleadoTiempoCompleto> empleadosTiempoCompleto;

    @Valid
    @Size(max = 1000, message = "No se permiten más de 1000 empleados por horas por request")
    private List<EmpleadoPorHoras> empleadosPorHoras;

    @Valid
    @Size(max = 1000, message = "No se permiten más de 1000 contratistas por request")
    private List<Contratista> contratistas;

    public List<EmpleadoTiempoCompleto> getEmpleadosTiempoCompleto() { return empleadosTiempoCompleto; }
    public void setEmpleadosTiempoCompleto(List<EmpleadoTiempoCompleto> empleadosTiempoCompleto) { this.empleadosTiempoCompleto = empleadosTiempoCompleto; }

    public List<EmpleadoPorHoras> getEmpleadosPorHoras() { return empleadosPorHoras; }
    public void setEmpleadosPorHoras(List<EmpleadoPorHoras> empleadosPorHoras) { this.empleadosPorHoras = empleadosPorHoras; }

    public List<Contratista> getContratistas() { return contratistas; }
    public void setContratistas(List<Contratista> contratistas) { this.contratistas = contratistas; }


    public boolean estaVacio() {
        return (empleadosTiempoCompleto == null || empleadosTiempoCompleto.isEmpty())
                && (empleadosPorHoras == null || empleadosPorHoras.isEmpty())
                && (contratistas == null || contratistas.isEmpty());
    }
}
