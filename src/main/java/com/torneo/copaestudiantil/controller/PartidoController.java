package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.dto.request.PartidoRequest;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.TablaPosicionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/partidos")
@RequiredArgsConstructor
public class PartidoController {

    private final PartidoRepository partidoRepository;
    private final EdicionTorneoRepository edicionTorneoRepository;
    private final CategoriaRepository categoriaRepository;
    private final SedeRepository sedeRepository;
    private final EquipoRepository equipoRepository;
    private final GrupoRepository grupoRepository;
    private final TablaPosicionService tablaPosicionService;

    private EquipoResponse toEquipoResponse(Equipo e) {
        return EquipoResponse.builder()
                .id(e.getId())
                .organizadorId(e.getOrganizadorId())
                .nombre(e.getNombre())
                .logoUrl(e.getLogoUrl())
                .activo(e.getActivo())
                .edicion(EdicionTorneoResponse.builder()
                        .id(e.getEdicion().getId())
                        .nombre(e.getEdicion().getNombre())
                        .fechaInicio(e.getEdicion().getFechaInicio())
                        .fechaFin(e.getEdicion().getFechaFin())
                        .activa(e.getEdicion().getActiva())
                        .build())
                .categoria(CategoriaResponse.builder()
                        .id(e.getCategoria().getId())
                        .anioNacimiento(e.getCategoria().getAnioNacimiento())
                        .modalidad(e.getCategoria().getModalidad())
                        .nivel(e.getCategoria().getNivel())
                        .activa(e.getCategoria().getActiva())
                        .build())
                .sede(SedeResponse.builder()
                        .id(e.getSede().getId())
                        .nombre(e.getSede().getNombre())
                        .direccion(e.getSede().getDireccion())
                        .activa(e.getSede().getActiva())
                        .build())
                .build();
    }

    private PartidoResponse toResponse(Partido p) {
        return PartidoResponse.builder()
                .id(p.getId())
                .organizadorId(p.getOrganizadorId())
                .edicion(EdicionTorneoResponse.builder()
                        .id(p.getEdicion().getId())
                        .nombre(p.getEdicion().getNombre())
                        .fechaInicio(p.getEdicion().getFechaInicio())
                        .fechaFin(p.getEdicion().getFechaFin())
                        .activa(p.getEdicion().getActiva())
                        .build())
                .categoria(CategoriaResponse.builder()
                        .id(p.getCategoria().getId())
                        .anioNacimiento(p.getCategoria().getAnioNacimiento())
                        .modalidad(p.getCategoria().getModalidad())
                        .nivel(p.getCategoria().getNivel())
                        .activa(p.getCategoria().getActiva())
                        .build())
                .sede(SedeResponse.builder()
                        .id(p.getSede().getId())
                        .nombre(p.getSede().getNombre())
                        .direccion(p.getSede().getDireccion())
                        .activa(p.getSede().getActiva())
                        .build())
                .equipoLocal(toEquipoResponse(p.getEquipoLocal()))
                .equipoVisitante(toEquipoResponse(p.getEquipoVisitante()))
                .fechaHora(p.getFechaHora())
                .golesLocal(p.getGolesLocal())
                .golesVisitante(p.getGolesVisitante())
                .estado(p.getEstado())
                .fase(p.getFase())
                .grupoId(p.getGrupo() != null ? p.getGrupo().getId() : null)
                .activo(p.getActivo())
                .build();
    }

    // --- CONSULTAS ---

    @GetMapping
    public ResponseEntity<List<PartidoResponse>> listar(
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId) {
        return ResponseEntity.ok(
                partidoRepository.findByEdicionIdAndCategoriaId(edicionId, categoriaId)
                        .stream().map(this::toResponse).collect(Collectors.toList())
        );
    }

    /**
     * Listar partidos de una fase específica.
     * GET /api/partidos/fase?edicionId=1&categoriaId=2&fase=SEMIFINAL_ORO
     */
    @GetMapping("/fase")
    public ResponseEntity<List<PartidoResponse>> listarPorFase(
            @RequestParam Long edicionId,
            @RequestParam Long categoriaId,
            @RequestParam FasePartido fase) {
        return ResponseEntity.ok(
                partidoRepository.findByEdicionIdAndCategoriaIdAndFase(edicionId, categoriaId, fase)
                        .stream().map(this::toResponse).collect(Collectors.toList())
        );
    }

    /**
     * Listar partidos de un grupo (fase GRUPOS).
     * GET /api/partidos/grupo/{grupoId}
     */
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<PartidoResponse>> listarPorGrupo(@PathVariable Long grupoId) {
        return ResponseEntity.ok(
                partidoRepository.findByGrupoId(grupoId)
                        .stream().map(this::toResponse).collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartidoResponse> buscarPorId(@PathVariable Long id) {
        return partidoRepository.findById(id)
                .map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<PartidoResponse>> listarPorEquipo(@PathVariable Long equipoId) {
        return ResponseEntity.ok(
                partidoRepository.findByEquipoLocalIdOrEquipoVisitanteId(equipoId, equipoId)
                        .stream().map(this::toResponse).collect(Collectors.toList())
        );
    }

    // --- CREACIÓN ---

    @PostMapping
    public ResponseEntity<PartidoResponse> crear(@RequestBody PartidoRequest request) {
        EdicionTorneo edicion = edicionTorneoRepository.findById(request.getEdicionId())
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        Sede sede = sedeRepository.findById(request.getSedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));
        Equipo local = equipoRepository.findById(request.getEquipoLocalId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo local no encontrado"));
        Equipo visitante = equipoRepository.findById(request.getEquipoVisitanteId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo visitante no encontrado"));

        FasePartido fase = request.getFase() != null ? request.getFase() : FasePartido.GRUPOS;

        // Resolver grupo: obligatorio solo cuando la fase es GRUPOS
        Grupo grupo = null;
        if (FasePartido.GRUPOS.equals(fase)) {
            if (request.getGrupoId() == null) {
                throw new BadRequestException("grupoId es obligatorio para partidos de fase GRUPOS");
            }
            grupo = grupoRepository.findById(request.getGrupoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
        }

        Partido partido = Partido.builder()
                .organizadorId(request.getOrganizadorId())
                .edicion(edicion)
                .categoria(categoria)
                .sede(sede)
                .equipoLocal(local)
                .equipoVisitante(visitante)
                .fechaHora(request.getFechaHora())
                .estado(request.getEstado() != null ? request.getEstado() : EstadoPartido.PROGRAMADO)
                .fase(fase)
                .grupo(grupo)
                .activo(true)
                .build();

        return ResponseEntity.ok(toResponse(partidoRepository.save(partido)));
    }

    // --- ACCIONES DE ESTADO ---

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<PartidoResponse> iniciar(@PathVariable Long id) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
        if (!EstadoPartido.PROGRAMADO.equals(partido.getEstado())) {
            throw new BadRequestException("El partido no está en estado PROGRAMADO");
        }
        partido.setEstado(EstadoPartido.EN_JUEGO);
        return ResponseEntity.ok(toResponse(partidoRepository.save(partido)));
    }

    @PutMapping("/{id}/suspender")
    public ResponseEntity<PartidoResponse> suspender(@PathVariable Long id) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
        partido.setEstado(EstadoPartido.SUSPENDIDO);
        return ResponseEntity.ok(toResponse(partidoRepository.save(partido)));
    }

    @PutMapping("/{id}/wo")
    public ResponseEntity<PartidoResponse> registrarWo(
            @PathVariable Long id,
            @RequestParam Long equipoWoId) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
        if (partido.getEquipoLocal().getId().equals(equipoWoId)) {
            partido.setGolesLocal(0);
            partido.setGolesVisitante(2);
        } else {
            partido.setGolesLocal(2);
            partido.setGolesVisitante(0);
        }
        partido.setEstado(EstadoPartido.WO);
        Partido guardado = partidoRepository.save(partido);
        tablaPosicionService.actualizarTablaAlFinalizarPartido(guardado);
        return ResponseEntity.ok(toResponse(guardado));
    }

    @PutMapping("/{id}/resultado")
    public ResponseEntity<PartidoResponse> registrarResultado(
            @PathVariable Long id,
            @RequestParam Integer golesLocal,
            @RequestParam Integer golesVisitante) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
        if (!EstadoPartido.EN_JUEGO.equals(partido.getEstado())) {
            throw new BadRequestException("El partido debe estar EN_JUEGO para registrar resultado");
        }
        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        partido.setEstado(EstadoPartido.FINALIZADO);
        Partido guardado = partidoRepository.save(partido);
        tablaPosicionService.actualizarTablaAlFinalizarPartido(guardado);
        return ResponseEntity.ok(toResponse(guardado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
        partido.setActivo(false);
        partidoRepository.save(partido);
        return ResponseEntity.ok().build();
    }
}