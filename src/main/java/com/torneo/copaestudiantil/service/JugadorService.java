package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.request.JugadorRequest;
import com.torneo.copaestudiantil.dto.response.JugadorResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JugadorService {
    JugadorResponse crear(JugadorRequest request);
    JugadorResponse obtenerPorId(Long id);
    JugadorResponse obtenerPorDocumento(String numeroDocumento);
    List<JugadorResponse> listarTodos();
    JugadorResponse actualizar(Long id, JugadorRequest request);
    void desactivar(Long id);
    JugadorResponse subirImagen(Long id, MultipartFile file);
}
