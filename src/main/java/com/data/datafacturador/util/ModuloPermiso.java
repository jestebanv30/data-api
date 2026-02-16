package com.data.datafacturador.util;

import lombok.Getter;

/**
 * Módulos disponibles para asignación de permisos
 * Basado en los datos de data2.permiso_usuario
 */
@Getter
public enum ModuloPermiso {
    // Configuración y Administración
    DATOS_EMPRESA("datos_empresa", "Datos de Empresa"),
    SUCURSAL("sucursal", "Gestión de Sucursales"),
    EMPLEADO("empleado", "Gestión de Empleados"),
    USUARIO("usuario", "Gestión de Usuarios"),
    CONFIGURACION_GENERAL("configuracion_general", "Configuración General"),
    CONFIGURACION_FACTURACION("configuracion_facturacion", "Configuración de Facturación"),

    // Inventario y Productos
    CREAR_PRODUCTO("crear_producto", "Crear/Editar Productos"),
    AJUSTE_INVENTARIO("ajuste_inventario", "Ajuste de Inventario"),
    CONSULTA_INVENTARIO("consulta_inventario", "Consulta de Inventario"),
    TRASLADOS("traslados", "Traslados entre Bodegas"),
    CONSULTA_TRASLADOS("consulta_traslados", "Consulta de Traslados"),

    // Compras y Proveedores
    COMPRAS("compras", "Registro de Compras"),
    DEVOLUCION_COMPRAS("devolucion_compras", "Devolución de Compras"),
    CUENTAS_PAGAR("cuentas_pagar", "Cuentas por Pagar"),
    CONSULTA_COMPRA("consulta_compra", "Consulta de Compras"),

    // Ventas y Clientes
    VENTAS("ventas", "Punto de Venta / Facturación"),
    ANTICIPOS("anticipos", "Anticipos de Clientes"),
    DEVOLUCION_VENTAS("devolucion_ventas", "Devolución de Ventas"),
    CUENTAS_COBRAR("cuentas_cobrar", "Cuentas por Cobrar"),
    CONSULTA_VENTA("consulta_venta", "Consulta de Ventas"),
    CAJA_GENERAL("caja_general", "Caja General"),

    // Órdenes (Técnicas, Vehiculares, Producción)
    CREAR_ORDEN_TECNICO("crear_orden_tecnico", "Crear Orden Técnica"),
    CERRAR_ORDEN_TECNICO("cerrar_orden_tecnico", "Cerrar Orden Técnica"),
    CONSULTA_ORDEN_TECNICO("consulta_orden_tecnico", "Consulta Orden Técnica"),

    CREAR_ORDEN_VEHICULAR("crear_orden_vehicular", "Crear Orden Vehicular"),
    CERRAR_ORDEN_VEHICULAR("cerrar_orden_vehicular", "Cerrar Orden Vehicular"),
    CONSULTA_ORDEN_VEHICULAR("consulta_orden_vehicular", "Consulta Orden Vehicular"),

    CREAR_ORDEN_PRODUCCION("crear_orden_produccion", "Crear Orden Producción"),
    CERRAR_ORDEN_PRODUCCION("cerrar_orden_produccion", "Cerrar Orden Producción"),
    CONSULTA_ORDEN_PRODUCCION("consulta_orden_produccion", "Consulta Orden Producción");

    private final String codigo;
    private final String descripcion;

    ModuloPermiso(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public static ModuloPermiso fromCodigo(String codigo) {
        for (ModuloPermiso modulo : values()) {
            if (modulo.codigo.equalsIgnoreCase(codigo)) {
                return modulo;
            }
        }
        throw new IllegalArgumentException("Módulo desconocido: " + codigo);
    }
}
