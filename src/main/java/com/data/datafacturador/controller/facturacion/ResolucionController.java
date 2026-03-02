package com.data.datafacturador.controller.facturacion;

import com.data.datafacturador.entity.facturacion.ResolucionTipoDocumento;
import com.data.datafacturador.entity.referencia.TipoDocumento;
import com.data.datafacturador.repository.referencia.TipoDocumentoRepository;
import com.data.datafacturador.service.facturacion.ResolucionService;
import com.data.datafacturador.service.UsuarioService;
import com.data.datafacturador.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturacion/resoluciones")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class ResolucionController {

    private final ResolucionService resolucionService;
    private final UsuarioService usuarioService;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    // -------------------------------------------------------
    // GET — Catálogo de tipos de documento (dropdown frontend)
    // -------------------------------------------------------
    @GetMapping("/tipos-documento")
    public ResponseEntity<List<TipoDocumento>> listarTiposDocumento() {
        return ResponseEntity.ok(tipoDocumentoRepository.findAll());
    }

    // -------------------------------------------------------
    // GET — Listar por sucursal
    // -------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<ResolucionTipoDocumento>> listarPorSucursal(
            @RequestParam Long sucursalId,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaId(authentication);
        return ResponseEntity.ok(resolucionService.listarPorSucursal(sucursalId, empresaId));
    }

    // -------------------------------------------------------
    // GET — Por ID (para cargar el formulario de edición)
    // -------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ResolucionTipoDocumento> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaId(authentication);
        return ResponseEntity.ok(resolucionService.obtenerPorId(id, empresaId));
    }

    // -------------------------------------------------------
    // POST — Crear resolución
    // -------------------------------------------------------
    @PostMapping
    public ResponseEntity<ResolucionTipoDocumento> crearResolucion(
            @RequestBody ResolucionTipoDocumento resolucion,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaId(authentication);
        return ResponseEntity.ok(resolucionService.crearResolucion(resolucion, empresaId));
    }

    // -------------------------------------------------------
    // PUT — Actualizar resolución (campos Admin: resolución, rango, fechas)
    // -------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ResolucionTipoDocumento> actualizarResolucion(
            @PathVariable Long id,
            @RequestBody ResolucionTipoDocumento resolucion,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaId(authentication);
        return ResponseEntity.ok(resolucionService.actualizarResolucion(id, resolucion, empresaId));
    }

    // -------------------------------------------------------
    // DELETE — Eliminar resolución
    // -------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarResolucion(
            @PathVariable Long id,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaId(authentication);
        resolucionService.eliminarResolucion(id, empresaId);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // Helper
    // -------------------------------------------------------
    private Integer obtenerEmpresaId(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(email);
        if (usuario.getEmpresaId() == null) {
            throw new SecurityException("Usuario no asociado a una empresa");
        }
        return usuario.getEmpresaId();
    }
}
