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
}
