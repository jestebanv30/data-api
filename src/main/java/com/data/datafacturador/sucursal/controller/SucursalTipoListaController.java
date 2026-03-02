package com.data.datafacturador.sucursal.controller;

import com.data.datafacturador.dto.ApiResponse;
import com.data.datafacturador.entity.referencia.TipoListaPrecios;
import com.data.datafacturador.repository.referencia.TipoListaPreciosRepository;
import com.data.datafacturador.service.UsuarioService;
import com.data.datafacturador.sucursal.entity.Sucursal;
import com.data.datafacturador.sucursal.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Gestión de Tipos de Lista de Precios por Sucursal.
 *
 * Lógica de aislamiento:
 * - Cada tipo_lista pertenece a una sucursal (id_sucursal).
 * - Cada sucursal pertenece a una empresa.
 * - Al filtrar por id_sucursal, automáticamente se aísla por empresa.
 * - El ADMIN solo puede gestionar sucursales de su empresa.
 * - El SUPER_ADMIN puede gestionar cualquier sucursal.
 */
@RestController
@RequestMapping("/api/sucursales/{sucursalId}/tipos-lista")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class SucursalTipoListaController {

    private final TipoListaPreciosRepository tipoListaRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioService usuarioService;

    // -------------------------------------------------------
    // GET — Listar tipos de lista de una sucursal
    // -------------------------------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<List<TipoListaPrecios>>> listar(
            @PathVariable Long sucursalId,
            Authentication auth) {
        verificarAcceso(sucursalId, auth);
        List<TipoListaPrecios> lista = tipoListaRepository.findByIdSucursalOrderByNombreAsc(sucursalId);
        return ResponseEntity.ok(ApiResponse.success(lista, "Tipos de lista listados"));
    }

    // -------------------------------------------------------
    // POST — Crear tipo de lista para una sucursal
    // -------------------------------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<TipoListaPrecios>> crear(
            @PathVariable Long sucursalId,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        verificarAcceso(sucursalId, auth);
        TipoListaPrecios e = new TipoListaPrecios();
        e.setNombre(body.get("nombre"));
        e.setIdSucursal(sucursalId);
        return ResponseEntity.ok(ApiResponse.success(tipoListaRepository.save(e), "Tipo de lista creado"));
    }

    // -------------------------------------------------------
    // PUT — Editar tipo de lista (debe pertenecer a la sucursal)
    // -------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoListaPrecios>> editar(
            @PathVariable Long sucursalId,
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        verificarAcceso(sucursalId, auth);
        TipoListaPrecios e = tipoListaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de lista no encontrado"));
        if (!sucursalId.equals(e.getIdSucursal())) {
            throw new com.data.datafacturador.exception.AccesoDenegadoException(
                    "El tipo de lista no pertenece a esta sucursal");
        }
        if (body.containsKey("nombre")) e.setNombre(body.get("nombre"));
        return ResponseEntity.ok(ApiResponse.success(tipoListaRepository.save(e), "Tipo de lista actualizado"));
    }

    // -------------------------------------------------------
    // DELETE — Eliminar tipo de lista
    // -------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long sucursalId,
            @PathVariable Long id,
            Authentication auth) {
        verificarAcceso(sucursalId, auth);
        TipoListaPrecios e = tipoListaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de lista no encontrado"));
        if (!sucursalId.equals(e.getIdSucursal())) {
            throw new com.data.datafacturador.exception.AccesoDenegadoException(
                    "El tipo de lista no pertenece a esta sucursal");
        }
        tipoListaRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Tipo de lista eliminado"));
    }

    // -------------------------------------------------------
    // Helper: verifica que el ADMIN solo acceda a sus sucursales
    // -------------------------------------------------------
    private void verificarAcceso(Long sucursalId, Authentication auth) {
        boolean isSuperAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        if (isSuperAdmin) return; // SuperAdmin puede acceder a cualquier sucursal

        // Admin: verificar que la sucursal pertenece a su empresa
        Integer empresaIdUsuario = getEmpresaId(auth);
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada: " + sucursalId));

        if (!sucursal.getEmpresaId().equals(empresaIdUsuario)) {
            throw new com.data.datafacturador.exception.AccesoDenegadoException(
                    "No tiene acceso a esta sucursal");
        }
    }

    private Integer getEmpresaId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            Long id = (Long) jwt.getClaims().get("empresaId");
            return id != null ? id.intValue() : null;
        }
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            return usuarioService.buscarPorUsername(ud.getUsername()).getEmpresaId();
        }
        throw new IllegalStateException("Tipo de autenticación no soportado");
    }
}
