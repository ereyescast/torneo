package com.torneo.copaestudiantil.mapper;

import com.torneo.copaestudiantil.dto.request.TecnicoRequest;
import com.torneo.copaestudiantil.dto.response.TecnicoResponse;
import com.torneo.copaestudiantil.entity.Tecnico;
import org.springframework.stereotype.Component;

@Component
public class TecnicoMapper {

    public Tecnico toEntity(TecnicoRequest request) {
        return Tecnico.builder()
                .nombres(request.getNombres())
                .apellidosPaterno(request.getApellidosPaterno())
                .apellidosMaterno(request.getApellidosMaterno())
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .nacionalidad(request.getNacionalidad())
                .fechaNac(request.getFechaNac())
                .activo(true)
                .build();
    }

    public TecnicoResponse toResponse(Tecnico tecnico) {
        return TecnicoResponse.builder()
                .id(tecnico.getId())
                .nombres(tecnico.getNombres())
                .apellidosPaterno(tecnico.getApellidosPaterno())
                .apellidosMaterno(tecnico.getApellidosMaterno())
                .tipoDocumento(tecnico.getTipoDocumento())
                .numeroDocumento(tecnico.getNumeroDocumento())
                .nacionalidad(tecnico.getNacionalidad())
                .fechaNac(tecnico.getFechaNac())
                .profileImage(tecnico.getProfileImage())
                .build();
    }
}