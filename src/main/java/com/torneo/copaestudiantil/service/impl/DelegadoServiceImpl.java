package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.common.util.SecurityUtils;
import com.torneo.copaestudiantil.dto.response.DelegadoResponse;
import com.torneo.copaestudiantil.entity.Delegado;
import com.torneo.copaestudiantil.entity.Equipo;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.DelegadoRepository;
import com.torneo.copaestudiantil.repository.EquipoRepository;
import com.torneo.copaestudiantil.service.DelegadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class DelegadoServiceImpl implements DelegadoService {

    private final DelegadoRepository delegadoRepository;
    private final EquipoRepository equipoRepository;

    // Sin caracteres ambiguos (0/O, 1/I) para que sea fácil de dictar
    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RND = new SecureRandom();

    @Override
    public DelegadoResponse invitar(Long equipoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        SecurityUtils.validarPertenencia(equipo.getOrganizadorId());

        // Si ya existe el delegado del equipo, reusa su código (no se regenera).
        Delegado delegado = delegadoRepository.findByEquipoId(equipoId).orElse(null);
        if (delegado == null) {
            delegado = Delegado.builder()
                    .equipoId(equipoId)
                    .organizadorId(equipo.getOrganizadorId())
                    .codigoInvitacion(generarCodigoUnico(equipo.getNombre()))
                    .build();
            delegado = delegadoRepository.save(delegado);
        }
        return toResponse(delegado);
    }

    @Override
    public DelegadoResponse obtenerPorEquipo(Long equipoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        SecurityUtils.validarPertenencia(equipo.getOrganizadorId());

        return delegadoRepository.findByEquipoId(equipoId)
                .map(this::toResponse)
                .orElse(null);
    }

    /** Código tipo "RFC-7K2M": prefijo del nombre del equipo + 4 aleatorios. */
    private String generarCodigoUnico(String nombreEquipo) {
        String prefijo = (nombreEquipo == null ? "EQ" : nombreEquipo)
                .replaceAll("[^A-Za-z]", "").toUpperCase();
        prefijo = (prefijo.length() >= 3 ? prefijo.substring(0, 3) : (prefijo + "EQ").substring(0, 3));

        String codigo;
        do {
            StringBuilder sb = new StringBuilder(prefijo).append('-');
            for (int i = 0; i < 4; i++) sb.append(ALFABETO.charAt(RND.nextInt(ALFABETO.length())));
            codigo = sb.toString();
        } while (delegadoRepository.existsByCodigoInvitacion(codigo));
        return codigo;
    }

    private DelegadoResponse toResponse(Delegado d) {
        return DelegadoResponse.builder()
                .id(d.getId())
                .equipoId(d.getEquipoId())
                .nombres(d.getNombres())
                .apellidosPaterno(d.getApellidosPaterno())
                .apellidosMaterno(d.getApellidosMaterno())
                .email(d.getEmail())
                .codigoInvitacion(d.getCodigoInvitacion())
                .estado(d.getEstado())
                .activo(d.getActivo())
                .build();
    }
}
