package com.data.datafacturador.util;

/**
 * Contexto de Empresa - ThreadLocal para mantener el empresa_id de la sesión actual
 * Esto permite que todos los servicios accedan al empresa_id sin pasarlo como parámetro
 */
public class EmpresaContext {

    private static final ThreadLocal<Integer> empresaIdHolder = new ThreadLocal<>();

    /**
     * Establece el empresa_id para el thread actual
     */
    public static void setEmpresaId(Integer empresaId) {
        empresaIdHolder.set(empresaId);
    }

    /**
     * Obtiene el empresa_id del thread actual
     */
    public static Integer getEmpresaId() {
        return empresaIdHolder.get();
    }

    /**
     * Limpia el empresa_id del thread actual
     * IMPORTANTE: Llamar siempre al final del request
     */
    public static void clear() {
        empresaIdHolder.remove();
    }

    /**
     * Verifica si hay un empresa_id establecido
     */
    public static boolean hasEmpresaId() {
        return empresaIdHolder.get() != null;
    }
}
