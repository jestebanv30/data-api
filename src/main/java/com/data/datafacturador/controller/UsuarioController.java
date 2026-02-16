package com.data.datafacturador.controller;

import com.data.datafacturador.dto.ApiResponse;
import com.data.datafacturador.dto.UsuarioRequest;
import com.data.datafacturador.dto.UsuarioResponse;
import com.data.datafacturador.service.UsuarioService;
import com.data.datafacturador.util.ModuloPermiso;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para gestión de usuarios por administradores de empresa
 * Permite a los admins gestionar usuarios de su propia empresa
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Listar usuarios de mi empresa
     */
    @GetMapping("/usuarios")
    public ResponseEntity<ApiResponse<Page<UsuarioResponse>>> listarUsuarios(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        log.info("Listando usuarios de empresa: {}", empresaId);
        
        return ResponseEntity.ok(ApiResponse.success(
                usuarioService.listarUsuarios(empresaId, pageable),
                "Usuarios listados correctamente"
        ));
    }

    /**
     * Editar usuario de mi empresa
     */
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioRequest request,
            Authentication authentication) {
        
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        log.info("Actualizando usuario {} de empresa {}", id, empresaId);
        
        return ResponseEntity.ok(ApiResponse.success(
                usuarioService.actualizarUsuarioEmpresa(id, request, empresaId),
                "Usuario actualizado correctamente"
        ));
    }

    /**
     * Obtener usuario de mi empresa por ID
     */
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerUsuario(
            @PathVariable Long id,
            Authentication authentication) {
        
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        log.info("Obteniendo usuario {} de empresa {}", id, empresaId);
        
        return ResponseEntity.ok(ApiResponse.success(
                usuarioService.obtenerUsuarioEmpresa(id, empresaId),
                "Usuario obtenido correctamente"
        ));
    }

    /**
     * Listar módulos de permisos disponibles
     */
    @GetMapping("/permisos/modulos")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> listarModulos() {
        List<Map<String, String>> modulos = Arrays.stream(ModuloPermiso.values())
                .map(m -> Map.of("codigo", m.getCodigo(), "descripcion", m.getDescripcion()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(modulos, "Módulos listados correctamente"));
    }

    /**
     * Cambiar estado de usuario (ACTIVO/INACTIVO) - Solo usuarios de mi empresa
     */
    @PatchMapping("/usuarios/{id}/estado")
    public ResponseEntity<ApiResponse<com.data.datafacturador.entity.Usuario>> cambiarEstadoUsuario(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        String nuevoEstado = body.get("estado");
        
        log.info("Admin de empresa {} cambiando estado de usuario {} a {}", empresaId, id, nuevoEstado);
        
        return ResponseEntity.ok(ApiResponse.success(
                usuarioService.cambiarEstadoUsuario(id, nuevoEstado, empresaId),
                "Estado de usuario actualizado correctamente"
        ));
    }

    /**
     * Obtiene el empresaId del usuario autenticado
     */
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
            // Buscar usuario en BD para obtener empresaId (podría optimizarse guardándolo en UserDetails)
            return usuarioService.buscarPorUsername(username).getEmpresaId();
        }

        throw new IllegalStateException("Tipo de autenticación no soportado: " + principal.getClass().getName());
    }
}
