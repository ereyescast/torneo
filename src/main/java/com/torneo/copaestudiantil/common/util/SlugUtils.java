package com.torneo.copaestudiantil.common.util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Genera slugs (códigos públicos) a partir de un nombre.
 *
 * "Copa Estudiantil Callao"  → "copa-estudiantil-callao"
 * "Bundesliga Kids Perú"     → "bundesliga-kids-peru"
 *
 * Usado para las URLs públicas que se comparten por WhatsApp.
 */
public final class SlugUtils {

    private SlugUtils() {}

    public static String generar(String texto) {
        if (texto == null || texto.isBlank()) return "torneo";

        // Quitar tildes y acentos
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return normalizado
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")  // solo letras, números, espacios y guiones
                .replaceAll("\\s+", "-")           // espacios → guiones
                .replaceAll("-+", "-")             // guiones múltiples → uno solo
                .replaceAll("^-|-$", "");          // quitar guiones al inicio/fin
    }

    /**
     * Normaliza texto para BÚSQUEDA: quita tildes y pasa a minúsculas,
     * conservando los espacios (a diferencia de generar(), que los
     * convierte en guiones).
     *
     * "Liga Sábados Lima"  → "liga sabados lima"
     *
     * Permite que una búsqueda sin tildes ("sabados") encuentre el
     * nombre con tildes ("Sábados").
     */
    public static String normalizarBusqueda(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
