package com.data.datafacturador.controller.admin;

import com.data.datafacturador.dto.ApiResponse;
import com.data.datafacturador.dto.EmpresaRequest;
import com.data.datafacturador.dto.EmpresaResponse;

import com.data.datafacturador.service.EmpresaService;
import com.data.datafacturador.sucursal.dto.SucursalListResponse;
import com.data.datafacturador.sucursal.service.SucursalService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador administrativo para gestión de empresas (Multi-tenant)
 * Requiere rol SUPER_ADMIN
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/empresas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminEmpresaController {

    private final EmpresaService empresaService;
    private final SucursalService sucursalService;

    /**
     * Listar todas las empresas (paginado)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EmpresaResponse>>> listarEmpresas(
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("Listando empresas (Admin)");
        return ResponseEntity.ok(ApiResponse.success(
                empresaService.listarTodas(pageable),
                "Empresas listadas correctamente"
        ));
    }

    /**
     * Obtener detalle de una empresa
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmpresaResponse>> obtenerEmpresa(@PathVariable Long id) {
        log.info("Obteniendo detalle empresa (Admin): {}", id);
        return ResponseEntity.ok(ApiResponse.success(
                empresaService.obtenerEmpresaPorId(id),
                "Detalle de empresa obtenido correctamente"
        ));
    }

    /**
     * Crear una nueva empresa (y su sucursal principal)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EmpresaResponse>> crearEmpresa(@RequestBody EmpresaRequest request) {
        log.info("Creando empresa (Admin): {}", request.getRazonSocial());
        return ResponseEntity.ok(ApiResponse.success(
                empresaService.crearEmpresa(request),
                "Empresa creada exitosamente"
        ));
    }


    /**
     * Listar sucursales de una empresa
     */
    @GetMapping("/{id}/sucursales")
    public ResponseEntity<ApiResponse<List<SucursalListResponse>>> listarSucursalesEmpresa(@PathVariable Long id) {
        log.info("Listando sucursales de empresa (Admin): {}", id);
        return ResponseEntity.ok(ApiResponse.success(
                sucursalService.listarSucursalesPorEmpresa(id.intValue()),
                "Sucursales listadas correctamente"
        ));
    }

    /**
     * Crear una sucursal para una empresa específica
     */
    @PostMapping("/{id}/sucursales")
    public ResponseEntity<ApiResponse<com.data.datafacturador.sucursal.dto.SucursalDetailResponse>> crearSucursalEmpresa(
            @PathVariable Long id,
            @RequestBody com.data.datafacturador.sucursal.dto.SucursalRequest request) {
        log.info("Creando sucursal para empresa (Admin): {}", id);
        return ResponseEntity.ok(ApiResponse.success(
                sucursalService.crearSucursalPorEmpresa(id.intValue(), request),
                "Sucursal creada exitosamente"
        ));
    }
}
