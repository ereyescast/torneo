package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.RolUsuario;
import com.torneo.copaestudiantil.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);

    /** Para la siembra del admin: ¿ya existe al menos un usuario con este rol? */
    boolean existsByRol(RolUsuario rol);
}
