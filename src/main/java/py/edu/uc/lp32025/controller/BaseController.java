package py.edu.uc.lp32025.controller;

import org.springframework.http.ResponseEntity;

/**
 * Controlador base para estandarizar respuestas REST.
 * Las clases hijas pueden usar estos métodos en lugar de ResponseEntity.xxx().
 */
public abstract class BaseController {

    protected <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }

    protected ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    protected ResponseEntity<Void> notFound() {
        return ResponseEntity.notFound().build();
    }

    protected <T> ResponseEntity<T> badRequest(T body) {
        return ResponseEntity.badRequest().body(body);
    }
}
