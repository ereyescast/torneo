package com.torneo.copaestudiantil.service;

import com.torneo.copaestudiantil.dto.response.CodigoOrganizadorResponse;

import java.util.List;

public interface CodigoOrganizadorService {

    /** Admin de plataforma: genera un nuevo código de invitación (nota opcional). */
    CodigoOrganizadorResponse generar(String nota);

    /** Admin de plataforma: lista todos los códigos (para ver cuáles se usaron). */
    List<CodigoOrganizadorResponse> listar();
}
