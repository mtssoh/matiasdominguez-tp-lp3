package py.edu.uc.lp32025.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.controller.BaseController;
import py.edu.uc.lp32025.domain.Gerente;
import py.edu.uc.lp32025.mappers.GerenteMapper;
import py.edu.uc.lp32025.repository.GerenteRepository;

@RestController
@RequestMapping("/api/gerentes")
public class GerenteController extends BaseController {

    private final GerenteRepository gerenteRepository;
    private final GerenteMapper gerenteMapper = new GerenteMapper();

    public GerenteController(GerenteRepository gerenteRepository) {
        this.gerenteRepository = gerenteRepository;
    }

    @PostMapping
    public ResponseEntity<?> crearGerente(@RequestBody Gerente gerente) {
        Gerente guardado = gerenteRepository.save(gerente);
        return ok(gerenteMapper.toDto(guardado));
    }

    @GetMapping
    public ResponseEntity<?> listarGerentes() {
        return ok(
                gerenteRepository.findAll().stream()
                        .map(gerenteMapper::toDto)
                        .toList()
        );
    }
}
