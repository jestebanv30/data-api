package com.data.datafacturador.controller.admin;

import com.data.datafacturador.dto.ApiResponse;
import com.data.datafacturador.sucursal.dto.SucursalListResponse;
import com.data.datafacturador.sucursal.service.SucursalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador administrativo para gestión global de sucursales
 * Requiere rol SUPER_ADMIN
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/sucursales")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminSucursalController {

    private final SucursalService sucursalService;

    /**
     * Listar todas las sucursales del sistema (paginado)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SucursalListResponse>>> listarSucursales(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Listando todas las sucursales del sistema (Admin)");
        return ResponseEntity.ok(ApiResponse.success(
                sucursalService.listarTodas(pageable),
                "Sucursales listadas correctamente"
        ));
    }

    /**
     * Obtener detalle de una sucursal por ID (Global)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<com.data.datafacturador.sucursal.dto.SucursalDetailResponse>> obtenerSucursal(@org.springframework.web.bind.annotation.PathVariable Long id) {
        log.info("Obteniendo detalle de sucursal (Admin): {}", id);
        return ResponseEntity.ok(ApiResponse.success(
                sucursalService.obtenerDetalleSucursalPorId(id),
                "Detalle de sucursal obtenido correctamente"
        ));
    }
}
