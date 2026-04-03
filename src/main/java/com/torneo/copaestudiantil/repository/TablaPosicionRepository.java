package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.Equipo;
import com.torneo.copaestudiantil.entity.TablaPosicion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TablaPosicionRepository extends JpaRepository<TablaPosicion, Long> {

    Optional<TablaPosicion> findByEquipoIdAndEdicionIdAndCategoriaId(
            Long equipoId,
            Long edicionId,
            Long categoriaId
    );

    List<TablaPosicion> findByEdicionIdAndCategoriaIdOrderByPuntosDescDiferenciaGolDescGolesFavorDesc(
            Long edicionId,
            Long categoriaId
    );
}