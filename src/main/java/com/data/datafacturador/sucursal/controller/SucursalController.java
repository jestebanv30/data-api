package com.data.datafacturador.sucursal.controller;

import com.data.datafacturador.dto.ApiResponse;
import com.data.datafacturador.sucursal.dto.SucursalDetailResponse;
import com.data.datafacturador.sucursal.dto.SucursalListResponse;
import com.data.datafacturador.sucursal.service.SucursalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de sucursales
 */
@Slf4j
@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    /**
     * Lista todas las sucursales de la empresa del usuario autenticado
     * Vista resumida con campos esenciales
     * 
     * @return Lista de sucursales resumidas
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SucursalListResponse>>> listarSucursales() {
        log.info("GET /api/sucursales - Listar sucursales");

        List<SucursalListResponse> sucursales = sucursalService.listarMisSucursales();

        return ResponseEntity.ok(ApiResponse.success(
                sucursales,
                "Sucursales obtenidas exitosamente"));
    }

    /**
     * Obtiene el detalle completo de una sucursal específica
     * 
     * @param id ID de la sucursal
     * @return Detalle completo de la sucursal
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SucursalDetailResponse>> obtenerDetalleSucursal(
            @PathVariable Long id) {
        log.info("GET /api/sucursales/{} - Obtener detalle de sucursal", id);

        SucursalDetailResponse sucursal = sucursalService.obtenerDetalleSucursal(id);

        return ResponseEntity.ok(ApiResponse.success(
                sucursal,
                "Detalle de sucursal obtenido exitosamente"
        ));
    }

    /**
     * Crea una nueva sucursal
     * 
     * @param request Datos de la nueva sucursal
     * @return Detalle de la sucursal creada
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SucursalDetailResponse>> crearSucursal(
            @RequestBody com.data.datafacturador.sucursal.dto.SucursalRequest request) {
        log.info("POST /api/sucursales - Creando nueva sucursal");
        
        SucursalDetailResponse sucursal = sucursalService.crearSucursal(request);
        
        return ResponseEntity.ok(ApiResponse.success(
                sucursal,
                "Sucursal creada exitosamente"
        ));
    }

    /**
     * Actualiza una sucursal existente
     * 
     * @param id ID de la sucursal
     * @param request Datos a actualizar (incluyendo IDs de referencia)
     * @return Detalle de la sucursal actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SucursalDetailResponse>> actualizarSucursal(
            @PathVariable Long id,
            @RequestBody com.data.datafacturador.sucursal.dto.SucursalRequest request) {
        log.info("PUT /api/sucursales/{} - Actualizando sucursal", id);
        
        SucursalDetailResponse sucursal = sucursalService.actualizarSucursal(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(
                sucursal,
                "Sucursal actualizada exitosamente"
        ));
    }
}
