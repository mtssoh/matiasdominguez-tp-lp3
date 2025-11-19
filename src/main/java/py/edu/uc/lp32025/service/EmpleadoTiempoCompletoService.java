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
import py.edu.uc.lp32025.dto.BatchEmpleadosRequest;
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
    public BatchResultado guardarEmpleadosEnBatch(BatchEmpleadosRequest request) {
        if (request == null || request.estaVacio()) {
            return BatchResultado.vacio();
        }

        List<String> errores = new ArrayList<>();
        int totalProcesados = 0;
        int totalGuardados = 0;

        // 1) Unificar todos los empleados en una sola lista polimórfica
        List<Persona> todos = new ArrayList<>();

        if (request.getEmpleadosTiempoCompleto() != null) {
            todos.addAll(request.getEmpleadosTiempoCompleto());
        }
        if (request.getEmpleadosPorHoras() != null) {
            todos.addAll(request.getEmpleadosPorHoras());
        }
        if (request.getContratistas() != null) {
            todos.addAll(request.getContratistas());
        }

        if (todos.isEmpty()) {
            return BatchResultado.vacio();
        }

        // 2) Procesar en chunks
        for (int i = 0; i < todos.size(); i += CHUNK) {
            int fin = Math.min(i + CHUNK, todos.size());
            List<Persona> chunk = todos.subList(i, fin);

            // Listas separadas para guardar según tipo concreto
            List<EmpleadoTiempoCompleto> etcValidos = new ArrayList<>();
            List<EmpleadoPorHoras> ephValidos = new ArrayList<>();
            List<Contratista> cValidos = new ArrayList<>();

            for (int j = 0; j < chunk.size(); j++) {
                Persona p = chunk.get(j);
                totalProcesados++;

                // Bean Validation (sobre Persona concreta)
                Set<ConstraintViolation<Object>> viols = validator.validate(p);
                if (!viols.isEmpty()) {
                    String msg = viols.stream()
                            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                            .collect(Collectors.joining("; "));
                    errores.add("Chunk " + (i / CHUNK) + " item " + j + ": " + msg);
                    continue;
                }

                // Validaciones específicas polimórficas
                if (!p.validarDatosEspecificos()) {
                    errores.add("Chunk " + (i / CHUNK) + " item " + j + ": validaciones específicas fallidas");
                    continue;
                }

                // Clasificar por tipo concreto para guardar en el repo adecuado
                if (p instanceof EmpleadoTiempoCompleto etc) {
                    etcValidos.add(etc);
                } else if (p instanceof EmpleadoPorHoras eph) {
                    ephValidos.add(eph);
                } else if (p instanceof Contratista c) {
                    cValidos.add(c);
                } else {
                    errores.add("Chunk " + (i / CHUNK) + " item " + j + ": tipo de persona no soportado: " + p.getClass().getSimpleName());
                }
            }

            // 3) Guardar en cada repositorio según el tipo
            int guardadosEnEsteChunk = 0;

            if (!etcValidos.isEmpty()) {
                etcRepo.saveAll(etcValidos);
                guardadosEnEsteChunk += etcValidos.size();
            }
            if (!ephValidos.isEmpty()) {
                ephRepo.saveAll(ephValidos);
                guardadosEnEsteChunk += ephValidos.size();
            }
            if (!cValidos.isEmpty()) {
                cRepo.saveAll(cValidos);
                guardadosEnEsteChunk += cValidos.size();
            }

            if (guardadosEnEsteChunk > 0) {
                em.flush();
                em.clear();
                totalGuardados += guardadosEnEsteChunk;
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
