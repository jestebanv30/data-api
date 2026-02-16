package com.data.datafacturador.service;

import com.data.datafacturador.dto.EmpresaResponse;
import com.data.datafacturador.exception.AccesoDenegadoException;
import com.data.datafacturador.exception.EmpresaNoEncontradaException;
import com.data.datafacturador.entity.Empresa;
import com.data.datafacturador.entity.Usuario;
import com.data.datafacturador.repository.EmpresaRepository;
import com.data.datafacturador.repository.UsuarioRepository;
import com.data.datafacturador.sucursal.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de empresa
 * Maneja la lógica de negocio relacionada con empresas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaMapper empresaMapper;
    private final SucursalRepository sucursalRepository;

    /**
     * Obtiene los datos de la empresa del usuario autenticado
     */
    @Transactional(readOnly = true)
    public EmpresaResponse obtenerMiEmpresa() {
        // Obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.debug("Obteniendo empresa para usuario: {}", username);

        // Buscar usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new AccesoDenegadoException("Usuario no encontrado"));

        // Verificar que el usuario tenga empresa
        if (usuario.getEmpresaId() == null) {
            throw new EmpresaNoEncontradaException("Usuario no tiene empresa asignada");
        }

        // Buscar empresa
        Empresa empresa = empresaRepository.findById(Long.valueOf(usuario.getEmpresaId()))
                .orElseThrow(() -> new EmpresaNoEncontradaException(Long.valueOf(usuario.getEmpresaId())));

        // Verificar que la empresa esté activa
        if (!empresa.isActiva()) {
            throw new AccesoDenegadoException("La empresa no está activa");
        }

        log.info("Empresa obtenida exitosamente: {} - {}", empresa.getId(), empresa.getRazonSocial());

        return empresaMapper.toResponse(empresa);
    }

    /**
     * Actualiza los datos de la empresa del usuario autenticado
     * Permite actualizar TODOS los campos de la empresa
     */
    @Transactional
    public EmpresaResponse actualizarMiEmpresa(EmpresaResponse request) {
        // Obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.debug("Actualizando empresa para usuario: {}", username);

        // Buscar usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new AccesoDenegadoException("Usuario no encontrado"));

        // Verificar que el usuario tenga empresa
        if (usuario.getEmpresaId() == null) {
            throw new EmpresaNoEncontradaException("Usuario no tiene empresa asignada");
        }

        // Buscar empresa
        Empresa empresa = empresaRepository.findById(Long.valueOf(usuario.getEmpresaId()))
                .orElseThrow(() -> new EmpresaNoEncontradaException(Long.valueOf(usuario.getEmpresaId())));

        // Actualizar todos los campos permitidos (solo si vienen en el request)
        if (request.getNombre() != null) {
            empresa.setNombre(request.getNombre());
        }
        if (request.getRazonSocial() != null) {
            empresa.setRazonSocial(request.getRazonSocial());
        }
        if (request.getDireccion() != null) {
            empresa.setDireccion(request.getDireccion());
        }
        if (request.getCiudad() != null) {
            empresa.setCiudad(request.getCiudad());
        }
        if (request.getDepartamento() != null) {
            empresa.setDepartamento(request.getDepartamento());
        }
        if (request.getPais() != null) {
            empresa.setPais(request.getPais());
        }
        if (request.getTelefono() != null) {
            empresa.setTelefono(request.getTelefono());
        }
        if (request.getCelular() != null) {
            empresa.setCelular(request.getCelular());
        }
        if (request.getEmail() != null) {
            empresa.setEmail(request.getEmail());
        }
        if (request.getSitioWeb() != null) {
            empresa.setSitioWeb(request.getSitioWeb());
        }
        if (request.getLogoUrl() != null) {
            empresa.setLogoUrl(request.getLogoUrl());
        }
        if (request.getConfiguracion() != null) {
            empresa.setConfiguracion(request.getConfiguracion());
        }

        // Guardar cambios
        empresa = empresaRepository.save(empresa);

        log.info("Empresa actualizada exitosamente: {} - {}", empresa.getId(), empresa.getRazonSocial());

        return empresaMapper.toResponse(empresa);
    }


    /**
     * Lista todas las empresas (paginado)
     * Para uso de Super Admin
     */
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<EmpresaResponse> listarTodas(org.springframework.data.domain.Pageable pageable) {
        return empresaRepository.findAll(pageable)
                .map(empresaMapper::toResponse);
    }
    /**
     * Obtiene una empresa por su ID
     * Para uso de Super Admin
     */
    @Transactional(readOnly = true)
    public EmpresaResponse obtenerEmpresaPorId(Long id) {
        log.debug("Obteniendo empresa por ID: {}", id);
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaNoEncontradaException(id));
        return empresaMapper.toResponse(empresa);
    }

    /**
     * Crea una nueva empresa y su sucursal principal
     * Para uso de Super Admin
     */
    @Transactional
    public EmpresaResponse crearEmpresa(com.data.datafacturador.dto.EmpresaRequest request) {
        log.info("Creando nueva empresa: {}", request.getRazonSocial());

        // Validar NIT único
        if (empresaRepository.existsByNit(request.getNit())) {
            throw new IllegalArgumentException("Ya existe una empresa con el NIT: " + request.getNit());
        }

        // 1. Crear Empresa
        Empresa empresa = new Empresa();
        empresa.setNombre(request.getNombre());
        empresa.setRazonSocial(request.getRazonSocial());
        empresa.setNit(request.getNit());
        empresa.setDireccion(request.getDireccion());
        empresa.setCiudad(request.getCiudad());
        empresa.setDepartamento(request.getDepartamento());
        empresa.setPais(request.getPais() != null ? request.getPais() : "Colombia");
        empresa.setTelefono(request.getTelefono());
        empresa.setCelular(request.getCelular());
        empresa.setEmail(request.getEmail());
        empresa.setSitioWeb(request.getSitioWeb());
        empresa.setLogoUrl(request.getLogoUrl());
        empresa.setDatosPrecargados(request.getDatosPrecargados() != null ? request.getDatosPrecargados() : true);
        empresa.setConfiguracion(request.getConfiguracion());
        empresa.setEstado("ACTIVO");
        empresa.setFechaCreacion(java.time.LocalDateTime.now());
        empresa.setFechaActualizacion(java.time.LocalDateTime.now());

        empresa = empresaRepository.save(empresa);
        log.info("Empresa guardada con ID: {}", empresa.getId());

        // 2. Crear Sucursal Principal
        crearSucursalPrincipal(empresa);

        return empresaMapper.toResponse(empresa);
    }

    private void crearSucursalPrincipal(Empresa empresa) {
        com.data.datafacturador.sucursal.entity.Sucursal sucursal = new com.data.datafacturador.sucursal.entity.Sucursal();
        
        // Vincular a la empresa
        sucursal.setEmpresaId(empresa.getId().intValue());
        
        // Datos básicos (heredados de empresa)
        sucursal.setNombreComercial(empresa.getNombre()); // Nombre comercial igual al de la empresa
        sucursal.setNombreRazonSocial("SEDE PRINCIPAL"); // Nombre interno para distinguir
        
        // Datos legales heredados
        sucursal.setCcNit(empresa.getNit());
        // sucursal.setDv(...) // Calcular DV si es necesario, o dejar null por ahora
        
        // Ubicación heredada
        sucursal.setDireccion(empresa.getDireccion() != null ? empresa.getDireccion() : "Dirección no registrada");
        // Nota: Ciudad y Departamento en Empresa son Strings. En Sucursal son: idCiudad (String para ID) y Departamento (String).
        // Como no tenemos el ID de ciudad mapeado desde el nombre en Empresa (que es texto libre posiblemente), 
        // dejamos ciudad null o intentamos asignar si coincide. 
        // Para evitar errores, dejaremos campos de ubicación específicos vacíos o solo texto si el campo lo permite.
        // Sucursal tiene: direccion (String), departamento (String), ciudad (String nombre), idCiudad (String ID).
        sucursal.setDepartamento(empresa.getDepartamento());
        sucursal.setPais(empresa.getPais());
        
        // Contacto heredado (con defaults por si la empresa no los tiene, ya que son NOT NULL en Sucursal)
        sucursal.setTelefono(empresa.getTelefono() != null ? empresa.getTelefono() : "No registrado");
        sucursal.setCelular(empresa.getCelular() != null ? empresa.getCelular() : "No registrado");
        sucursal.setCorreo(empresa.getEmail() != null ? empresa.getEmail() : "sin_correo@sistema.com");
        
        // Slogan (null o heredado si existiera)
        sucursal.setSlogan("Gracias por su compra"); 
        
        // Configuración de impresión por defecto
        sucursal.setCopiasVentas(1L);
        sucursal.setCopiasCompras(1L);
        sucursal.setCopiasCobros(1L);
        sucursal.setCopiasPagos(1L);
        sucursal.setSincronizarDian(0L); // No sincronizar por defecto
        
        // Logo vacio por defecto (mejor que null para evitar NPEs en frontend)
        sucursal.setLogo(new byte[0]);

        // Configuración por defecto
        sucursal.setEstado("ACTIVO");
        sucursal.setFechaActualizacion(java.time.ZonedDateTime.now());
        
        // Guardar
        sucursalRepository.save(sucursal);
        log.info("Sucursal principal creada para empresa ID: {}", empresa.getId());
    }
}
