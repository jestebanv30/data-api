package com.data.datafacturador.security;

import com.data.datafacturador.entity.Usuario;
import com.data.datafacturador.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementación personalizada de UserDetailsService
 * Carga usuarios desde la base de datos para autenticación
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final com.data.datafacturador.repository.PermisoUsuarioRepository permisoUsuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Cargando usuario: {}", username);

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));

        // Verificar que el usuario esté activo
        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        // Verificar que la empresa esté activa
        if (usuario.getEmpresa() == null || !usuario.getEmpresa().getActivo()) {
            throw new UsernameNotFoundException(
                    "Usuario no tiene empresa activa: " + username);
        }

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(getAuthorities(usuario))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }

    /**
     * Obtiene las autoridades (roles) del usuario
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        // 1. Roles especiales hardcodeados (Super Admin)
        if ("superadmin".equalsIgnoreCase(usuario.getUsername()) || "datasystemadmin".equalsIgnoreCase(usuario.getUsername())) {
             return Collections.singletonList(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
        }
        
        // 2. Buscar roles en PermisoUsuario
        java.util.List<com.data.datafacturador.entity.PermisoUsuario> permisos = 
            permisoUsuarioRepository.findByIdUsuarioAndEmpresaIdAndEstado(usuario.getId(), usuario.getEmpresaId(), "ACTIVO");
            
        boolean esAdmin = permisos.stream()
                .anyMatch(p -> "ADMINISTRADOR".equalsIgnoreCase(p.getTipoUsuario()));
        
        String role = esAdmin ? "ROLE_ADMIN" : "ROLE_USER";
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
