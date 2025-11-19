package py.edu.uc.lp32025.Exception;

/**
 * CheckedException: se lanza cuando un empleado no tiene días suficientes
 * para la operación solicitada (vacaciones/permisos).
 */
public class DiasInsuficientesException extends Exception {

    public DiasInsuficientesException(String message) {
        super(message);
    }
}
