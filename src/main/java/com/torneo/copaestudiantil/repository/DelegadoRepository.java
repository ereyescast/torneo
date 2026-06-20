package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Delegado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DelegadoRepository extends JpaRepository<Delegado, Long> {

    Optional<Delegado> findByCodigoInvitacion(String codigoInvitacion);
    boolean existsByCodigoInvitacion(String codigoInvitacion);

    Optional<Delegado> findByEquipoId(Long equipoId);
    Optional<Delegado> findByEquipoIdAndActivoTrue(Long equipoId);
    Optional<Delegado> findByUsuarioId(Long usuarioId);
}
