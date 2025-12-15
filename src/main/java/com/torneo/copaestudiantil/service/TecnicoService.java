package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.TecnicoDTO;
import com.torneo.copaestudiantil.dto.TecnicoRequest;
import com.torneo.copaestudiantil.entity.Tecnico;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface TecnicoService {

    TecnicoDTO registrarTecnico(TecnicoRequest request);

    List<Tecnico> listarTecnicos();

    Optional<Tecnico> buscarPorNombre (String nombres);

    Optional<Tecnico> buscarPorId (Long id);

    Tecnico actualizarTecnico (Long idTecnico, TecnicoRequest request);
    
    void eliminarTecnico(Long idTecnico);


}
