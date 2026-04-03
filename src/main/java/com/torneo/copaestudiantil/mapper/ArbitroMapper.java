package com.torneo.copaestudiantil.mapper;

import com.torneo.copaestudiantil.dto.response.ArbitroResponse;
import com.torneo.copaestudiantil.entity.Arbitro;
import org.springframework.stereotype.Component;

@Component
public class ArbitroMapper {

    public ArbitroResponse toResponse(Arbitro arbitro) {
        return ArbitroResponse.builder()
                .id(arbitro.getId())
                .nombre(arbitro.getNombre())
                .telefono(arbitro.getTelefono())
                .email(arbitro.getEmail())
                .activo(arbitro.getActivo())
                .build();
    }
}