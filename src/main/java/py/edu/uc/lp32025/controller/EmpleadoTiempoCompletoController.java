package py.edu.uc.lp32025.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.domain.Persona;
import py.edu.uc.lp32025.dto.BatchEmpleadosRequest;
import py.edu.uc.lp32025.dto.BatchResponseDto;
import py.edu.uc.lp32025.dto.ReporteEmpleadoDto;
import py.edu.uc.lp32025.domain.Contratista;
import py.edu.uc.lp32025.domain.EmpleadoPorHoras;
import py.edu.uc.lp32025.domain.EmpleadoTiempoCompleto;
import py.edu.uc.lp32025.repository.ContratistaRepository;
import py.edu.uc.lp32025.repository.EmpleadoPorHorasRepository;
import py.edu.uc.lp32025.repository.EmpleadoTiempoCompletoRepository;
import py.edu.uc.lp32025.service.EmpleadoTiempoCompletoService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados-tiempo-completo")
public class EmpleadoTiempoCompletoController {

    private final EmpleadoTiempoCompletoService service;
    private final EmpleadoTiempoCompletoRepository etcRepo;
    private final EmpleadoPorHorasRepository ephRepo;
    private final ContratistaRepository cRepo;

    public EmpleadoTiempoCompletoController(EmpleadoTiempoCompletoService service,
                                            EmpleadoTiempoCompletoRepository etcRepo,
                                            EmpleadoPorHorasRepository ephRepo,
                                            ContratistaRepository cRepo) {
        this.service = service;
        this.etcRepo = etcRepo;
        this.ephRepo = ephRepo;
        this.cRepo = cRepo;
    }

    // ---- 4.1 Batch (solo EmpleadoTiempoCompleto) ----
    @PostMapping("/batch")
    public ResponseEntity<BatchResponseDto> guardarBatch(@Valid @RequestBody BatchEmpleadosRequest request) {
        List<EmpleadoTiempoCompleto> lista = request.getEmpleadosTiempoCompleto();
        if (lista == null || lista.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(BatchResponseDto.conErrores(0, 0, List.of("La lista de empleados de tiempo completo está vacía.")));
        }

        EmpleadoTiempoCompletoService.BatchResultado r = service.guardarEmpleadosEnBatch(lista);
        return ResponseEntity.ok(new BatchResponseDto(r.getTotalProcesados(), r.getTotalGuardados(), r.getErrores()));
    }

    // ---- 4.2 Nómina total por tipo ----
    @GetMapping("/nomina")
    public ResponseEntity<Map<String, BigDecimal>> obtenerNomina() {
        return ResponseEntity.ok(service.calcularNominaTotal());
    }

    // ---- 4.3 Reporte polimórfico (devuelve DTOs completos) ----
    @GetMapping("/reporte")
    public ResponseEntity<List<ReporteEmpleadoDto>> reporte() {
        List<ReporteEmpleadoDto> out = new ArrayList<>();
        // Unimos todos los tipos y aplicamos polimorfismo
        List<? extends Persona> todos = new ArrayList<Persona>() {{
            addAll(etcRepo.findAll());
            addAll(ephRepo.findAll());
            addAll(cRepo.findAll());
        }};
        for (Persona p : todos) {
            out.add(new ReporteEmpleadoDto(
                    p.getClass().getSimpleName(),
                    p.obtenerInformacionCompleta(),
                    p.calcularImpuestos(),
                    p.validarDatosEspecificos()
            ));
        }
        return ResponseEntity.ok(out);
    }
}
