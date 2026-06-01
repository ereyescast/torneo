package com.torneo.copaestudiantil.repository;

import com.torneo.copaestudiantil.entity.EstadoFixture;
import com.torneo.copaestudiantil.entity.Fixture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FixtureRepository extends JpaRepository<Fixture, Long> {

    List<Fixture> findByOrganizadorId(Long organizadorId);

    List<Fixture> findByEdicionId(Long edicionId);

    List<Fixture> findByEdicionIdAndCategoriaId(Long edicionId, Long categoriaId);

    List<Fixture> findByOrganizadorIdAndEstado(Long organizadorId, EstadoFixture estado);

    boolean existsByEdicionIdAndNumeroFecha(Long edicionId, Integer numeroFecha);
}
