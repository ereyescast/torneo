package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.entity.Tecnico;

import java.util.List;
import java.util.Optional;

public interface TecnicoService {

    Tecnico registrarTecnico(Tecnico tecnico);

    List<Tecnico> listarTecnicos();

    Optional<Tecnico> buscarPorNombre (String nombres);

    Optional<Tecnico> buscarPorId (Long id);

    Tecnico actualizarTecnico (Long idTecnico, Tecnico tecnico);
    
    void eliminarTecnico(Long idTecnico);
}
