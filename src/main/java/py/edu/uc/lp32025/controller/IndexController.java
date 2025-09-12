package py.edu.uc.lp32025.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import py.edu.uc.lp32025.dto.SaludoDto;

@RestController
public class IndexController {

    @RequestMapping("/")
    public RedirectView redirectToHolaMundo() {
        return new RedirectView("/HolaMundo");
    }

    @RequestMapping("/HolaMundo")
    public SaludoDto HolaMundo() {
        return new SaludoDto("¡Hola desde Spring Boot!");
    }

    @GetMapping("/HolaMundo/{nombre}")
    public SaludoDto holaMundo(@PathVariable String nombre) {
        if (nombre.length() < 2) {
            return new SaludoDto(400, "Nombre demasiado corto", "Por favor, introduce un nombre más largo.");
        }
        return new SaludoDto("¡Hola " + nombre + " desde Spring Boot!");
    }
}
