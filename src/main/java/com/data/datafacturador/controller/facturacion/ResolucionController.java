package com.data.datafacturador.controller.facturacion;

import com.data.datafacturador.entity.facturacion.ResolucionTipoDocumento;
import com.data.datafacturador.service.facturacion.ResolucionService;
import com.data.datafacturador.service.UsuarioService;
import com.data.datafacturador.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturacion/resoluciones")
@RequiredArgsConstructor
public class ResolucionController {

    private final ResolucionService resolucionService;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<ResolucionTipoDocumento>> listarPorSucursal(
            @RequestParam Long sucursalId,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(resolucionService.listarPorSucursal(sucursalId, empresaId));
    }

    @PostMapping
    public ResponseEntity<ResolucionTipoDocumento> crearResolucion(
            @RequestBody ResolucionTipoDocumento resolucion,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(resolucionService.crearResolucion(resolucion, empresaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResolucionTipoDocumento> actualizarResolucion(
            @PathVariable Long id,
            @RequestBody ResolucionTipoDocumento resolucion,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        return ResponseEntity.ok(resolucionService.actualizarResolucion(id, resolucion, empresaId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarResolucion(
            @PathVariable Long id,
            Authentication authentication) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado(authentication);
        resolucionService.eliminarResolucion(id, empresaId);
        return ResponseEntity.noContent().build();
    }

    private Integer obtenerEmpresaIdUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(email);
        
        if (usuario.getEmpresaId() == null) {
             // Podría ser Super Admin sin empresa, pero este módulo es para admin de empresa
             // Si super admin quiere usarlo, debería tener lógica especial, 
             // pero por ahora asumimos que el usuario DEBE tener empresa para configurar resoluciones de empresa.
             throw new SecurityException("Usuario no asociado a una empresa");
        }
        return usuario.getEmpresaId();
    }
}
