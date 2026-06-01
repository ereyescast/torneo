package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.ConfiguracionCancha;
import com.torneo.copaestudiantil.entity.ModalidadJuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfiguracionCanchaRepository extends JpaRepository<ConfiguracionCancha, Long> {

    List<ConfiguracionCancha> findByFixtureId(Long fixtureId);

    List<ConfiguracionCancha> findByFixtureIdAndModalidad(Long fixtureId, ModalidadJuego modalidad);

    void deleteByFixtureId(Long fixtureId);
}
