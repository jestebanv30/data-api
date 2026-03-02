package com.data.datafacturador.dto.facturacion;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resultado de la extracción automática del PDF DIAN Formato 1876.
 * El frontend usará estos datos para precargar el formulario de nueva resolución,
 * permitiendo al usuario revisar y confirmar antes de guardar.
 */
@Data
@Builder
public class PdfDianExtractionDTO {

    /** Número de formulario DIAN (normalmente 17 dígitos). Se usará como numeroResolucion. */
    private String numeroResolucion;

    /** Prefijo de la resolución (Casilla 31). Ej: "SETP", "FV", "DSDS". */
    private String prefijo;

    /** Rango desde (Casilla 32). */
    private BigDecimal rangoDesde;

    /** Rango hasta (Casilla 33). */
    private BigDecimal rangoHasta;

    /**
     * Fecha de inicio de vigencia: extraída del sello "Fecha Acuse de Recibo - Firmado"
     * que aparece en la página 2 del PDF.
     */
    private LocalDate fechaDesde;

    /**
     * Fecha de fin de vigencia: calculada como fechaDesde + vigencia_meses.
     * La vigencia en meses viene de la Casilla 38 ("Vigencia").
     */
    private LocalDate fechaHasta;

    /**
     * Nombre de la modalidad/tipo de documento detectada en el PDF.
     * Ej: "FACTURA ELECTRÓNICA DE VENTA", "DOCUMENTO SOPORTE", "NÓMINA ELECTRÓNICA".
     * El frontend usará esto como sugerencia para que el usuario mapee al idTipoDocumento.
     */
    private String tipoDocumentoDetectado;

    /** Cantidad de meses de vigencia (Casilla 38). Solo informativo. */
    private Integer vigenciaMeses;

    /**
     * Mensaje de advertencia si algún campo no pudo extraerse con certeza.
     * Si es null, la extracción fue completa. El frontend debe mostrarlo al usuario.
     */
    private String advertencia;
}
