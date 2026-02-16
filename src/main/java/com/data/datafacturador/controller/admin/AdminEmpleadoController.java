package com.data.datafacturador.controller.admin;

import com.data.datafacturador.dto.ApiResponse;
import com.data.datafacturador.entity.nomina.Empleado;
import com.data.datafacturador.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminEmpleadoController {

    private final EmpleadoService empleadoService;

    /**
     * Listar empleados de una empresa específica
     */
    @GetMapping("/empleados")
    public ResponseEntity<ApiResponse<Page<Empleado>>> listarEmpleados(
            @RequestParam Integer empresaId,
            Pageable pageable) {
        log.info("Listando empleados (Admin) - EmpresaId: {}", empresaId);
        return ResponseEntity.ok(ApiResponse.success(
                empleadoService.listarEmpleados(empresaId, pageable),
                "Empleados listados correctamente"
        ));
    }

    /**
     * Listar empleados de una empresa específica (Formato Lista DTO para selectores)
     */
    @GetMapping("/empleados/lista")
    public ResponseEntity<ApiResponse<java.util.List<com.data.datafacturador.dto.EmpleadoListaDTO>>> listarEmpleadosLista(
            @RequestParam Integer empresaId) {
        log.info("Listando empleados lista (Admin) - EmpresaId: {}", empresaId);
        return ResponseEntity.ok(ApiResponse.success(
                empleadoService.listarEmpleadosLista(empresaId),
                "Empleados listados correctamente"
        ));
    }

    /**
     * Obtener empleado por ID (requiere empresaId para validación/contexto)
     */
    @GetMapping("/empleados/{id}")
    public ResponseEntity<ApiResponse<Empleado>> obtenerEmpleado(
            @PathVariable Long id,
            @RequestParam Integer empresaId) {
        log.info("Obteniendo empleado (Admin): {} - EmpresaId: {}", id, empresaId);
        return ResponseEntity.ok(ApiResponse.success(
                empleadoService.obtenerEmpleado(id, empresaId),
                "Empleado obtenido correctamente"
        ));
    }

    /**
     * Crear empleado para una empresa
     */
    @PostMapping("/empleados")
    public ResponseEntity<ApiResponse<Empleado>> crearEmpleado(
            @RequestParam Integer empresaId,
            @RequestBody Empleado empleado) {
        log.info("Creando empleado (Admin) para empresa: {}", empresaId);
        return ResponseEntity.ok(ApiResponse.success(
                empleadoService.crearEmpleado(empleado, empresaId),
                "Empleado creado correctamente"
        ));
    }

    /**
     * Actualizar empleado
     */
    @PutMapping("/empleados/{id}")
    public ResponseEntity<ApiResponse<Empleado>> actualizarEmpleado(
            @PathVariable Long id,
            @RequestParam Integer empresaId,
            @RequestBody Empleado empleado) {
        log.info("Actualizando empleado (Admin): {} - EmpresaId: {}", id, empresaId);
        return ResponseEntity.ok(ApiResponse.success(
                empleadoService.actualizarEmpleado(id, empleado, empresaId),
                "Empleado actualizado correctamente"
        ));
    }

    /**
     * Eliminar empleado (Hard Delete)
     */
    @DeleteMapping("/empleados/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarEmpleado(
            @PathVariable Long id,
            @RequestParam Integer empresaId) {
        log.info("Eliminando empleado (Admin): {} - EmpresaId: {}", id, empresaId);
        empleadoService.eliminarEmpleado(id, empresaId);
        return ResponseEntity.ok(ApiResponse.success(null, "Empleado eliminado correctamente"));
    }

    /**
     * Cambiar estado de empleado
     */
    @PatchMapping("/empleados/{id}/estado")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Integer empresaId,
            @RequestParam String estado) {
        log.info("Cambiando estado empleado (Admin): {} - Estado: {} - EmpresaId: {}", id, estado, empresaId);
        empleadoService.cambiarEstadoEmpleado(id, estado, empresaId);
        return ResponseEntity.ok(ApiResponse.success(null, "Estado actualizado correctamente"));
    }
}
