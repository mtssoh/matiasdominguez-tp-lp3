package py.edu.uc.lp32025.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

import py.edu.uc.lp32025.domain.Contratista;
import py.edu.uc.lp32025.domain.EmpleadoPorHoras;
import py.edu.uc.lp32025.domain.EmpleadoTiempoCompleto;
import py.edu.uc.lp32025.domain.Persona;
import py.edu.uc.lp32025.repository.ContratistaRepository;
import py.edu.uc.lp32025.repository.EmpleadoPorHorasRepository;
import py.edu.uc.lp32025.repository.EmpleadoTiempoCompletoRepository;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmpleadoTiempoCompletoService {

    private static final int CHUNK = 100;

    private final EmpleadoTiempoCompletoRepository etcRepo;
    private final EmpleadoPorHorasRepository ephRepo;
    private final ContratistaRepository cRepo;
    private final EntityManager em;
    private final Validator validator;

    public EmpleadoTiempoCompletoService(EmpleadoTiempoCompletoRepository etcRepo,
                                         EmpleadoPorHorasRepository ephRepo,
                                         ContratistaRepository cRepo,
                                         EntityManager em,
                                         Validator validator) {
        this.etcRepo = etcRepo;
        this.ephRepo = ephRepo;
        this.cRepo = cRepo;
        this.em = em;
        this.validator = validator;
    }

    // 4.1 Persistencia en Batch (chunks de 100, Bean Validation + validaciones específicas por tipo)
    @Transactional
    public BatchResultado guardarEmpleadosEnBatch(List<EmpleadoTiempoCompleto> empleados) {
        if (empleados == null || empleados.isEmpty()) return BatchResultado.vacio();

        List<String> errores = new ArrayList<>();
        int totalProcesados = 0;
        int totalGuardados = 0;

        for (int i = 0; i < empleados.size(); i += CHUNK) {
            int fin = Math.min(i + CHUNK, empleados.size());
            List<EmpleadoTiempoCompleto> chunk = empleados.subList(i, fin);

            List<EmpleadoTiempoCompleto> validos = new ArrayList<>();
            for (int j = 0; j < chunk.size(); j++) {
                EmpleadoTiempoCompleto e = chunk.get(j);
                totalProcesados++;

                // Bean Validation
                Set<ConstraintViolation<EmpleadoTiempoCompleto>> viols = validator.validate(e);
                if (!viols.isEmpty()) {
                    String msg = viols.stream()
                            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                            .collect(Collectors.joining("; "));
                    errores.add("Chunk " + (i/CHUNK) + " item " + j + ": " + msg);
                    continue;
                }

                // Validaciones específicas (polimorfismo vía método de la superclase)
                if (!e.validarDatosEspecificos()) {
                    errores.add("Chunk " + (i/CHUNK) + " item " + j + ": validaciones específicas fallidas");
                    continue;
                }

                validos.add(e);
            }

            if (!validos.isEmpty()) {
                etcRepo.saveAll(validos);
                // Optimización de memoria/flush por lote
                em.flush();
                em.clear();
                totalGuardados += validos.size();
            }
        }

        return new BatchResultado(totalProcesados, totalGuardados, errores);
    }


    @Transactional
    public Map<String, BigDecimal> calcularNominaTotal() {
        Map<String, BigDecimal> res = new LinkedHashMap<>();

        BigDecimal totalETC = etcRepo.findAll().stream()
                .map(Persona::calcularSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        res.put(EmpleadoTiempoCompleto.class.getSimpleName(), totalETC);

        BigDecimal totalEPH = ephRepo.findAll().stream()
                .map(Persona::calcularSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        res.put(EmpleadoPorHoras.class.getSimpleName(), totalEPH);

        BigDecimal totalC = cRepo.findAll().stream()
                .map(Persona::calcularSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        res.put(Contratista.class.getSimpleName(), totalC);

        return res;
    }


    @Transactional
    public List<String> generarReporteCompleto() {
        List<String> lineas = new ArrayList<>();

        List<? extends Persona> todos = new ArrayList<Persona>() {{
            addAll(etcRepo.findAll());
            addAll(ephRepo.findAll());
            addAll(cRepo.findAll());
        }};

        for (Persona p : todos) {
            String info = p.obtenerInformacionCompleta();
            boolean valido = p.validarDatosEspecificos();
            BigDecimal impuestos = p.calcularImpuestos();

            lineas.add(String.format("[%s] %s | Valido: %s | Impuestos: %s",
                    p.getClass().getSimpleName(), info, (valido ? "Sí" : "No"), impuestos));
        }
        return lineas;
    }


    public static class BatchResultado {
        private final int totalProcesados;
        private final int totalGuardados;
        private final List<String> errores;

        public BatchResultado(int totalProcesados, int totalGuardados, List<String> errores) {
            this.totalProcesados = totalProcesados;
            this.totalGuardados = totalGuardados;
            this.errores = errores;
        }
        public static BatchResultado vacio() { return new BatchResultado(0, 0, List.of()); }

        public int getTotalProcesados() { return totalProcesados; }
        public int getTotalGuardados() { return totalGuardados; }
        public List<String> getErrores() { return errores; }
    }
}
