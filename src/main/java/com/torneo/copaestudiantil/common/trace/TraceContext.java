package com.torneo.copaestudiantil.common.trace;

public class TraceContext {

    private static final ThreadLocal<String> TRACE_ID    = new ThreadLocal<>();
    private static final ThreadLocal<String> REQUEST_ID  = new ThreadLocal<>();
    private static final ThreadLocal<Long>   ORGANIZADOR = new ThreadLocal<>();

    public static void setTraceId(String v)      { TRACE_ID.set(v); }
    public static void setRequestId(String v)    { REQUEST_ID.set(v); }
    public static void setOrganizadorId(Long v)  { ORGANIZADOR.set(v); }

    public static String getTraceId()       { return TRACE_ID.get(); }
    public static String getRequestId()     { return REQUEST_ID.get(); }
    public static Long   getOrganizadorId() { return ORGANIZADOR.get(); }

    public static void clear() {
        TRACE_ID.remove();
        REQUEST_ID.remove();
        ORGANIZADOR.remove();
    }
}
