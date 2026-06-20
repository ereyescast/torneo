package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.util.SecurityUtils;
import com.torneo.copaestudiantil.dto.response.CodigoOrganizadorResponse;
import com.torneo.copaestudiantil.entity.CodigoOrganizador;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.repository.CodigoOrganizadorRepository;
import com.torneo.copaestudiantil.service.CodigoOrganizadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CodigoOrganizadorServiceImpl implements CodigoOrganizadorService {

    private final CodigoOrganizadorRepository codigoRepository;

    // Sin caracteres ambiguos (0/O, 1/I) para que sea fácil de dictar
    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RND = new SecureRandom();

    @Override
    public CodigoOrganizadorResponse generar(String nota) {
        // Solo el admin de plataforma puede generar códigos de organizador.
        if (!SecurityUtils.esAdmin())
            throw new BadRequestException("Solo el administrador de plataforma puede generar códigos.");

        CodigoOrganizador codigo = CodigoOrganizador.builder()
                .codigo(generarCodigoUnico())
                .nota(nota)
                .build();
        return toResponse(codigoRepository.save(codigo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CodigoOrganizadorResponse> listar() {
        if (!SecurityUtils.esAdmin())
            throw new BadRequestException("Solo el administrador de plataforma puede ver los códigos.");

        return codigoRepository.findAllByOrderByFechaCreacionDesc()
                .stream().map(this::toResponse).toList();
    }

    /** Código tipo "ORG-7K2M". */
    private String generarCodigoUnico() {
        String codigo;
        do {
            StringBuilder sb = new StringBuilder("ORG-");
            for (int i = 0; i < 4; i++) sb.append(ALFABETO.charAt(RND.nextInt(ALFABETO.length())));
            codigo = sb.toString();
        } while (codigoRepository.existsByCodigo(codigo));
        return codigo;
    }

    private CodigoOrganizadorResponse toResponse(CodigoOrganizador c) {
        return CodigoOrganizadorResponse.builder()
                .id(c.getId())
                .codigo(c.getCodigo())
                .nota(c.getNota())
                .estado(c.getEstado())
                .organizadorId(c.getOrganizadorId())
                .emailUsado(c.getEmailUsado())
                .fechaCreacion(c.getFechaCreacion())
                .fechaUso(c.getFechaUso())
                .build();
    }
}
