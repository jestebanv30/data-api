package com.data.datafacturador.controller.admin;

import com.data.datafacturador.dto.ApiResponse;
import com.data.datafacturador.dto.UsuarioRequest;
import com.data.datafacturador.dto.UsuarioResponse;
import com.data.datafacturador.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Controlador administrativo para gestión de usuarios
 * Requiere rol SUPER_ADMIN
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Crear un nuevo usuario
     */
    @PostMapping("/usuarios")
    public ResponseEntity<ApiResponse<UsuarioResponse>> crearUsuario(@RequestBody UsuarioRequest request) {
        log.info("Creando usuario (Admin): {}", request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                usuarioService.crearUsuario(request),
                "Usuario creado exitosamente con permisos asignados"
        ));
    }

    /**
     * Listar usuarios (paginado), opcionalmente filtrando por empresa
     */
    @GetMapping("/usuarios")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<UsuarioResponse>>> listarUsuarios(
            @RequestParam(required = false) Integer empresaId,
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        
        log.info("Listando usuarios (Admin) - EmpresaId: {}", empresaId);
        return ResponseEntity.ok(ApiResponse.success(
                usuarioService.listarUsuarios(empresaId, pageable),
                "Usuarios listados correctamente"
        ));
    }

    /**
     * Listar roles disponibles
     */
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<String>>> listarRoles() {
        return ResponseEntity.ok(ApiResponse.success(
                Arrays.asList("ADMINISTRADOR", "EMPLEADO"),
                "Roles listados correctamente"
        ));
    }

    /**
     * Listar módulos de permisos disponibles
     */
    @GetMapping("/permisos/modulos")
    public ResponseEntity<ApiResponse<List<java.util.Map<String, String>>>> listarModulos() {
        List<java.util.Map<String, String>> modulos = Arrays.stream(com.data.datafacturador.util.ModuloPermiso.values())
                .map(m -> java.util.Map.of("codigo", m.getCodigo(), "descripcion", m.getDescripcion()))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(modulos, "Módulos listados correctamente"));
    }

    /**
     * Actualizar usuario existente
     */
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizarUsuario(
            @PathVariable Long id, 
            @RequestBody UsuarioRequest request) {
        
        log.info("Actualizando usuario (Admin): {}", request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                usuarioService.actualizarUsuario(id, request),
                "Usuario actualizado correctamente"
        ));
    }

    /**
     * Cambiar estado de usuario (ACTIVO/INACTIVO)
     */
    @PatchMapping("/usuarios/{id}/estado")
    public ResponseEntity<ApiResponse<com.data.datafacturador.entity.Usuario>> cambiarEstadoUsuario(
            @PathVariable Long id,
            @RequestParam Integer empresaId,
            @RequestBody Map<String, String> body) {
        
        log.info("Cambiando estado de usuario {} a {}", id, body.get("estado"));
        String nuevoEstado = body.get("estado");
        
        return ResponseEntity.ok(ApiResponse.success(
                usuarioService.cambiarEstadoUsuario(id, nuevoEstado, empresaId),
                "Estado de usuario actualizado correctamente"
        ));
    }

    /**
     * Obtener detalle de usuario por ID
     */
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerUsuario(@PathVariable Long id) {
        log.info("Obteniendo usuario (Admin): {}", id);
        return ResponseEntity.ok(ApiResponse.success(
                usuarioService.obtenerUsuario(id),
                "Usuario obtenido correctamente"
        ));
    }

    /**
     * Eliminar usuario
     */
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarUsuario(@PathVariable Long id) {
        log.info("Eliminando usuario (Admin): {}", id);
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Usuario eliminado correctamente"));
    }
}
