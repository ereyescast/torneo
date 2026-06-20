package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.TecnicoRequest;
import com.torneo.copaestudiantil.dto.request.search.TecnicoSearchRequest;
import com.torneo.copaestudiantil.dto.response.TecnicoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface TecnicoService {

    CursorData<TecnicoResponse> search(TecnicoSearchRequest request);
    TecnicoResponse registrar(TecnicoRequest request);
    TecnicoResponse obtenerPorId(Long id);
    TecnicoResponse actualizar(Long id, TecnicoRequest request);
    void eliminar(Long id);
    TecnicoResponse subirImagen(Long id, MultipartFile file);

    /** Asigna un técnico a un equipo dentro de una edición (cuerpo técnico). */
    void asignarAEquipo(Long tecnicoId, Long equipoId, Long edicionId);
}
