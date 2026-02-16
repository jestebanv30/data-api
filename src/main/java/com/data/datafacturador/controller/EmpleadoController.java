package com.data.datafacturador.controller;

import com.data.datafacturador.dto.EmpleadoListaDTO;
import com.data.datafacturador.dto.EmpleadoSinUsuarioDTO;
import com.data.datafacturador.entity.nomina.Empleado;
import com.data.datafacturador.service.EmpleadoService;
import com.data.datafacturador.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Page<Empleado>> listarEmpleados(
            Authentication authentication,
            Pageable pageable) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(empleadoService.listarEmpleados(empresaId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> obtenerEmpleado(@PathVariable Long id, Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(empleadoService.obtenerEmpleado(id, empresaId));
    }

    @GetMapping("/sin-usuario")
    public ResponseEntity<List<EmpleadoSinUsuarioDTO>> listarEmpleadosSinUsuario(Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(empleadoService.listarEmpleadosSinUsuario(empresaId));
    }

    @GetMapping("/lista")
    public ResponseEntity<List<EmpleadoListaDTO>> listarEmpleadosLista(Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(empleadoService.listarEmpleadosLista(empresaId));
    }

    @PostMapping
    public ResponseEntity<Empleado> crearEmpleado(@RequestBody Empleado empleado, Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(empleadoService.crearEmpleado(empleado, empresaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empleado> actualizarEmpleado(
            @PathVariable Long id,
            @RequestBody Empleado empleado,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(empleadoService.actualizarEmpleado(id, empleado, empresaId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        empleadoService.cambiarEstadoEmpleado(id, estado, empresaId);
        return ResponseEntity.ok().build();
    }

    // DELETE eliminado: Solo accesible para Super Admin via AdminEmpleadoController

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
