package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.EstadoPago;
import com.torneo.copaestudiantil.entity.PagoEquipo;
import com.torneo.copaestudiantil.entity.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoEquipoRepository extends JpaRepository<PagoEquipo, Long> {

    List<PagoEquipo> findByEquipoId(Long equipoId);

    List<PagoEquipo> findByEquipoIdAndEdicionId(Long equipoId, Long edicionId);

    List<PagoEquipo> findByEdicionId(Long edicionId);

    List<PagoEquipo> findByEdicionIdAndEstado(Long edicionId, EstadoPago estado);

    List<PagoEquipo> findByEquipoIdAndEdicionIdAndTipoPago(
            Long equipoId, Long edicionId, TipoPago tipoPago);

    Optional<PagoEquipo> findByEquipoIdAndEdicionIdAndTipoPagoAndPartidoId(
            Long equipoId, Long edicionId, TipoPago tipoPago, Long partidoId);

    /** Equipos con deuda pendiente en una edición */
    List<PagoEquipo> findByEdicionIdAndEstadoIn(Long edicionId, List<EstadoPago> estados);
}
