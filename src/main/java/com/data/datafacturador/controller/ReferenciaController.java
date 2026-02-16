package com.data.datafacturador.controller;

import com.data.datafacturador.dto.ApiResponse;
import com.data.datafacturador.entity.nomina.*;
import com.data.datafacturador.entity.referencia.*;
import com.data.datafacturador.repository.nomina.*;
import com.data.datafacturador.repository.referencia.*;
import com.data.datafacturador.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/referencias")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER')")
public class ReferenciaController {

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
    private final ConceptoRetiroRepository conceptoRetiroRepository;
    private final TipoIdentificacionRepository tipoIdentificacionRepository;
    
    // Legacy / Sucursal References (Restored)
    private final TipoRegimenRepository tipoRegimenRepository;
    private final ResponsabilidadTributariaRepository responsabilidadTributariaRepository;
    private final TipoOrganizacionRepository tipoOrganizacionRepository;
    private final ResponsabilidadFiscalRepository responsabilidadFiscalRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    private final TipoListaPreciosRepository tipoListaPreciosRepository;
    private final PaisRepository paisRepository;
    private final CiudadDepartamentoRepository ciudadDepartamentoRepository;

    // Company specific
    private final BancoRepository bancoRepository;
    private final FormaPagoRepository formaPagoRepository;
    private final MedioPagoRepository medioPagoRepository;
    
    private final UsuarioService usuarioService;

    // Global References
    @GetMapping("/tipos-identificacion")
    public ResponseEntity<ApiResponse<List<TipoIdentificacion>>> listarTiposIdentificacion() {
        return ResponseEntity.ok(ApiResponse.success(tipoIdentificacionRepository.findAll(), "Tipos de identificación listados"));
    }

    @GetMapping("/tipos-contrato")
    public ResponseEntity<ApiResponse<List<TipoContrato>>> listarTiposContrato() {
        return ResponseEntity.ok(ApiResponse.success(tipoContratoRepository.findAll(), "Tipos de contrato listados"));
    }

    @GetMapping("/tipos-periodo")
    public ResponseEntity<ApiResponse<List<TipoPeriodo>>> listarTiposPeriodo() {
        return ResponseEntity.ok(ApiResponse.success(tipoPeriodoRepository.findAll(), "Tipos de periodo listados"));
    }

    @GetMapping("/cargos")
    public ResponseEntity<ApiResponse<List<TipoCargo>>> listarCargos() {
        return ResponseEntity.ok(ApiResponse.success(tipoCargoRepository.findAll(), "Cargos listados"));
    }

    @GetMapping("/tipos-trabajador")
    public ResponseEntity<ApiResponse<List<TipoTrabajador>>> listarTiposTrabajador() {
        return ResponseEntity.ok(ApiResponse.success(tipoTrabajadorRepository.findAll(), "Tipos de trabajador listados"));
    }

    @GetMapping("/subtipos-cotizante")
    public ResponseEntity<ApiResponse<List<SubtipoCotizante>>> listarSubtiposCotizante() {
        return ResponseEntity.ok(ApiResponse.success(subtipoCotizanteRepository.findAll(), "Subtipos cotizante listados"));
    }

    @GetMapping("/fondos-salud")
    public ResponseEntity<ApiResponse<List<FondoSalud>>> listarFondosSalud() {
        return ResponseEntity.ok(ApiResponse.success(fondoSaludRepository.findAll(), "Fondos de salud listados"));
    }

    @GetMapping("/fondos-pension")
    public ResponseEntity<ApiResponse<List<FondoPension>>> listarFondosPension() {
        return ResponseEntity.ok(ApiResponse.success(fondoPensionRepository.findAll(), "Fondos de pensión listados"));
    }

    @GetMapping("/fondos-cesantias")
    public ResponseEntity<ApiResponse<List<FondoCesantias>>> listarFondosCesantias() {
        return ResponseEntity.ok(ApiResponse.success(fondoCesantiasRepository.findAll(), "Fondos de cesantías listados"));
    }

    @GetMapping("/arl")
    public ResponseEntity<ApiResponse<List<Arl>>> listarArl() {
        return ResponseEntity.ok(ApiResponse.success(arlRepository.findAll(), "ARLs listadas"));
    }

    @GetMapping("/categorias-arl")
    public ResponseEntity<ApiResponse<List<CategoriaArl>>> listarCategoriasArl() {
        return ResponseEntity.ok(ApiResponse.success(categoriaArlRepository.findAll(), "Categorías ARL listadas"));
    }

    @GetMapping("/cajas-compensacion")
    public ResponseEntity<ApiResponse<List<CajaCompensacion>>> listarCajasCompensacion() {
        return ResponseEntity.ok(ApiResponse.success(cajaCompensacionRepository.findAll(), "Cajas de compensación listadas"));
    }

    @GetMapping("/conceptos-retiro")
    public ResponseEntity<ApiResponse<List<ConceptoRetiro>>> listarConceptosRetiro() {
        return ResponseEntity.ok(ApiResponse.success(conceptoRetiroRepository.findAll(), "Conceptos de retiro listados"));
    }



    // --- Legacy / Sucursal Endpoints (Restored) ---

    @GetMapping("/tipos-regimen")
    public ResponseEntity<ApiResponse<List<TipoRegimen>>> listarTiposRegimen() {
        return ResponseEntity.ok(ApiResponse.success(tipoRegimenRepository.findAll(), "Tipos de régimen listados"));
    }

    @GetMapping("/responsabilidades-tributarias")
    public ResponseEntity<ApiResponse<List<ResponsabilidadTributaria>>> listarResponsabilidadTributaria() {
        return ResponseEntity.ok(ApiResponse.success(responsabilidadTributariaRepository.findAll(), "Responsabilidades tributarias listadas"));
    }

    @GetMapping("/tipos-organizacion")
    public ResponseEntity<ApiResponse<List<TipoOrganizacion>>> listarTiposOrganizacion() {
        return ResponseEntity.ok(ApiResponse.success(tipoOrganizacionRepository.findAll(), "Tipos de organización listados"));
    }

    @GetMapping("/responsabilidades-fiscales")
    public ResponseEntity<ApiResponse<List<ResponsabilidadFiscal>>> listarResponsabilidadesFiscales() {
        return ResponseEntity.ok(ApiResponse.success(responsabilidadFiscalRepository.findAll(), "Responsabilidades fiscales listadas"));
    }

    @GetMapping("/tipos-documento")
    public ResponseEntity<ApiResponse<List<TipoDocumento>>> listarTiposDocumento() {
        return ResponseEntity.ok(ApiResponse.success(tipoDocumentoRepository.findAll(), "Tipos de documento listados"));
    }
    
    @GetMapping("/tipos-lista-precios")
    public ResponseEntity<ApiResponse<List<TipoListaPrecios>>> listarTiposListaPrecios() {
        return ResponseEntity.ok(ApiResponse.success(tipoListaPreciosRepository.findAll(), "Tipos de lista de precios listados"));
    }

    @GetMapping("/paises")
    public ResponseEntity<ApiResponse<List<Pais>>> listarPaises() {
        return ResponseEntity.ok(ApiResponse.success(paisRepository.findAll(), "Países listados"));
    }
    
    @GetMapping("/ciudades")
    public ResponseEntity<ApiResponse<List<CiudadDepartamento>>> listarCiudades() {
        return ResponseEntity.ok(ApiResponse.success(ciudadDepartamentoRepository.findAll(), "Ciudades listadas"));
    }

    // Company References (Filtered)
    @GetMapping("/bancos")
    public ResponseEntity<ApiResponse<List<Banco>>> listarBancos(Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(ApiResponse.success(bancoRepository.findByEmpresaId(empresaId), "Bancos listados"));
    }

    @GetMapping("/formas-pago")
    public ResponseEntity<ApiResponse<List<FormaPago>>> listarFormasPago(Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(ApiResponse.success(formaPagoRepository.findByEmpresaId(empresaId), "Formas de pago listadas"));
    }

    @GetMapping("/medios-pago")
    public ResponseEntity<ApiResponse<List<MedioPago>>> listarMediosPago(Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(ApiResponse.success(medioPagoRepository.findByEmpresaId(empresaId), "Medios de pago listados"));
    }

    private Integer obtenerEmpresaIdUsuarioAutenticado(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        
        // Caso 1: JWT (Oauth2 Resource Server)
        if (principal instanceof org.springframework.security.oauth2.jwt.Jwt) {
            org.springframework.security.oauth2.jwt.Jwt jwt = (org.springframework.security.oauth2.jwt.Jwt) principal;
            Long empresaIdLong = (Long) jwt.getClaims().get("empresaId");
            return empresaIdLong != null ? empresaIdLong.intValue() : null;
        } 
        
        // Caso 2: UserDetails (UsernamePasswordAuthenticationToken)
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            return usuarioService.buscarPorUsername(username).getEmpresaId();
        }

        throw new IllegalStateException("Tipo de autenticación no soportado: " + principal.getClass().getName());
    }
}
