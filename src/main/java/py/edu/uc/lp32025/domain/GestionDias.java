package py.edu.uc.lp32025.domain;

import py.edu.uc.lp32025.Exception.DiasInsuficientesException;

public interface GestionDias {

    int getDiasVacacionesDisponibles();

    int getDiasPermisosDisponibles();

    /**
     * Solicita días de vacaciones o permisos.
     * Debe lanzar DiasInsuficientesException si no hay saldo.
     */
    void solicitarDias(TipoDia tipo, int cantidad) throws DiasInsuficientesException;
}
