package com.torneo.copaestudiantil.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.trace.TraceContext;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Wrapper estándar para TODOS los responses de la API.
 * Incluye trazabilidad, código de negocio y datos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int status;
    private String traceId;
    private String requestId;
    private Long organizadorId;
    private String codigo;
    private String mensaje;
    private String descripcion;
    private LocalDateTime timestamp;
    private T data;

    // ── Factory methods para uso rápido ──────────────────────────────────────

    public static <T> ApiResponse<T> ok(T data, CodigoNegocio codigo) {
        return ApiResponse.<T>builder()
                .status(200)
                .traceId(TraceContext.getTraceId())
                .requestId(TraceContext.getRequestId())
                .organizadorId(TraceContext.getOrganizadorId())
                .codigo(codigo.getCodigo())
                .mensaje("Operación exitosa")
                .descripcion(codigo.getDescripcion())
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data, CodigoNegocio codigo) {
        return ApiResponse.<T>builder()
                .status(201)
                .traceId(TraceContext.getTraceId())
                .requestId(TraceContext.getRequestId())
                .organizadorId(TraceContext.getOrganizadorId())
                .codigo(codigo.getCodigo())
                .mensaje("Recurso creado exitosamente")
                .descripcion(codigo.getDescripcion())
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static ApiResponse<Void> noContent(CodigoNegocio codigo) {
        return ApiResponse.<Void>builder()
                .status(204)
                .traceId(TraceContext.getTraceId())
                .requestId(TraceContext.getRequestId())
                .organizadorId(TraceContext.getOrganizadorId())
                .codigo(codigo.getCodigo())
                .mensaje("Operación exitosa")
                .descripcion(codigo.getDescripcion())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(int status, CodigoNegocio codigo, String descripcion) {
        return ApiResponse.<T>builder()
                .status(status)
                .traceId(TraceContext.getTraceId())
                .requestId(TraceContext.getRequestId())
                .organizadorId(TraceContext.getOrganizadorId())
                .codigo(codigo.getCodigo())
                .mensaje("Error en la operación")
                .descripcion(descripcion)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
