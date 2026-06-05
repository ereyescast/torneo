package com.torneo.copaestudiantil.common.util;

import com.torneo.copaestudiantil.entity.RolUsuario;
import com.torneo.copaestudiantil.entity.Usuario;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilidades para obtener información del usuario autenticado.
 *
 * Centraliza el acceso al organizadorId del usuario logueado, que es la base
 * del multi-tenancy: cada organizador solo ve y modifica sus propios datos.
 *
 * Los services usan estos métodos en lugar de recibir organizadorId del body,
 * cerrando el hueco de seguridad donde un usuario podía falsificar el ID de otro.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /** Devuelve el usuario autenticado actual, o null si no hay sesión. */
    public static Usuario getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof Usuario usuario) return usuario;
        return null;
    }

    /**
     * Devuelve el organizadorId del usuario autenticado.
     * Lanza excepción si no hay usuario o si no tiene organizador asignado.
     *
     * Úsalo en operaciones de escritura/lectura que SIEMPRE deben filtrar
     * por organizador (crear equipo, listar mis torneos, etc.).
     */
    public static Long getOrganizadorIdActual() {
        Usuario usuario = getUsuarioActual();
        if (usuario == null)
            throw new BadRequestException("No hay usuario autenticado");

        // Un ADMIN de plataforma puede no tener organizador
        if (usuario.getOrganizadorId() == null)
            throw new BadRequestException(
                    "El usuario no pertenece a ningún organizador. "
                            + "Los administradores de plataforma deben especificar el organizador.");

        return usuario.getOrganizadorId();
    }

    /** Devuelve el organizadorId del usuario, o null si es ADMIN sin organizador. */
    public static Long getOrganizadorIdOrNull() {
        Usuario usuario = getUsuarioActual();
        return usuario != null ? usuario.getOrganizadorId() : null;
    }

    /** True si el usuario autenticado es ADMIN de plataforma. */
    public static boolean esAdmin() {
        Usuario usuario = getUsuarioActual();
        return usuario != null && RolUsuario.ADMIN.equals(usuario.getRol());
    }

    /**
     * Valida que un recurso pertenezca al organizador del usuario autenticado.
     * Lanza excepción si el recurso es de otro organizador.
     *
     * Los ADMIN pueden acceder a cualquier recurso (no se valida pertenencia).
     *
     * Ejemplo de uso en un service:
     *   Equipo equipo = equipoRepository.findById(id)...;
     *   SecurityUtils.validarPertenencia(equipo.getOrganizadorId());
     */
    public static void validarPertenencia(Long organizadorIdDelRecurso) {
        if (esAdmin()) return; // el admin ve todo

        Long miOrganizador = getOrganizadorIdActual();
        if (!miOrganizador.equals(organizadorIdDelRecurso)) {
            throw new BadRequestException(
                    "No tienes acceso a este recurso. Pertenece a otro organizador.");
        }
    }
}
