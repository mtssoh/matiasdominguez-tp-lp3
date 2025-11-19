package py.edu.uc.lp32025.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp32025.domain.Persona;
import py.edu.uc.lp32025.dto.BatchEmpleadosRequest;
import py.edu.uc.lp32025.dto.BatchResponseDto;
import py.edu.uc.lp32025.dto.NominaConDiasResponseDto;
import py.edu.uc.lp32025.dto.ReporteEmpleadoDto;
import py.edu.uc.lp32025.repository.ContratistaRepository;
import py.edu.uc.lp32025.repository.EmpleadoPorHorasRepository;
import py.edu.uc.lp32025.repository.EmpleadoTiempoCompletoRepository;
import py.edu.uc.lp32025.repository.GerenteRepository;
import py.edu.uc.lp32025.service.EmpleadoTiempoCompletoService;
import py.edu.uc.lp32025.util.NominaUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(
        name = "Empleados Tiempo Completo",
        description = "Batch polimórfico, nómina y reportes de empleados"
)
@RestController
@RequestMapping("/api/empleados-tiempo-completo")
public class EmpleadoTiempoCompletoController extends BaseController {

    private final EmpleadoTiempoCompletoService service;
    private final EmpleadoTiempoCompletoRepository etcRepo;
    private final EmpleadoPorHorasRepository ephRepo;
    private final ContratistaRepository cRepo;
    private final GerenteRepository grRepo;

    public EmpleadoTiempoCompletoController(EmpleadoTiempoCompletoService service,
                                            EmpleadoTiempoCompletoRepository etcRepo,
                                            EmpleadoPorHorasRepository ephRepo,
                                            ContratistaRepository cRepo,
                                            GerenteRepository grRepo) {
        this.service = service;
        this.etcRepo = etcRepo;
        this.ephRepo = ephRepo;
        this.cRepo = cRepo;
        this.grRepo = grRepo;
    }

    // ---- 4.1 Batch polimórfico (TC, por horas y contratistas) ----
    @Operation(
            summary = "Carga masiva polimórfica",
            description = "Permite cargar en un solo request empleados de tiempo completo, por horas y contratistas"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch procesado correctamente",
                    content = @Content(schema = @Schema(implementation = BatchResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Request vacío o inválido")
    })
    @PostMapping("/batch")
    public ResponseEntity<BatchResponseDto> guardarBatch(@Valid @RequestBody BatchEmpleadosRequest request) {

        if (request.estaVacio()) {
            return badRequest(
                    BatchResponseDto.conErrores(
                            0,
                            0,
                            List.of("Debe enviar al menos un empleado en alguna de las listas.")
                    )
            );
        }

        EmpleadoTiempoCompletoService.BatchResultado r = service.guardarEmpleadosEnBatch(request);

        return ok(new BatchResponseDto(
                r.getTotalProcesados(),
                r.getTotalGuardados(),
                r.getErrores()
        ));
    }

    // ---- 4.2 Nómina total por tipo ----
    @Operation(
            summary = "Obtener nómina total por tipo de empleado",
            description = "Devuelve el total de salarios sumados por cada tipo: tiempo completo, por horas y contratistas"
    )
    @ApiResponse(responseCode = "200", description = "Nómina calculada correctamente")
    @GetMapping("/nomina")
    public ResponseEntity<Map<String, BigDecimal>> obtenerNomina() {
        return ok(service.calcularNominaTotal());
    }

    // ---- 4.3 Reporte polimórfico (devuelve DTOs completos) ----
    @Operation(
            summary = "Reporte polimórfico de empleados",
            description = "Combina todos los tipos de empleados y devuelve información completa, impuestos y validez"
    )
    @ApiResponse(responseCode = "200", description = "Reporte generado correctamente")
    @GetMapping("/reporte")
    public ResponseEntity<List<ReporteEmpleadoDto>> reporte() {
        List<ReporteEmpleadoDto> out = new ArrayList<>();

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
        return ok(out);
    }

    // ---- Nómina extendida con días de vacaciones/permisos ----
    @Operation(
            summary = "Nómina extendida con días",
            description = "Incluye la nómina económica y un resumen de días solicitados por los gerentes"
    )
    @ApiResponse(responseCode = "200", description = "Nómina extendida generada correctamente")
    @GetMapping("/nomina-con-dias")
    public ResponseEntity<NominaConDiasResponseDto> obtenerNominaConDias() {
        var nominaPorTipo = service.calcularNominaTotal();
        var gerentes = grRepo.findAll();
        int totalDias = NominaUtils.calcularTotalDiasSolicitados(gerentes);
        var reporteGerentes = NominaUtils.generarReporteEmpleadosConMasDe(gerentes, 20);

        NominaConDiasResponseDto dto = new NominaConDiasResponseDto(
                nominaPorTipo,
                totalDias,
                reporteGerentes
        );

        return ok(dto);
    }
}
