package com.data.datafacturador.controller.admin;

import com.data.datafacturador.entity.configuracion.Impuesto;
import com.data.datafacturador.entity.configuracion.Retencion;
import com.data.datafacturador.entity.configuracion.SalarioMinimo;
import com.data.datafacturador.service.ConfiguracionAnualService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/configuracion")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminConfiguracionController {

    private final ConfiguracionAnualService configuracionAnualService;
    private final com.data.datafacturador.repository.referencia.PorcentajeSaludRepository porcentajeSaludRepository;
    private final com.data.datafacturador.repository.referencia.PorcentajePensionRepository porcentajePensionRepository;

    // --- Retenciones (Globales + Locales) ---

    @GetMapping("/retenciones")
    public ResponseEntity<List<Retencion>> listarRetenciones(
            @RequestParam(required = false) Integer empresaId) {
        // Super Admin puede listar las de una empresa específica O (si es null) las globales
        // Usamos la misma lógica del servicio que trae Globales + Esa empresa
        // Si no envía empresaId, traerá las globales (porque null OR null)
        return ResponseEntity.ok(configuracionAnualService.listarRetenciones(empresaId));
    }

    @PostMapping("/retenciones")
    public ResponseEntity<Retencion> crearRetencion(@RequestBody Retencion retencion) {
        // Super Admin puede enviar empresaId en el body.
        // Si retencion.empresaId es null -> Crea GLOBAL.
        // Si retencion.empresaId tiene valor -> Crea para esa empresa.
        // El servicio configuracionAnualService.guardarRetencion respeta el empresaId del objeto si el segundo param es null?
        // Revisemos el servicio:
        // if (empresaId != null) { retencion.setEmpresaId(empresaId); }
        // Si pasamos null en el segundo param, el servicio NO sobrescribe. Perfecto.
        return ResponseEntity.ok(configuracionAnualService.guardarRetencion(retencion, null));
    }

    @PutMapping("/retenciones/{id}")
    public ResponseEntity<Retencion> actualizarRetencion(
            @PathVariable Long id,
            @RequestBody Retencion retencion) {
        // Pasamos null como empresaId 'usuario', y true como isSuperAdmin
        return ResponseEntity.ok(configuracionAnualService.actualizarRetencion(id, retencion, null, true));
    }

    @DeleteMapping("/retenciones/{id}")
    public ResponseEntity<Void> eliminarRetencion(@PathVariable Long id) {
        configuracionAnualService.eliminarRetencion(id, null, true);
        return ResponseEntity.noContent().build();
    }

    // --- Salario Mínimo ---
    
    @GetMapping("/salario-minimo")
    public ResponseEntity<List<SalarioMinimo>> listarSalariosMinimo(
            @RequestParam(required = false) Integer empresaId) {
        return ResponseEntity.ok(configuracionAnualService.listarSalariosMinimos(empresaId));
    }

    @PostMapping("/salario-minimo")
    public ResponseEntity<SalarioMinimo> crearSalarioMinimo(@RequestBody SalarioMinimo salario) {
        return ResponseEntity.ok(configuracionAnualService.guardarSalarioMinimo(salario, null, true));
    }

    @PutMapping("/salario-minimo/{id}")
    public ResponseEntity<SalarioMinimo> actualizarSalarioMinimo(
            @PathVariable Long id,
            @RequestBody SalarioMinimo salario) {
        return ResponseEntity.ok(configuracionAnualService.actualizarSalarioMinimo(id, salario, null, true));
    }
    
    @DeleteMapping("/salario-minimo/{id}")
    public ResponseEntity<Void> eliminarSalarioMinimo(@PathVariable Long id) {
        configuracionAnualService.eliminarSalarioMinimo(id, null, true);
        return ResponseEntity.noContent().build();
    }

    // --- Impuestos (Globales + Locales) ---

    @GetMapping("/impuestos")
    public ResponseEntity<List<Impuesto>> listarImpuestos(
            @RequestParam(required = false) Integer empresaId) {
        return ResponseEntity.ok(configuracionAnualService.listarImpuestos(empresaId));
    }

    @PostMapping("/impuestos")
    public ResponseEntity<Impuesto> crearImpuesto(@RequestBody Impuesto impuesto) {
        return ResponseEntity.ok(configuracionAnualService.guardarImpuesto(impuesto, null));
    }

    @PutMapping("/impuestos/{id}")
    public ResponseEntity<Impuesto> actualizarImpuesto(
            @PathVariable Long id,
            @RequestBody Impuesto impuesto) {
        return ResponseEntity.ok(configuracionAnualService.actualizarImpuesto(id, impuesto, null, true));
    }

    @DeleteMapping("/impuestos/{id}")
    public ResponseEntity<Void> eliminarImpuesto(@PathVariable Long id) {
        configuracionAnualService.eliminarImpuesto(id, null, true);
        return ResponseEntity.noContent().build();
    }

    // --- Porcentajes Salud (Globales) ---

    @GetMapping("/porcentajes-salud")
    public ResponseEntity<List<com.data.datafacturador.entity.referencia.PorcentajeSalud>> listarPorcentajesSalud() {
        return ResponseEntity.ok(porcentajeSaludRepository.findAll());
    }

    @PostMapping("/porcentajes-salud")
    public ResponseEntity<com.data.datafacturador.entity.referencia.PorcentajeSalud> crearPorcentajeSalud(
            @RequestBody com.data.datafacturador.entity.referencia.PorcentajeSalud porcentaje) {
        // Super Admin crea como global (empresa_id = null)
        porcentaje.setEmpresaId(null);
        return ResponseEntity.ok(porcentajeSaludRepository.save(porcentaje));
    }

    @PutMapping("/porcentajes-salud/{id}")
    public ResponseEntity<com.data.datafacturador.entity.referencia.PorcentajeSalud> actualizarPorcentajeSalud(
            @PathVariable Long id,
            @RequestBody com.data.datafacturador.entity.referencia.PorcentajeSalud actualizado) {
        com.data.datafacturador.entity.referencia.PorcentajeSalud existente = porcentajeSaludRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Porcentaje salud no encontrado"));
        existente.setPorcentaje(actualizado.getPorcentaje());
        existente.setDescripcion(actualizado.getDescripcion());
        return ResponseEntity.ok(porcentajeSaludRepository.save(existente));
    }

    @DeleteMapping("/porcentajes-salud/{id}")
    public ResponseEntity<Void> eliminarPorcentajeSalud(@PathVariable Long id) {
        porcentajeSaludRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Porcentajes Pensión (Globales) ---

    @GetMapping("/porcentajes-pension")
    public ResponseEntity<List<com.data.datafacturador.entity.referencia.PorcentajePension>> listarPorcentajesPension() {
        return ResponseEntity.ok(porcentajePensionRepository.findAll());
    }

    @PostMapping("/porcentajes-pension")
    public ResponseEntity<com.data.datafacturador.entity.referencia.PorcentajePension> crearPorcentajePension(
            @RequestBody com.data.datafacturador.entity.referencia.PorcentajePension porcentaje) {
        porcentaje.setEmpresaId(null);
        return ResponseEntity.ok(porcentajePensionRepository.save(porcentaje));
    }

    @PutMapping("/porcentajes-pension/{id}")
    public ResponseEntity<com.data.datafacturador.entity.referencia.PorcentajePension> actualizarPorcentajePension(
            @PathVariable Long id,
            @RequestBody com.data.datafacturador.entity.referencia.PorcentajePension actualizado) {
        com.data.datafacturador.entity.referencia.PorcentajePension existente = porcentajePensionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Porcentaje pensión no encontrado"));
        existente.setPorcentaje(actualizado.getPorcentaje());
        existente.setDescripcion(actualizado.getDescripcion());
        return ResponseEntity.ok(porcentajePensionRepository.save(existente));
    }

    @DeleteMapping("/porcentajes-pension/{id}")
    public ResponseEntity<Void> eliminarPorcentajePension(@PathVariable Long id) {
        porcentajePensionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
