package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.common.response.CursorData;
import com.torneo.copaestudiantil.dto.request.JugadorRequest;
import com.torneo.copaestudiantil.dto.request.search.JugadorSearchRequest;
import com.torneo.copaestudiantil.dto.response.JugadorResponse;
import org.springframework.web.multipart.MultipartFile;



public interface JugadorService {
    CursorData<JugadorResponse> search(JugadorSearchRequest request);
    JugadorResponse obtenerPorId(Long id);
    JugadorResponse obtenerPorDocumento(String numeroDocumento);
    JugadorResponse crear(JugadorRequest request);
    JugadorResponse actualizar(Long id, JugadorRequest request);
    void desactivar(Long id);
    JugadorResponse subirImagen(Long id, MultipartFile file, boolean consentimiento);
}
