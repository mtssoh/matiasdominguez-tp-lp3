package py.edu.uc.lp32025.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import py.edu.uc.lp32025.dto.ErrorResponseDto;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FechaNacimientoInvalidaException.class)
    public ResponseEntity<ErrorResponseDto> handleFechaNacimientoInvalida(FechaNacimientoInvalidaException ex) {
        ErrorResponseDto dto = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(DiasInsuficientesException.class)
    public ResponseEntity<ErrorResponseDto> handleDiasInsuficientes(DiasInsuficientesException ex) {
        ErrorResponseDto dto = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(EmpleadoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDto> handleEmpleadoNoEncontrado(EmpleadoNoEncontradoException ex) {
        ErrorResponseDto dto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
    }

    // (Opcional pero recomendable) handler genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenerico(Exception ex) {
        ErrorResponseDto dto = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
    }
}
