package com.data.datafacturador.controller.admin;

import com.data.datafacturador.entity.configuracion.Impuesto;
import com.data.datafacturador.entity.configuracion.Retencion;
import com.data.datafacturador.entity.configuracion.SalarioMinimo;
import com.data.datafacturador.entity.referencia.*;
import com.data.datafacturador.entity.nomina.Banco;
import com.data.datafacturador.repository.referencia.*;
import com.data.datafacturador.repository.nomina.BancoRepository;
import com.data.datafacturador.service.ConfiguracionAnualService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/configuracion")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminConfiguracionController {

    private final ConfiguracionAnualService configuracionAnualService;
    private final PorcentajeSaludRepository porcentajeSaludRepository;
    private final PorcentajePensionRepository porcentajePensionRepository;
    private final com.data.datafacturador.repository.configuracion.UvtRepository uvtRepository;

    // -- 12 Referencias Globales Nómina --
    private final TipoContratoRepository tipoContratoRepository;
    private final TipoPeriodoRepository tipoPeriodoRepository;
    private final TipoCargoRepository tipoCargoRepository;
    private final TipoTrabajadorRepository tipoTrabajadorRepository;
    private final SubtipoCotizanteRepository subtipoCotizanteRepository;
    private final FondoSaludRepository fondoSaludRepository;
    private final FondoPensionRepository fondoPensionRepository;
    private final FondoCesantiasRepository fondoCesantiasRepository;
    private final ArlRepository arlRepository;
    private final CategoriaArlRepository categoriaArlRepository;
    private final CajaCompensacionRepository cajaCompensacionRepository;
    private final BancoRepository bancoRepository;

    // -- Tipo Lista Precios: gestionado por sucursal, ver SucursalTipoListaController --

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

    // --- UVT (Global / Modificable por SuperAdmin) ---

    @GetMapping("/uvt")
    public ResponseEntity<List<com.data.datafacturador.entity.configuracion.Uvt>> listarUvts() {
        return ResponseEntity.ok(uvtRepository.findAll());
    }

    @PostMapping("/uvt")
    public ResponseEntity<com.data.datafacturador.entity.configuracion.Uvt> crearUvt(@RequestBody com.data.datafacturador.entity.configuracion.Uvt uvt) {
        return ResponseEntity.ok(configuracionAnualService.guardarUvt(uvt));
    }

    @PutMapping("/uvt/{id}")
    public ResponseEntity<com.data.datafacturador.entity.configuracion.Uvt> actualizarUvt(
            @PathVariable Long id, @RequestBody com.data.datafacturador.entity.configuracion.Uvt actualizado) {
        com.data.datafacturador.entity.configuracion.Uvt existente = uvtRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UVT no encontrada"));
        existente.setAnio(actualizado.getAnio());
        existente.setValorUvt(actualizado.getValorUvt());
        existente.setTopeUvt(actualizado.getTopeUvt());
        return ResponseEntity.ok(uvtRepository.save(existente));
    }

    @DeleteMapping("/uvt/{id}")
    public ResponseEntity<Void> eliminarUvt(@PathVariable Long id) {
        uvtRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // Referencias Globales de Nómina (empresa_id = NULL)
    // =========================================================

    // --- Tipos de Contrato ---
    @GetMapping("/referencias/tipos-contrato")
    public ResponseEntity<List<TipoContrato>> listarTiposContrato() {
        return ResponseEntity.ok(tipoContratoRepository.findAll());
    }
    @PostMapping("/referencias/tipos-contrato")
    public ResponseEntity<TipoContrato> crearTipoContrato(@RequestBody Map<String, String> body) {
        String codigo = body.get("codigo");
        if (codigo != null && tipoContratoRepository.existsByCodigo(codigo)) {
            throw new IllegalArgumentException("El código DIAN '" + codigo + "' ya existe");
        }
        TipoContrato e = new TipoContrato();
        e.setNombre(body.get("nombre")); e.setCodigo(codigo); e.setEmpresaId(null);
        return ResponseEntity.ok(tipoContratoRepository.save(e));
    }
    @PutMapping("/referencias/tipos-contrato/{id}")
    public ResponseEntity<TipoContrato> editarTipoContrato(@PathVariable Long id, @RequestBody Map<String, String> body) {
        TipoContrato e = tipoContratoRepository.findById(id).orElseThrow();
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        if (body.containsKey("codigo")) {
            String c = body.get("codigo");
            if (!c.equals(e.getCodigo()) && tipoContratoRepository.existsByCodigo(c))
                throw new IllegalArgumentException("Código DIAN '" + c + "' ya existe");
            e.setCodigo(c);
        }
        return ResponseEntity.ok(tipoContratoRepository.save(e));
    }
    @DeleteMapping("/referencias/tipos-contrato/{id}")
    public ResponseEntity<Void> eliminarTipoContrato(@PathVariable Long id) {
        tipoContratoRepository.deleteById(id); return ResponseEntity.noContent().build();
    }

    // --- Tipos de Período ---
    @GetMapping("/referencias/tipos-periodo")
    public ResponseEntity<List<TipoPeriodo>> listarTiposPeriodo() {
        return ResponseEntity.ok(tipoPeriodoRepository.findAll());
    }
    @PostMapping("/referencias/tipos-periodo")
    public ResponseEntity<TipoPeriodo> crearTipoPeriodo(@RequestBody Map<String, String> body) {
        TipoPeriodo e = new TipoPeriodo(); e.setNombre(body.get("nombre")); e.setEmpresaId(null);
        return ResponseEntity.ok(tipoPeriodoRepository.save(e));
    }
    @PutMapping("/referencias/tipos-periodo/{id}")
    public ResponseEntity<TipoPeriodo> editarTipoPeriodo(@PathVariable Long id, @RequestBody Map<String, String> body) {
        TipoPeriodo e = tipoPeriodoRepository.findById(id).orElseThrow();
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        return ResponseEntity.ok(tipoPeriodoRepository.save(e));
    }
    @DeleteMapping("/referencias/tipos-periodo/{id}")
    public ResponseEntity<Void> eliminarTipoPeriodo(@PathVariable Long id) {
        tipoPeriodoRepository.deleteById(id); return ResponseEntity.noContent().build();
    }

    // --- Cargos ---
    @GetMapping("/referencias/cargos")
    public ResponseEntity<List<TipoCargo>> listarCargos() {
        return ResponseEntity.ok(tipoCargoRepository.findAll());
    }
    @PostMapping("/referencias/cargos")
    public ResponseEntity<TipoCargo> crearCargo(@RequestBody Map<String, String> body) {
        TipoCargo e = new TipoCargo(); e.setNombre(body.get("nombre")); e.setEmpresaId(null);
        return ResponseEntity.ok(tipoCargoRepository.save(e));
    }
    @PutMapping("/referencias/cargos/{id}")
    public ResponseEntity<TipoCargo> editarCargo(@PathVariable Long id, @RequestBody Map<String, String> body) {
        TipoCargo e = tipoCargoRepository.findById(id).orElseThrow();
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        return ResponseEntity.ok(tipoCargoRepository.save(e));
    }
    @DeleteMapping("/referencias/cargos/{id}")
    public ResponseEntity<Void> eliminarCargo(@PathVariable Long id) {
        tipoCargoRepository.deleteById(id); return ResponseEntity.noContent().build();
    }

    // --- Tipos de Trabajador ---
    @GetMapping("/referencias/tipos-trabajador")
    public ResponseEntity<List<TipoTrabajador>> listarTiposTrabajador() {
        return ResponseEntity.ok(tipoTrabajadorRepository.findAll());
    }
    @PostMapping("/referencias/tipos-trabajador")
    public ResponseEntity<TipoTrabajador> crearTipoTrabajador(@RequestBody Map<String, String> body) {
        String codigo = body.get("codigo");
        if (codigo != null && tipoTrabajadorRepository.existsByCodigo(codigo))
            throw new IllegalArgumentException("Código DIAN '" + codigo + "' ya existe");
        TipoTrabajador e = new TipoTrabajador(); e.setNombre(body.get("nombre")); e.setCodigo(codigo); e.setEmpresaId(null);
        return ResponseEntity.ok(tipoTrabajadorRepository.save(e));
    }
    @PutMapping("/referencias/tipos-trabajador/{id}")
    public ResponseEntity<TipoTrabajador> editarTipoTrabajador(@PathVariable Long id, @RequestBody Map<String, String> body) {
        TipoTrabajador e = tipoTrabajadorRepository.findById(id).orElseThrow();
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        if (body.containsKey("codigo")) {
            String c = body.get("codigo");
            if (!c.equals(e.getCodigo()) && tipoTrabajadorRepository.existsByCodigo(c))
                throw new IllegalArgumentException("Código DIAN '" + c + "' ya existe");
            e.setCodigo(c);
        }
        return ResponseEntity.ok(tipoTrabajadorRepository.save(e));
    }
    @DeleteMapping("/referencias/tipos-trabajador/{id}")
    public ResponseEntity<Void> eliminarTipoTrabajador(@PathVariable Long id) {
        tipoTrabajadorRepository.deleteById(id); return ResponseEntity.noContent().build();
    }

    // --- Subtipos Cotizante ---
    @GetMapping("/referencias/subtipos-cotizante")
    public ResponseEntity<List<SubtipoCotizante>> listarSubtiposCotizante() {
        return ResponseEntity.ok(subtipoCotizanteRepository.findAll());
    }
    @PostMapping("/referencias/subtipos-cotizante")
    public ResponseEntity<SubtipoCotizante> crearSubtipoCotizante(@RequestBody Map<String, String> body) {
        SubtipoCotizante e = new SubtipoCotizante(); e.setNombre(body.get("nombre")); e.setEmpresaId(null);
        return ResponseEntity.ok(subtipoCotizanteRepository.save(e));
    }
    @PutMapping("/referencias/subtipos-cotizante/{id}")
    public ResponseEntity<SubtipoCotizante> editarSubtipoCotizante(@PathVariable Long id, @RequestBody Map<String, String> body) {
        SubtipoCotizante e = subtipoCotizanteRepository.findById(id).orElseThrow();
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        return ResponseEntity.ok(subtipoCotizanteRepository.save(e));
    }
    @DeleteMapping("/referencias/subtipos-cotizante/{id}")
    public ResponseEntity<Void> eliminarSubtipoCotizante(@PathVariable Long id) {
        subtipoCotizanteRepository.deleteById(id); return ResponseEntity.noContent().build();
    }

    // --- Fondos de Salud (EPS) ---
    @GetMapping("/referencias/fondos-salud")
    public ResponseEntity<List<FondoSalud>> listarFondosSalud() {
        return ResponseEntity.ok(fondoSaludRepository.findAll());
    }
    @PostMapping("/referencias/fondos-salud")
    public ResponseEntity<FondoSalud> crearFondoSalud(@RequestBody Map<String, String> body) {
        FondoSalud e = new FondoSalud(); e.setNombre(body.get("nombre")); e.setEmpresaId(null);
        return ResponseEntity.ok(fondoSaludRepository.save(e));
    }
    @PutMapping("/referencias/fondos-salud/{id}")
    public ResponseEntity<FondoSalud> editarFondoSalud(@PathVariable Long id, @RequestBody Map<String, String> body) {
        FondoSalud e = fondoSaludRepository.findById(id).orElseThrow();
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        return ResponseEntity.ok(fondoSaludRepository.save(e));
    }
    @DeleteMapping("/referencias/fondos-salud/{id}")
    public ResponseEntity<Void> eliminarFondoSalud(@PathVariable Long id) {
        fondoSaludRepository.deleteById(id); return ResponseEntity.noContent().build();
    }

    // --- Fondos de Pensión (AFP) ---
    @GetMapping("/referencias/fondos-pension")
    public ResponseEntity<List<FondoPension>> listarFondosPension() {
        return ResponseEntity.ok(fondoPensionRepository.findAll());
    }
    @PostMapping("/referencias/fondos-pension")
    public ResponseEntity<FondoPension> crearFondoPension(@RequestBody Map<String, String> body) {
        FondoPension e = new FondoPension(); e.setNombre(body.get("nombre")); e.setEmpresaId(null);
        return ResponseEntity.ok(fondoPensionRepository.save(e));
    }
    @PutMapping("/referencias/fondos-pension/{id}")
    public ResponseEntity<FondoPension> editarFondoPension(@PathVariable Long id, @RequestBody Map<String, String> body) {
        FondoPension e = fondoPensionRepository.findById(id).orElseThrow();
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        return ResponseEntity.ok(fondoPensionRepository.save(e));
    }
    @DeleteMapping("/referencias/fondos-pension/{id}")
    public ResponseEntity<Void> eliminarFondoPension(@PathVariable Long id) {
        fondoPensionRepository.deleteById(id); return ResponseEntity.noContent().build();
    }

    // --- Fondos de Cesantías ---
    @GetMapping("/referencias/fondos-cesantias")
    public ResponseEntity<List<FondoCesantias>> listarFondosCesantias() {
        return ResponseEntity.ok(fondoCesantiasRepository.findAll());
    }
    @PostMapping("/referencias/fondos-cesantias")
    public ResponseEntity<FondoCesantias> crearFondoCesantias(@RequestBody Map<String, String> body) {
        FondoCesantias e = new FondoCesantias(); e.setNombre(body.get("nombre")); e.setEmpresaId(null);
        return ResponseEntity.ok(fondoCesantiasRepository.save(e));
    }
    @PutMapping("/referencias/fondos-cesantias/{id}")
    public ResponseEntity<FondoCesantias> editarFondoCesantias(@PathVariable Long id, @RequestBody Map<String, String> body) {
        FondoCesantias e = fondoCesantiasRepository.findById(id).orElseThrow();
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        return ResponseEntity.ok(fondoCesantiasRepository.save(e));
    }
    @DeleteMapping("/referencias/fondos-cesantias/{id}")
    public ResponseEntity<Void> eliminarFondoCesantias(@PathVariable Long id) {
        fondoCesantiasRepository.deleteById(id); return ResponseEntity.noContent().build();
    }

    // --- ARL ---
    @GetMapping("/referencias/arl")
    public ResponseEntity<List<Arl>> listarArls() {
        return ResponseEntity.ok(arlRepository.findAll());
    }
    @PostMapping("/referencias/arl")
    public ResponseEntity<Arl> crearArl(@RequestBody Map<String, String> body) {
        Arl e = new Arl(); e.setNombre(body.get("nombre")); e.setEmpresaId(null);
        return ResponseEntity.ok(arlRepository.save(e));
    }
    @PutMapping("/referencias/arl/{id}")
    public ResponseEntity<Arl> editarArl(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Arl e = arlRepository.findById(id).orElseThrow();
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        return ResponseEntity.ok(arlRepository.save(e));
    }
    @DeleteMapping("/referencias/arl/{id}")
    public ResponseEntity<Void> eliminarArl(@PathVariable Long id) {
        arlRepository.deleteById(id); return ResponseEntity.noContent().build();
    }

    // --- Categorías ARL ---
    @GetMapping("/referencias/categorias-arl")
    public ResponseEntity<List<CategoriaArl>> listarCategoriasArl() {
        return ResponseEntity.ok(categoriaArlRepository.findAll());
    }
    @PostMapping("/referencias/categorias-arl")
    public ResponseEntity<CategoriaArl> crearCategoriaArl(@RequestBody Map<String, String> body) {
        CategoriaArl e = new CategoriaArl(); e.setNombre(body.get("nombre")); e.setEmpresaId(null);
        return ResponseEntity.ok(categoriaArlRepository.save(e));
    }
    @PutMapping("/referencias/categorias-arl/{id}")
    public ResponseEntity<CategoriaArl> editarCategoriaArl(@PathVariable Long id, @RequestBody Map<String, String> body) {
        CategoriaArl e = categoriaArlRepository.findById(id).orElseThrow();
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        return ResponseEntity.ok(categoriaArlRepository.save(e));
    }
    @DeleteMapping("/referencias/categorias-arl/{id}")
    public ResponseEntity<Void> eliminarCategoriaArl(@PathVariable Long id) {
        categoriaArlRepository.deleteById(id); return ResponseEntity.noContent().build();
    }

    // --- Cajas de Compensación ---
    @GetMapping("/referencias/cajas-compensacion")
    public ResponseEntity<List<CajaCompensacion>> listarCajasCompensacion() {
        return ResponseEntity.ok(cajaCompensacionRepository.findAll());
    }
    @PostMapping("/referencias/cajas-compensacion")
    public ResponseEntity<CajaCompensacion> crearCajaCompensacion(@RequestBody Map<String, String> body) {
        CajaCompensacion e = new CajaCompensacion(); e.setNombre(body.get("nombre")); e.setEmpresaId(null);
        return ResponseEntity.ok(cajaCompensacionRepository.save(e));
    }
    @PutMapping("/referencias/cajas-compensacion/{id}")
    public ResponseEntity<CajaCompensacion> editarCajaCompensacion(@PathVariable Long id, @RequestBody Map<String, String> body) {
        CajaCompensacion e = cajaCompensacionRepository.findById(id).orElseThrow();
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        return ResponseEntity.ok(cajaCompensacionRepository.save(e));
    }
    @DeleteMapping("/referencias/cajas-compensacion/{id}")
    public ResponseEntity<Void> eliminarCajaCompensacion(@PathVariable Long id) {
        cajaCompensacionRepository.deleteById(id); return ResponseEntity.noContent().build();
    }

    // --- Bancos ---
    @GetMapping("/referencias/bancos")
    public ResponseEntity<List<Banco>> listarBancos() {
        return ResponseEntity.ok(bancoRepository.findAll());
    }
    @PostMapping("/referencias/bancos")
    public ResponseEntity<Banco> crearBanco(@RequestBody Map<String, String> body) {
        Banco e = new Banco(); e.setNombre(body.get("nombre")); e.setEmpresaId(null);
        return ResponseEntity.ok(bancoRepository.save(e));
    }
    @PutMapping("/referencias/bancos/{id}")
    public ResponseEntity<Banco> editarBanco(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Banco e = bancoRepository.findById(id).orElseThrow();
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        return ResponseEntity.ok(bancoRepository.save(e));
    }
    @DeleteMapping("/referencias/bancos/{id}")
    public ResponseEntity<Void> eliminarBanco(@PathVariable Long id) {
        bancoRepository.deleteById(id); return ResponseEntity.noContent().build();
    }

}

