package com.data.datafacturador.service;

import com.data.datafacturador.dto.UsuarioRequest;
import com.data.datafacturador.dto.UsuarioResponse;
import com.data.datafacturador.entity.PermisoUsuario;
import com.data.datafacturador.entity.Usuario;
import com.data.datafacturador.repository.PermisoUsuarioRepository;
import com.data.datafacturador.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Servicio para gestión de usuarios
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PermisoUsuarioRepository permisoUsuarioRepository;
    private final com.data.datafacturador.repository.nomina.EmpleadoRepository empleadoRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    // Constantes de Módulos (Fallback legacy)
    private static final List<String> MODULOS_ADMIN = Arrays.asList(
            "datos_empresa", "sucursal", "empleado", "usuario",
            "configuracion_general", "configuracion_facturacion",
            "crear_producto", "compras", "devolucion_compras", "cuentas_pagar", "consulta_compra",
            "traslados", "consulta_traslados", "ventas", "anticipos", "devolucion_ventas", "cuentas_cobrar", "consulta_venta",
            "ajuste_inventario", "consulta_inventario", "caja_general",
            "crear_orden_tecnico", "cerrar_orden_tecnico", "consulta_orden_tecnico",
            "crear_orden_vehicular", "cerrar_orden_vehicular", "consulta_orden_vehicular",
            "crear_orden_produccion", "cerrar_orden_produccion", "consulta_orden_produccion"
    );

    private static final List<String> MODULOS_EMPLEADO = Arrays.asList(
            "crear_producto", "compras", "consulta_compra",
            "ventas", "anticipos", "consulta_venta",
            "caja_general"
    );

    /**
     * Crea un nuevo usuario y le asigna permisos
     */
    @Transactional
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        log.info("Creando usuario: {} para empresa: {}", request.getUsername(), request.getEmpresaId());

        // 1. Validar existencia
        if (usuarioRepository.existsByUsuario(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        // 2. Crear Entidad Usuario
        Usuario usuario = new Usuario();
        usuario.setUsuario(request.getUsername());
        usuario.setClave(passwordEncoder.encode(request.getPassword()));
        usuario.setEmpresaId(request.getEmpresaId());
        usuario.setIdEmpleado(request.getIdEmpleado());
        usuario.setActivo(request.getActivo() != null ? request.getActivo() : true);
        
        usuario = usuarioRepository.save(usuario);

        // 2.1. Sincronizar Empleado (si existe)
        if (usuario.getIdEmpleado() != null) {
            Usuario finalUsuario = usuario; // efectivo final para lambda
            empleadoRepository.findById(usuario.getIdEmpleado()).ifPresent(empleado -> {
                empleado.setUsuario(finalUsuario);
                empleadoRepository.save(empleado);
            });
        }

        // 3. Asignar Permisos
        asignarPermisos(usuario, request.getSucursalesIds(), request.getPermisos(), request.getRol());

        return usuarioMapper.toResponse(usuario);
    }

    /**
     * Actualiza un usuario existente y sus permisos
     */
    @Transactional
    public UsuarioResponse actualizarUsuario(Long id, UsuarioRequest request) {
        log.info("Actualizando usuario: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
                
        // Validar username único si cambió
        if (!usuario.getUsuario().equals(request.getUsername()) && 
            usuarioRepository.existsByUsuario(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        // Detectar si cambió el empleado
        Long idEmpleadoAnterior = usuario.getIdEmpleado();
        Long idEmpleadoNuevo = request.getIdEmpleado();

        usuario.setUsuario(request.getUsername());
        usuario.setEmpresaId(request.getEmpresaId());
        usuario.setIdEmpleado(request.getIdEmpleado());
        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
        }
        
        // Sólo actualizar password si viene en el request (y no es vacío)
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            usuario.setClave(passwordEncoder.encode(request.getPassword()));
        }

        usuario = usuarioRepository.save(usuario);

        // Sincronizar Empleado (manejo de cambios)
        if (idEmpleadoAnterior != null && !idEmpleadoAnterior.equals(idEmpleadoNuevo)) {
            // Desvincular anterior
            empleadoRepository.findById(idEmpleadoAnterior).ifPresent(empleado -> {
                empleado.setUsuario(null);
                empleadoRepository.save(empleado);
            });
        }
        
        if (idEmpleadoNuevo != null && !idEmpleadoNuevo.equals(idEmpleadoAnterior)) {
             // Vincular nuevo
             Usuario finalUsuario = usuario;
             empleadoRepository.findById(idEmpleadoNuevo).ifPresent(empleado -> {
                 empleado.setUsuario(finalUsuario);
                 empleadoRepository.save(empleado);
             });
        }

        // Actualizar permisos (Borrar anteriores y Crear nuevos)
        // Nota: Se asume que el usuario envía TODOS los permisos deseados
        permisoUsuarioRepository.deleteByIdUsuarioAndEmpresaId(usuario.getId(), usuario.getEmpresaId());
        permisoUsuarioRepository.flush(); // Asegurar borrado antes de insertar
        
        asignarPermisos(usuario, request.getSucursalesIds(), request.getPermisos(), request.getRol());

        return usuarioMapper.toResponse(usuario);
    }

    /**
     * Cambiar estado de usuario (ACTIVO/INACTIVO)
     */
    @Transactional
    public Usuario cambiarEstadoUsuario(Long id, String nuevoEstado, Integer empresaId) {
        log.info("Cambiando estado de usuario {} a {}", id, nuevoEstado);
        
        Usuario usuario = usuarioRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado o no pertenece a la empresa"));
        
        if (!"ACTIVO".equals(nuevoEstado) && !"INACTIVO".equals(nuevoEstado)) {
            throw new IllegalArgumentException("Estado inválido. Use ACTIVO o INACTIVO");
        }
        
        usuario.setEstado(nuevoEstado);
        return usuarioRepository.save(usuario);
    }

    private void asignarPermisos(Usuario usuario, List<Long> sucursalesIds, List<String> permisos, String rol) {
        // 1. Determinar sucursales
        List<Long> sucursales = (sucursalesIds != null && !sucursalesIds.isEmpty()) 
                ? sucursalesIds 
                : List.of(1L); // Fallback a sucursal principal si no se especifica
                
        // 2. Determinar módulos (módulos explícitos > fallback por rol)
        List<String> modulos;
        if (permisos != null && !permisos.isEmpty()) {
            modulos = permisos;
        } else {
            // Fallback por rol (logica legacy)
            if ("ADMINISTRADOR".equalsIgnoreCase(rol)) {
                modulos = MODULOS_ADMIN;
            } else if ("EMPLEADO".equalsIgnoreCase(rol)) {
                modulos = MODULOS_EMPLEADO;
            } else {
                modulos = List.of(); // Sin permisos
            }
        }

        // 3. Crear registros de permisos
        for (Long sucursalId : sucursales) {
            for (String modulo : modulos) {
                PermisoUsuario permiso = PermisoUsuario.builder()
                        .idUsuario(usuario.getId())
                        .empresaId(usuario.getEmpresaId())
                        .idSucursal(sucursalId)
                        .modulo(modulo)
                        .tipoUsuario(rol != null ? rol.toUpperCase() : "EMPLEADO")
                        .estado("ACTIVO")
                        .fechaCreacion(ZonedDateTime.now())
                        .build();
                
                permisoUsuarioRepository.save(permiso);
            }
        }
    }

    /**
     * Lista usuarios (paginado), opcionalmente filtrando por empresa
     */
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<UsuarioResponse> listarUsuarios(Integer empresaId, org.springframework.data.domain.Pageable pageable) {
        if (empresaId != null) {
            return usuarioRepository.findByEmpresaId(empresaId, pageable)
                    .map(usuarioMapper::toResponse);
        } else {
            return usuarioRepository.findAll(pageable)
                    .map(usuarioMapper::toResponse);
        }
    }

    /**
     * Obtiene un usuario por ID (Admin)
     */
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return usuarioMapper.toResponse(usuario);
    }

    /**
     * Busca un usuario por username (Para uso interno/seguridad)
     */
    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
    }

    /**
     * Obtiene un usuario validando que pertenezca a la empresa del admin
     */
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuarioEmpresa(Long id, Integer empresaIdAutenticado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!usuario.getEmpresaId().equals(empresaIdAutenticado)) {
            throw new IllegalArgumentException("No tienes permiso para ver este usuario");
        }
        
        return usuarioMapper.toResponse(usuario);
    }

    /**
     * Actualiza un usuario validando que pertenezca a la empresa del admin
     */
    @Transactional
    public UsuarioResponse actualizarUsuarioEmpresa(Long id, UsuarioRequest request, Integer empresaIdAutenticado) {
        log.info("Actualizando usuario {} - Validando empresa {}", id, empresaIdAutenticado);

        // 1. Validar que el usuario a editar pertenezca a la empresa del admin
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        if (!usuario.getEmpresaId().equals(empresaIdAutenticado)) {
            throw new IllegalArgumentException("No tienes permiso para editar este usuario");
        }

        // 2. Forzar que el empresaId no cambie (seguridad)
        request.setEmpresaId(empresaIdAutenticado);

        // 3. Reutilizar lógica de actualización existente
        return actualizarUsuario(id, request);
    }

    /**
     * Elimina un usuario y sus permisos (Admin)
     */
    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        // 0. Desvincular empleado si existe
        usuarioRepository.findById(id).ifPresent(u -> {
            if (u.getIdEmpleado() != null) {
                empleadoRepository.findById(u.getIdEmpleado()).ifPresent(e -> {
                    e.setUsuario(null);
                    empleadoRepository.save(e);
                });
            }
        });

        // 1. Eliminar permisos
        permisoUsuarioRepository.deleteByIdUsuario(id);
        
        // 2. Eliminar usuario
        usuarioRepository.deleteById(id);
        log.info("Usuario eliminado correctamente: {}", id);
    }
}
