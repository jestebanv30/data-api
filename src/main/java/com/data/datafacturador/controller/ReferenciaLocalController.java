package com.data.datafacturador.controller;

import com.data.datafacturador.dto.ApiResponse;
import com.data.datafacturador.entity.referencia.*;
import com.data.datafacturador.repository.referencia.*;
import com.data.datafacturador.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador para gestión de referencias locales por empresa.
 * Permite a administradores crear/eliminar opciones propias en listas de referencia.
 * Rol: ADMIN o SUPER_ADMIN
 */
@Slf4j
@RestController
@RequestMapping("/api/referencias-locales")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class ReferenciaLocalController {

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
    private final CentroCostoRepository centroCostoRepository;
    private final PorcentajeSaludRepository porcentajeSaludRepository;
    private final PorcentajePensionRepository porcentajePensionRepository;
    private final com.data.datafacturador.repository.nomina.BancoRepository bancoRepository;
    private final UsuarioService usuarioService;

    // ====================================================================
    // TIPO CONTRATO
    // ====================================================================
    @PostMapping("/tipos-contrato")
    public ResponseEntity<ApiResponse<TipoContrato>> crearTipoContrato(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        String codigo = body.get("codigo");
        if (codigo != null && tipoContratoRepository.existsByCodigo(codigo)) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("El código DIAN '" + codigo + "' ya existe en el sistema"));
        }
        TipoContrato entity = new TipoContrato();
        entity.setNombre(body.get("nombre"));
        entity.setCodigo(codigo);
        entity.setEmpresaId(empresaId);
        return ok(tipoContratoRepository.save(entity), "Tipo de contrato creado");
    }

    @DeleteMapping("/tipos-contrato/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarTipoContrato(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(tipoContratoRepository, id, getEmpresaId(auth), "Tipo de contrato");
    }

    // ====================================================================
    // TIPO PERIODO
    // ====================================================================
    @PostMapping("/tipos-periodo")
    public ResponseEntity<ApiResponse<TipoPeriodo>> crearTipoPeriodo(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        TipoPeriodo entity = new TipoPeriodo();
        entity.setNombre(body.get("nombre"));
        entity.setEmpresaId(empresaId);
        return ok(tipoPeriodoRepository.save(entity), "Tipo de periodo creado");
    }

    @DeleteMapping("/tipos-periodo/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarTipoPeriodo(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(tipoPeriodoRepository, id, getEmpresaId(auth), "Tipo de periodo");
    }

    // ====================================================================
    // CARGO
    // ====================================================================
    @PostMapping("/cargos")
    public ResponseEntity<ApiResponse<TipoCargo>> crearCargo(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        TipoCargo entity = new TipoCargo();
        entity.setNombre(body.get("nombre"));
        entity.setEmpresaId(empresaId);
        return ok(tipoCargoRepository.save(entity), "Cargo creado");
    }

    @DeleteMapping("/cargos/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCargo(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(tipoCargoRepository, id, getEmpresaId(auth), "Cargo");
    }

    // ====================================================================
    // TIPO TRABAJADOR
    // ====================================================================
    @PostMapping("/tipos-trabajador")
    public ResponseEntity<ApiResponse<TipoTrabajador>> crearTipoTrabajador(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        String codigo = body.get("codigo");
        if (codigo != null && tipoTrabajadorRepository.existsByCodigo(codigo)) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("El código DIAN '" + codigo + "' ya existe en el sistema"));
        }
        TipoTrabajador entity = new TipoTrabajador();
        entity.setNombre(body.get("nombre"));
        entity.setCodigo(codigo);
        entity.setEmpresaId(empresaId);
        return ok(tipoTrabajadorRepository.save(entity), "Tipo de trabajador creado");
    }

    @DeleteMapping("/tipos-trabajador/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarTipoTrabajador(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(tipoTrabajadorRepository, id, getEmpresaId(auth), "Tipo de trabajador");
    }

    // ====================================================================
    // SUBTIPO COTIZANTE
    // ====================================================================
    @PostMapping("/subtipos-cotizante")
    public ResponseEntity<ApiResponse<SubtipoCotizante>> crearSubtipoCotizante(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        SubtipoCotizante entity = new SubtipoCotizante();
        entity.setNombre(body.get("nombre"));
        entity.setEmpresaId(empresaId);
        return ok(subtipoCotizanteRepository.save(entity), "Subtipo cotizante creado");
    }

    @DeleteMapping("/subtipos-cotizante/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarSubtipoCotizante(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(subtipoCotizanteRepository, id, getEmpresaId(auth), "Subtipo cotizante");
    }

    // ====================================================================
    // FONDO SALUD
    // ====================================================================
    @PostMapping("/fondos-salud")
    public ResponseEntity<ApiResponse<FondoSalud>> crearFondoSalud(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        FondoSalud entity = new FondoSalud();
        entity.setNombre(body.get("nombre"));
        entity.setEmpresaId(empresaId);
        return ok(fondoSaludRepository.save(entity), "Fondo de salud creado");
    }

    @DeleteMapping("/fondos-salud/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarFondoSalud(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(fondoSaludRepository, id, getEmpresaId(auth), "Fondo de salud");
    }

    // ====================================================================
    // FONDO PENSION
    // ====================================================================
    @PostMapping("/fondos-pension")
    public ResponseEntity<ApiResponse<FondoPension>> crearFondoPension(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        FondoPension entity = new FondoPension();
        entity.setNombre(body.get("nombre"));
        entity.setEmpresaId(empresaId);
        return ok(fondoPensionRepository.save(entity), "Fondo de pensión creado");
    }

    @DeleteMapping("/fondos-pension/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarFondoPension(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(fondoPensionRepository, id, getEmpresaId(auth), "Fondo de pensión");
    }

    // ====================================================================
    // FONDO CESANTIAS
    // ====================================================================
    @PostMapping("/fondos-cesantias")
    public ResponseEntity<ApiResponse<FondoCesantias>> crearFondoCesantias(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        FondoCesantias entity = new FondoCesantias();
        entity.setNombre(body.get("nombre"));
        entity.setEmpresaId(empresaId);
        return ok(fondoCesantiasRepository.save(entity), "Fondo de cesantías creado");
    }

    @DeleteMapping("/fondos-cesantias/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarFondoCesantias(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(fondoCesantiasRepository, id, getEmpresaId(auth), "Fondo de cesantías");
    }

    // ====================================================================
    // ARL
    // ====================================================================
    @PostMapping("/arl")
    public ResponseEntity<ApiResponse<Arl>> crearArl(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        Arl entity = new Arl();
        entity.setNombre(body.get("nombre"));
        entity.setEmpresaId(empresaId);
        return ok(arlRepository.save(entity), "ARL creada");
    }

    @DeleteMapping("/arl/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarArl(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(arlRepository, id, getEmpresaId(auth), "ARL");
    }

    // ====================================================================
    // CATEGORIA ARL
    // ====================================================================
    @PostMapping("/categorias-arl")
    public ResponseEntity<ApiResponse<CategoriaArl>> crearCategoriaArl(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        CategoriaArl entity = new CategoriaArl();
        entity.setNombre(body.get("nombre"));
        entity.setEmpresaId(empresaId);
        return ok(categoriaArlRepository.save(entity), "Categoría ARL creada");
    }

    @DeleteMapping("/categorias-arl/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCategoriaArl(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(categoriaArlRepository, id, getEmpresaId(auth), "Categoría ARL");
    }

    // ====================================================================
    // CAJA COMPENSACION
    // ====================================================================
    @PostMapping("/cajas-compensacion")
    public ResponseEntity<ApiResponse<CajaCompensacion>> crearCajaCompensacion(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        CajaCompensacion entity = new CajaCompensacion();
        entity.setNombre(body.get("nombre"));
        entity.setEmpresaId(empresaId);
        return ok(cajaCompensacionRepository.save(entity), "Caja de compensación creada");
    }

    @DeleteMapping("/cajas-compensacion/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCajaCompensacion(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(cajaCompensacionRepository, id, getEmpresaId(auth), "Caja de compensación");
    }

    // ====================================================================
    // BANCO (ya tiene empresa_id)
    // ====================================================================
    @PostMapping("/bancos")
    public ResponseEntity<ApiResponse<com.data.datafacturador.entity.nomina.Banco>> crearBanco(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        com.data.datafacturador.entity.nomina.Banco entity = new com.data.datafacturador.entity.nomina.Banco();
        entity.setNombre(body.get("nombre"));
        entity.setEmpresaId(empresaId);
        return ok(bancoRepository.save(entity), "Banco creado");
    }

    @DeleteMapping("/bancos/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarBanco(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(bancoRepository, id, getEmpresaId(auth), "Banco");
    }

    // ====================================================================
    // CENTRO DE COSTO
    // ====================================================================
    @PostMapping("/centros-costo")
    public ResponseEntity<ApiResponse<CentroCosto>> crearCentroCosto(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        CentroCosto entity = new CentroCosto();
        entity.setNombre(body.get("nombre"));
        entity.setEmpresaId(empresaId);
        return ok(centroCostoRepository.save(entity), "Centro de costo creado");
    }

    @DeleteMapping("/centros-costo/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCentroCosto(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(centroCostoRepository, id, getEmpresaId(auth), "Centro de costo");
    }

    // ====================================================================
    // PORCENTAJE SALUD
    // ====================================================================
    @PostMapping("/porcentajes-salud")
    public ResponseEntity<ApiResponse<PorcentajeSalud>> crearPorcentajeSalud(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        PorcentajeSalud entity = new PorcentajeSalud();
        entity.setPorcentaje(new java.math.BigDecimal(body.get("porcentaje")));
        entity.setDescripcion(body.get("descripcion"));
        entity.setEmpresaId(empresaId);
        return ok(porcentajeSaludRepository.save(entity), "Porcentaje salud creado");
    }

    @DeleteMapping("/porcentajes-salud/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarPorcentajeSalud(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(porcentajeSaludRepository, id, getEmpresaId(auth), "Porcentaje salud");
    }

    // ====================================================================
    // PORCENTAJE PENSION
    // ====================================================================
    @PostMapping("/porcentajes-pension")
    public ResponseEntity<ApiResponse<PorcentajePension>> crearPorcentajePension(
            @RequestBody Map<String, String> body, Authentication auth) {
        Integer empresaId = getEmpresaId(auth);
        PorcentajePension entity = new PorcentajePension();
        entity.setPorcentaje(new java.math.BigDecimal(body.get("porcentaje")));
        entity.setDescripcion(body.get("descripcion"));
        entity.setEmpresaId(empresaId);
        return ok(porcentajePensionRepository.save(entity), "Porcentaje pensión creado");
    }

    @DeleteMapping("/porcentajes-pension/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarPorcentajePension(@PathVariable Long id, Authentication auth) {
        return eliminarLocal(porcentajePensionRepository, id, getEmpresaId(auth), "Porcentaje pensión");
    }

    // ====================================================================
    // HELPERS
    // ====================================================================

    private <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    /**
     * Elimina un registro local (solo si pertenece a la empresa del usuario).
     * Los registros globales (empresa_id = NULL) NO pueden ser eliminados por admins.
     */
    private <T> ResponseEntity<ApiResponse<Void>> eliminarLocal(
            org.springframework.data.jpa.repository.JpaRepository<T, Long> repo,
            Long id, Integer empresaId, String tipo) {
        
        @SuppressWarnings("unchecked")
        T entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException(tipo + " no encontrado: " + id));

        // Verificar que es un registro local de la empresa del usuario
        try {
            Integer entityEmpresaId = (Integer) entity.getClass().getMethod("getEmpresaId").invoke(entity);
            if (entityEmpresaId == null) {
                throw new com.data.datafacturador.exception.AccesoDenegadoException(
                        "No se puede eliminar un registro global de " + tipo);
            }
            if (!entityEmpresaId.equals(empresaId)) {
                throw new com.data.datafacturador.exception.AccesoDenegadoException(
                        tipo + " no pertenece a su empresa");
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error verificando propiedad del registro", e);
        }

        repo.deleteById(id);
        log.info("{} local eliminado: {} (empresa: {})", tipo, id, empresaId);
        return ResponseEntity.ok(ApiResponse.success(null, tipo + " eliminado exitosamente"));
    }

    private Integer getEmpresaId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            Long empresaIdLong = (Long) jwt.getClaims().get("empresaId");
            return empresaIdLong != null ? empresaIdLong.intValue() : null;
        }
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            return usuarioService.buscarPorUsername(userDetails.getUsername()).getEmpresaId();
        }
        throw new IllegalStateException("Tipo de autenticación no soportado");
    }
}
