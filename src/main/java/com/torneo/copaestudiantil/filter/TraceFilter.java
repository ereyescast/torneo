package com.torneo.copaestudiantil.filter;

import com.torneo.copaestudiantil.common.trace.TraceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Intercepta cada request HTTP y:
 * 1. Genera un traceId único (o lo toma del header si viene de otro servicio)
 * 2. Genera un requestId único por request
 * 3. Los almacena en TraceContext para uso en toda la cadena
 * 4. Los agrega a los headers de respuesta para trazabilidad
 */
@Component
@Order(1) // Ejecutar antes que JwtAuthFilter
public class TraceFilter extends OncePerRequestFilter {

    private static final String HEADER_TRACE_ID   = "X-Trace-Id";
    private static final String HEADER_REQUEST_ID = "X-Request-Id";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // Si viene de otro servicio, reusar su traceId; sino generar uno nuevo
            String traceId = request.getHeader(HEADER_TRACE_ID);
            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString();
            }

            // Siempre generar un requestId nuevo por request
            String requestId = UUID.randomUUID().toString();

            TraceContext.setTraceId(traceId);
            TraceContext.setRequestId(requestId);

            // Agregar a los headers de respuesta para que el cliente pueda rastrear
            response.setHeader(HEADER_TRACE_ID, traceId);
            response.setHeader(HEADER_REQUEST_ID, requestId);

            filterChain.doFilter(request, response);

        } finally {
            // SIEMPRE limpiar el ThreadLocal al terminar — evita memory leaks
            TraceContext.clear();
        }
    }
}
