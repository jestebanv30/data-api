package com.data.datafacturador.service;

import com.data.datafacturador.dto.UsuarioResponse;
import com.data.datafacturador.entity.PermisoUsuario;
import com.data.datafacturador.entity.Usuario;
import com.data.datafacturador.repository.PermisoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre Usuario entity y DTOs
 */
@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final PermisoUsuarioRepository permisoUsuarioRepository;
    private final com.data.datafacturador.repository.nomina.EmpleadoRepository empleadoRepository;

    public UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        List<PermisoUsuario> permisos = List.of();
        String rol = "INVITADO";

        if (usuario.getEmpresaId() != null) {
            permisos = permisoUsuarioRepository.findByIdUsuarioAndEmpresaIdAndEstado(
                    usuario.getId(), 
                    usuario.getEmpresaId(), 
                    "ACTIVO"
            );
            
            if (!permisos.isEmpty()) {
                rol = permisos.get(0).getTipoUsuario();
            } else if (usuario.getEsAdmin()) {
                rol = "ADMINISTRADOR"; // Fallback legacy
            }
        }
        
        if ("superadmin".equalsIgnoreCase(usuario.getUsername()) || "datasystemadmin".equalsIgnoreCase(usuario.getUsername())) {
            rol = "SUPER_ADMIN";
        }

        String empleadoNombre = null;
        String empleadoIdentificacion = null;

        if (usuario.getIdEmpleado() != null) {
            empleadoRepository.findById(usuario.getIdEmpleado()).ifPresent(empleado -> {
                // Variables finales efectivas para la lambda o usar atomic ref si fuera necesario, 
                // pero aquí no podemos asignar a vars locales no finales.
                // Mejor approach: extraer datos directamente
            });
            
            // Re-escritura para evitar problemas de scope
            var empleadoOpt = empleadoRepository.findById(usuario.getIdEmpleado());
            if (empleadoOpt.isPresent()) {
                var emp = empleadoOpt.get();
                empleadoNombre = emp.getNombres() + " " + emp.getApellidos();
                empleadoIdentificacion = emp.getCc();
            }
        }

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .empresaId(usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null)
                .empresaNombre(usuario.getEmpresa() != null ? usuario.getEmpresa().getRazonSocial() : null)
                .roles(getRoles(usuario))
                .activo(usuario.getActivo())
                .rol(rol)
                .idEmpleado(usuario.getIdEmpleado())
                .empleadoNombre(empleadoNombre)
                .empleadoIdentificacion(empleadoIdentificacion)
                .sucursalesIds(permisos.stream()
                        .map(PermisoUsuario::getIdSucursal)
                        .distinct()
                        .collect(Collectors.toList()))
                .permisos(permisos.stream()
                        .map(PermisoUsuario::getModulo)
                        .distinct()
                        .collect(Collectors.toList()))
                .build();
    }

    private List<String> getRoles(Usuario usuario) {
        if ("superadmin".equalsIgnoreCase(usuario.getUsername()) || "datasystemadmin".equalsIgnoreCase(usuario.getUsername())) {
             return List.of("ROLE_SUPER_ADMIN");
        }
        // TODO: Implementar sistema de roles completo
        if (usuario.getEsAdmin()) {
            return List.of("ADMIN");
        }
        return List.of("USER");
    }
}
