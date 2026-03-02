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
     * Actualiza los datos de una empresa por ID
     * Para uso de Super Admin (puede editar cualquier empresa)
     */
    @Transactional
    public EmpresaResponse actualizarEmpresa(Long id, com.data.datafacturador.dto.EmpresaRequest request) {
        log.info("Actualizando empresa ID: {}", id);
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaNoEncontradaException(id));

        if (request.getNombre() != null)         empresa.setNombre(request.getNombre());
        if (request.getRazonSocial() != null)    empresa.setRazonSocial(request.getRazonSocial());
        if (request.getNit() != null)            empresa.setNit(request.getNit());
        if (request.getDv() != null)             empresa.setDv(request.getDv());
        if (request.getDireccion() != null)      empresa.setDireccion(request.getDireccion());
        if (request.getCiudad() != null)         empresa.setCiudad(request.getCiudad());
        if (request.getDepartamento() != null)   empresa.setDepartamento(request.getDepartamento());
        if (request.getPais() != null)           empresa.setPais(request.getPais());
        if (request.getTelefono() != null)       empresa.setTelefono(request.getTelefono());
        if (request.getCelular() != null)        empresa.setCelular(request.getCelular());
        if (request.getEmail() != null)          empresa.setEmail(request.getEmail());
        if (request.getSitioWeb() != null)       empresa.setSitioWeb(request.getSitioWeb());
        if (request.getLogoUrl() != null)        empresa.setLogoUrl(request.getLogoUrl());
        if (request.getConfiguracion() != null)  empresa.setConfiguracion(request.getConfiguracion());

        empresa.setFechaActualizacion(java.time.LocalDateTime.now());
        empresa = empresaRepository.save(empresa);
        log.info("Empresa {} actualizada exitosamente", id);
        return empresaMapper.toResponse(empresa);
    }

    /**
     * Crea una nueva empresa y su sucursal principal
     * Para uso de Super Admin
     */
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
        empresa.setDv(request.getDv());
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
        // Si el request es del tipo completo y trae detalle, lo usamos
        com.data.datafacturador.sucursal.dto.SucursalRequest detalleSucursal = null;
        
        log.info("Clase del Request recibido: {}", request.getClass().getName());
        
        if (request instanceof com.data.datafacturador.dto.EmpresaCompletaRequest) {
            detalleSucursal = ((com.data.datafacturador.dto.EmpresaCompletaRequest) request).getDetalleSucursalPrincipal();
            log.info("Detalle Sucursal Principal recibido: {}", detalleSucursal);
        } else {
            log.info("Request NO es instancia de EmpresaCompletaRequest");
        }
        
        crearSucursalPrincipal(empresa, detalleSucursal);

        return empresaMapper.toResponse(empresa);
    }

    private void crearSucursalPrincipal(Empresa empresa, com.data.datafacturador.sucursal.dto.SucursalRequest detalle) {
        com.data.datafacturador.sucursal.entity.Sucursal sucursal = new com.data.datafacturador.sucursal.entity.Sucursal();
        
        // Vincular a la empresa
        sucursal.setEmpresaId(empresa.getId().intValue());
        
        if (detalle != null) {
            // --- Usar datos proporcionados en el detalle ---
            sucursal.setNombreComercial(detalle.getNombreComercial() != null ? detalle.getNombreComercial() : empresa.getNombre());
            sucursal.setNombreRazonSocial(detalle.getNombreRazonSocial() != null ? detalle.getNombreRazonSocial() : "SEDE PRINCIPAL");
            
            sucursal.setCcNit(detalle.getCcNit() != null ? detalle.getCcNit() : empresa.getNit());
            sucursal.setDv(detalle.getDv()); // Puede ser null
            
            // Ubicación explícita
            sucursal.setDireccion(detalle.getDireccion() != null ? detalle.getDireccion() : empresa.getDireccion());
            sucursal.setPais(detalle.getPais() != null ? detalle.getPais() : empresa.getPais());
            sucursal.setDepartamento(detalle.getDepartamento() != null ? detalle.getDepartamento() : empresa.getDepartamento());
            sucursal.setIdCiudad(detalle.getCiudad()); // En Request es el ID
            sucursal.setCiudad(detalle.getCiudad()); // Usamos el ID como nombre si no hay otro mecanismo, o idealmente buscar nombre
            // Nota: Aquí se asume que si el FE manda el nombre ciudad en el campo ciudad, está bien.
            
            // Contacto explícito
            sucursal.setTelefono(detalle.getTelefono() != null ? detalle.getTelefono() : empresa.getTelefono());
            sucursal.setCelular(detalle.getCelular() != null ? detalle.getCelular() : empresa.getCelular());
            sucursal.setCorreo(detalle.getCorreo() != null ? detalle.getCorreo() : empresa.getEmail());
            
            // Configuración visual
            sucursal.setSlogan(detalle.getSlogan());
            sucursal.setSloganOrden(detalle.getSloganOrden());
            
            // Configuración Operativa (si viene)
            sucursal.setIdTipoOrganizacion(detalle.getIdTipoOrganizacion());
            sucursal.setIdTipoIdentificacion(detalle.getIdTipoIdentificacion());
            sucursal.setIdResponsabilidadFiscal(detalle.getIdResponsabilidadFiscal());
            sucursal.setIdTipoRegimen(detalle.getIdTipoRegimen());
            sucursal.setIdTipoResponsabilidadTributaria(detalle.getIdTipoResponsabilidadTributaria());
            
            sucursal.setCopiasVentas(detalle.getCopiasVentas() != null ? detalle.getCopiasVentas() : 1L);
            sucursal.setCopiasCompras(detalle.getCopiasCompras() != null ? detalle.getCopiasCompras() : 1L);
            sucursal.setCopiasCobros(detalle.getCopiasCobros() != null ? detalle.getCopiasCobros() : 1L);
            sucursal.setCopiasPagos(detalle.getCopiasPagos() != null ? detalle.getCopiasPagos() : 1L);

            sucursal.setSincronizarDian(detalle.getSincronizarDian() != null ? detalle.getSincronizarDian() : 0L);
            sucursal.setImprimirQr(detalle.getImprimirQr());
            
            // Logo específico si viene
            if (detalle.getLogoBase64() != null && !detalle.getLogoBase64().isEmpty()) {
                try {
                    sucursal.setLogo(java.util.Base64.getDecoder().decode(detalle.getLogoBase64()));
                } catch (IllegalArgumentException e) {
                    log.warn("Error decodificando logo Base64 para sucursal", e);
                    sucursal.setLogo(new byte[0]);
                }
            } else {
                 sucursal.setLogo(new byte[0]);
            }

            // IDs Referencias adicionales
            sucursal.setIdDocumento(detalle.getIdDocumento());
            sucursal.setIdTerceroDefecto(detalle.getIdTerceroDefecto());
            sucursal.setIdListaPrecioDefecto(detalle.getIdListaPrecioDefecto());


        } else {
            // --- Lógica Legacy (Heredar de Empresa) ---
            sucursal.setNombreComercial(empresa.getNombre());
            sucursal.setNombreRazonSocial("SEDE PRINCIPAL");
            
            sucursal.setCcNit(empresa.getNit());
            
            sucursal.setDireccion(empresa.getDireccion() != null ? empresa.getDireccion() : "Dirección no registrada");
            sucursal.setDepartamento(empresa.getDepartamento());
            sucursal.setPais(empresa.getPais());
            
            sucursal.setTelefono(empresa.getTelefono() != null ? empresa.getTelefono() : "No registrado");
            sucursal.setCelular(empresa.getCelular() != null ? empresa.getCelular() : "No registrado");
            sucursal.setCorreo(empresa.getEmail() != null ? empresa.getEmail() : "sin_correo@sistema.com");
            
            sucursal.setSlogan("Gracias por su compra");
            
            sucursal.setCopiasPagos(1L);
            
            sucursal.setSincronizarDian(0L);
            sucursal.setLogo(new byte[0]);
        }
        
        // Configuración Común / Defaults finales
        sucursal.setEstado("ACTIVO");
        sucursal.setFechaActualizacion(java.time.ZonedDateTime.now());
        
        sucursalRepository.save(sucursal);
        log.info("Sucursal principal creada para empresa ID: {}", empresa.getId());
    }
}
