package com.data.datafacturador.controller.admin;

import com.data.datafacturador.entity.facturacion.ResolucionTipoDocumento;
import com.data.datafacturador.service.facturacion.ResolucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoints de SuperAdmin para gestionar resoluciones DIAN.
 *
 * - Puede listar/gestionar resoluciones de cualquier empresa/sucursal.
 * - Es el ÚNICO que puede configurar idSoftware, pin, testSetId, claveTécnica.
 */
@RestController
@RequestMapping("/api/admin/facturacion/resoluciones")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminResolucionController {

    private final ResolucionService resolucionService;
    private final com.data.datafacturador.repository.facturacion.ResolucionRepository resolucionRepository;

    // -------------------------------------------------------
    // GET — Listar resoluciones de cualquier sucursal
    // -------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<ResolucionTipoDocumento>> listarPorSucursal(
            @RequestParam Long sucursalId) {
        return ResponseEntity.ok(resolucionService.listarPorSucursalAdmin(sucursalId));
    }

    // -------------------------------------------------------
    // GET — Por ID (sin restricción de empresa)
    // -------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ResolucionTipoDocumento> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                resolucionRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Resolución no encontrada: " + id))
        );
    }

    // -------------------------------------------------------
    // PATCH — Actualizar credenciales técnicas DIAN
    //
    // Body: { "idSoftware": "...", "pin": "...", "testSetId": "...", "claveTecnica": "..." }
    // Todos los campos son opcionales (solo se actualizan los que se envían).
    // -------------------------------------------------------
    @PatchMapping("/{id}/credenciales")
    public ResponseEntity<ResolucionTipoDocumento> actualizarCredenciales(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        ResolucionTipoDocumento actualizado = resolucionService.actualizarCredenciales(
                id,
                body.getOrDefault("idSoftware", null),
                body.getOrDefault("pin", null),
                body.getOrDefault("testSetId", null),
                body.getOrDefault("claveTecnica", null),
                body.getOrDefault("setPruebas", null)
        );
        return ResponseEntity.ok(actualizado);
    }

    // -------------------------------------------------------
    // DELETE — Eliminar cualquier resolución (sin restricción de empresa)
    // -------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resolucionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
