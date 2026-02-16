package com.data.datafacturador.controller;

import com.data.datafacturador.entity.configuracion.Retencion;
import com.data.datafacturador.entity.configuracion.SalarioMinimo;
import com.data.datafacturador.entity.configuracion.Uvt;
import com.data.datafacturador.service.ConfiguracionAnualService;
import com.data.datafacturador.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configuracion")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class ConfiguracionAnualController {

    private final ConfiguracionAnualService configuracionAnualService;
    private final UsuarioService usuarioService;

    // --- Salario Mínimo ---

    @GetMapping("/salario-minimo")
    public ResponseEntity<List<SalarioMinimo>> listarSalariosMinimos(
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(configuracionAnualService.listarSalariosMinimos(empresaId));
    }

    @PostMapping("/salario-minimo")
    public ResponseEntity<SalarioMinimo> guardarSalarioMinimo(
            @RequestBody SalarioMinimo salario,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(configuracionAnualService.guardarSalarioMinimo(salario, empresaId, false));
    }
    
    @PutMapping("/salario-minimo/{id}")
    public ResponseEntity<SalarioMinimo> actualizarSalarioMinimo(
            @PathVariable Long id,
            @RequestBody SalarioMinimo salario,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(configuracionAnualService.actualizarSalarioMinimo(id, salario, empresaId, false));
    }
    
    @DeleteMapping("/salario-minimo/{id}")
    public ResponseEntity<Void> eliminarSalarioMinimo(
            @PathVariable Long id,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        configuracionAnualService.eliminarSalarioMinimo(id, empresaId, false);
        return ResponseEntity.noContent().build();
    }

    // --- UVT ---

    @GetMapping("/uvt")
    public ResponseEntity<Uvt> obtenerUvt(
            @RequestParam(required = false) Integer anio,
            Authentication authentication) {
        if (anio == null) {
            anio = configuracionAnualService.obtenerAnioActual();
        }
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(configuracionAnualService.obtenerUvt(anio, empresaId));
    }

    @PostMapping("/uvt")
    public ResponseEntity<Uvt> guardarUvt(
            @RequestBody Uvt uvt,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(configuracionAnualService.guardarUvt(uvt, empresaId));
    }

    // --- Retenciones ---

    @GetMapping("/retenciones")
    public ResponseEntity<List<Retencion>> listarRetenciones(
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(configuracionAnualService.listarRetenciones(empresaId));
    }

    @PostMapping("/retenciones")
    public ResponseEntity<Retencion> guardarRetencion(
            @RequestBody Retencion retencion,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(configuracionAnualService.guardarRetencion(retencion, empresaId));
    }

    @PutMapping("/retenciones/{id}")
    public ResponseEntity<Retencion> actualizarRetencion(
            @PathVariable Long id,
            @RequestBody Retencion retencion,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(configuracionAnualService.actualizarRetencion(id, retencion, empresaId, false));
    }

    @DeleteMapping("/retenciones/{id}")
    public ResponseEntity<Void> eliminarRetencion(
            @PathVariable Long id,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        configuracionAnualService.eliminarRetencion(id, empresaId, false);
        return ResponseEntity.noContent().build();
    }

    // --- Impuestos ---

    @GetMapping("/impuestos")
    public ResponseEntity<List<com.data.datafacturador.entity.configuracion.Impuesto>> listarImpuestos(
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(configuracionAnualService.listarImpuestos(empresaId));
    }

    @PostMapping("/impuestos")
    public ResponseEntity<com.data.datafacturador.entity.configuracion.Impuesto> guardarImpuesto(
            @RequestBody com.data.datafacturador.entity.configuracion.Impuesto impuesto,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(configuracionAnualService.guardarImpuesto(impuesto, empresaId));
    }

    @PutMapping("/impuestos/{id}")
    public ResponseEntity<com.data.datafacturador.entity.configuracion.Impuesto> actualizarImpuesto(
            @PathVariable Long id,
            @RequestBody com.data.datafacturador.entity.configuracion.Impuesto impuesto,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(configuracionAnualService.actualizarImpuesto(id, impuesto, empresaId, false));
    }

    @DeleteMapping("/impuestos/{id}")
    public ResponseEntity<Void> eliminarImpuesto(
            @PathVariable Long id,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        configuracionAnualService.eliminarImpuesto(id, empresaId, false);
        return ResponseEntity.noContent().build();
    }

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
