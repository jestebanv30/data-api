package com.data.datafacturador.service.facturacion;

import com.data.datafacturador.dto.facturacion.PdfDianExtractionDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrae automáticamente los datos de configuración de una resolución DIAN
 * a partir del PDF oficial Formato 1876 "Autorización Numeración de Facturación".
 *
 * El PDF tiene 2 páginas:
 *  - Página 2: La tabla con casillas 30 (modalidad), 31 (prefijo), 32 (desde), 33 (hasta), 38 (vigencia).
 *  - Página 2 (parte inferior): Sello circular DIAN con "Fecha Acuse de Recibo / Firmado".
 *  - Página 1: Número de formulario (17 dígitos) como identificador único de la resolución.
 */
@Slf4j
@Service
public class PdfDianExtractionService {

    private static final int MAX_PDF_SIZE_BYTES = 6 * 1024 * 1024; // 6 MB
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PdfDianExtractionDTO extraer(MultipartFile file) throws IOException {
        validarArchivo(file);

        byte[] bytes = file.getBytes();
        String textoCombinado = extraerTextoCompleto(bytes);

        log.warn("=== TEXTO PDF CRUDO (PAG 1+2) ===\n{}\n=== FIN TEXTO ===", textoCombinado);

        List<String> advertencias = new ArrayList<>();

        // --- Número de formulario (Casilla 4) → numeroResolucion ---
        String numeroResolucion = extraerNumeroFormulario(textoCombinado, advertencias);

        // --- Fila de datos (Modalidad, Prefijo, Rangos, Vigencia) — una sola pasada ---
        DatosFila fila = extraerDatosFila(textoCombinado, advertencias);

        // --- Fecha Acuse de Recibo (sello circular de la DIAN) ---
        LocalDate fechaDesde = extraerFechaAcuse(textoCombinado, advertencias);

        // --- Calcular Fecha Hasta ---
        LocalDate fechaHasta = null;
        if (fechaDesde != null && fila != null && fila.vigenciaMeses() != null) {
            fechaHasta = fechaDesde.plusMonths(fila.vigenciaMeses());
        } else {
            advertencias.add("No se pudo calcular la fecha de vencimiento (faltó fecha o vigencia).");
        }

        String advertenciaFinal = advertencias.isEmpty() ? null : String.join(" | ", advertencias);

        return PdfDianExtractionDTO.builder()
                .numeroResolucion(numeroResolucion)
                .prefijo(fila != null ? fila.prefijo() : null)
                .rangoDesde(fila != null ? fila.rangoDesde() : null)
                .rangoHasta(fila != null ? fila.rangoHasta() : null)
                .fechaDesde(fechaDesde)
                .fechaHasta(fechaHasta)
                .tipoDocumentoDetectado(fila != null ? fila.modalidad() : null)
                .vigenciaMeses(fila != null ? fila.vigenciaMeses() : null)
                .advertencia(advertenciaFinal)
                .build();
    }

    // -----------------------------------------------------------------------
    // Extractores por campo — basados en la estructura REAL del PDF DIAN
    // -----------------------------------------------------------------------

    /**
     * "4. Número de formulario 18764081810977"
     * La palabra clave es "formulario" (sin tilde en el regex para evitar problemas de encoding).
     */
    private String extraerNumeroFormulario(String texto, List<String> adv) {
        Pattern p = Pattern.compile("formulario\\s+([0-9]{14,20})", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(texto);
        if (m.find()) return m.group(1).trim();

        // Fallback: cualquier número largo que empiece en 1876 (código del formulario DIAN)
        Pattern pFallback = Pattern.compile("\\b(1876[0-9]{11,16})\\b");
        Matcher mFallback = pFallback.matcher(texto);
        if (mFallback.find()) return mFallback.group(1);

        adv.add("No se encontró el número de formulario DIAN.");
        return null;
    }

    /**
     * La DIAN organiza los datos en una tabla cuya fila activa tiene este formato:
     *   MODALIDAD  <cod>  <PREFIJO>  <rangoDesde>  <rangoHasta>  <vigencia>  AUTORIZACIÓN  <cod>
     *
     * Ejemplo real del PDF:
     *   "DOCUMENTO SOPORTE 6 DSDS 1 2,000 24 AUTORIZACIÓN  1"
     *   "FACTURA ELECTRÓNICA DE VENTA 1 FE 1 1,000 34 AUTORIZACIÓN 1"
     *
     * Este método extrae todos los campos de esa fila en una sola pasada.
     */
    private DatosFila extraerDatosFila(String texto, List<String> adv) {
        // Detectamos la fila que contiene datos reales.
        // El patron es: <MODALIDAD> <codigo 1-2 dígitos> <PREFIJO> <número> <número con coma> <vigencia>
        String patronModalidad =
            "(FACTURA\\s+ELECTR[OÓoó]NICA(?:\\s+DE\\s+VENTA)?|" +
            "DOCUMENTO\\s+SOPORTE|" +
            "NOTA\\s+CR[EÉeé]DITO|" +
            "NOTA\\s+D[EÉeé]BITO|" +
            "N[OÓoó]MINA\\s+ELECTR[OÓoó]NICA|" +
            "DOCUMENTO\\s+EQUIVALENTE|" +
            "RECIBO\\s+ELECTR[OÓoó]NICO|" +
            "TIQUETE\\s+DE\\s+MAQUINA)";

        Pattern p = Pattern.compile(
            patronModalidad +
            "\\s+(\\d{1,2})" +          // Grupo 2: código modalidad (ej: 6, 1)
            "\\s+([A-Z0-9]{2,12})" +     // Grupo 3: prefijo (ej: DSDS, FE, SETP)
            "\\s+([0-9]{1,10})" +        // Grupo 4: rango desde (ej: 1)
            "\\s+([0-9][0-9,.]*)" +      // Grupo 5: rango hasta (ej: 2,000)
            "\\s+([0-9]{1,3})",          // Grupo 6: vigencia en meses (ej: 24)
            Pattern.CASE_INSENSITIVE
        );

        Matcher m = p.matcher(texto);
        if (m.find()) {
            return new DatosFila(
                m.group(1).trim().toUpperCase().replaceAll("\\s+", " "),
                m.group(3).trim().toUpperCase(),
                parsearNumero(m.group(4)),
                parsearNumero(m.group(5)),
                parseInteger(m.group(6))
            );
        }

        adv.add("No se pudo extraer la fila de datos de la resolución del PDF.");
        return null;
    }

    /** Registro con los datos de la fila activa de la tabla DIAN. */
    private record DatosFila(
        String modalidad,
        String prefijo,
        BigDecimal rangoDesde,
        BigDecimal rangoHasta,
        Integer vigenciaMeses
    ) {}

    /**
     * Extrae la fecha del sello circular "Fecha Acuse de Recibo - Firmado".
     * En el PDF aparece como "2024-10-19 / 11:38:26 AM" o en línea de formalización.
     */
    private LocalDate extraerFechaAcuse(String texto, List<String> adv) {
        // Patrón principal: fecha con hora (sello DIAN)
        Pattern p1 = Pattern.compile("(20[0-9]{2}-[0-1][0-9]-[0-3][0-9])\\s*/\\s*[0-9]{1,2}:[0-9]{2}:[0-9]{2}");
        Matcher m1 = p1.matcher(texto);
        if (m1.find()) {
            return LocalDate.parse(m1.group(1), DATE_FMT);
        }

        // Fallback: "997. Fecha formalización 2024-10-19"
        Pattern pFormal = Pattern.compile("(?i)formalizaci.n[\\s:]*([0-9]{4}-[0-1][0-9]-[0-3][0-9])");
        Matcher mFormal = pFormal.matcher(texto);
        if (mFormal.find()) {
            try { return LocalDate.parse(mFormal.group(1), DATE_FMT); } catch (Exception ignored) {}
        }

        // Fallback amplio: cualquier fecha YYYY-MM-DD
        Pattern p2 = Pattern.compile("\\b(20[0-9]{2}-[0-1][0-9]-[0-3][0-9])\\b");
        Matcher m2 = p2.matcher(texto);
        if (m2.find()) {
            try { return LocalDate.parse(m2.group(1), DATE_FMT); } catch (Exception ignored) {}
        }

        adv.add("No se encontró la fecha del acuse de recibo.");
        return null;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private String extraerPagina(PDDocument doc, PDFTextStripper stripper, int page) throws IOException {
        stripper.setStartPage(page);
        stripper.setEndPage(page);
        return stripper.getText(doc);
    }

    /** Extrae y normaliza el texto completo del PDF (todas las páginas). */
    public String extraerTextoCompleto(byte[] bytes) throws IOException {
        String textoPagina1;
        String textoPagina2;
        try (PDDocument doc = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            textoPagina1 = extraerPagina(doc, stripper, 1);
            textoPagina2 = doc.getNumberOfPages() >= 2
                    ? extraerPagina(doc, stripper, 2)
                    : "";
        }
        return normalizarEspaciosDIAN(textoPagina1 + "\n" + textoPagina2);
    }

    /**
     * El texto de los PDFs de la DIAN frecuentemente tiene espacios entre cada carácter
     * (ej: "2 0 2 4 - 1 0 - 1 9"). Este método los elimina para los patrones de fechas y números.
     */
    private String normalizarEspaciosDIAN(String texto) {
        if (texto == null) return "";

        // Normalizar fechas: "2 0 2 4 -1 0 -1 9" → "2024-10-19"
        // Patrón: dígito espacio(s) dígito repetido (para grupos de fecha)
        String resultado = texto;

        // Paso 1: Colapsar grupos de dígitos separados por espacios simples (caracteres de fecha/número)
        resultado = resultado.replaceAll("(\\d) (\\d) (\\d) (\\d) -(\\d) (\\d) -(\\d) (\\d)", "$1$2$3$4-$5$6-$7$8");
        resultado = resultado.replaceAll("(\\d) (\\d) (\\d) (\\d)-(\\d) (\\d)-(\\d) (\\d)", "$1$2$3$4-$5$6-$7$8");

        // Paso 2: Colapsar espacios entre dígitos en patrones de hora "1 1 :3 8 :2 6"
        resultado = resultado.replaceAll("(\\d) (\\d) :(\\d) (\\d) :(\\d) (\\d)", "$1$2:$3$4:$5$6");
        resultado = resultado.replaceAll("(\\d) (\\d):(\\d) (\\d):(\\d) (\\d)", "$1$2:$3$4:$5$6");

        return resultado;
    }

    private BigDecimal parsearNumero(String valor) {
        if (valor == null) return null;
        // Quitar comas (separadores de miles) y espacios
        String limpio = valor.replaceAll("[,\\s]", "");
        try { return new BigDecimal(limpio); } catch (NumberFormatException e) { return null; }
    }

    private Integer parseInteger(String valor) {
        if (valor == null) return null;
        try { return Integer.parseInt(valor.trim()); } catch (NumberFormatException e) { return null; }
    }

    private void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo PDF no puede estar vacío.");
        }
        if (file.getSize() > MAX_PDF_SIZE_BYTES) {
            throw new IllegalArgumentException("El archivo PDF excede el tamaño máximo permitido (6 MB).");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            // Algunos navegadores pueden enviar "application/octet-stream" para PDFs
            String nombre = file.getOriginalFilename();
            if (nombre == null || !nombre.toLowerCase().endsWith(".pdf")) {
                throw new IllegalArgumentException("Solo se aceptan archivos PDF.");
            }
        }
    }
}
