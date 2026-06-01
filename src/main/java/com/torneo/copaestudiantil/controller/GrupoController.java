package com.torneo.copaestudiantil.controller;

import com.torneo.copaestudiantil.common.codigo.CodigoNegocio;
import com.torneo.copaestudiantil.common.response.ApiResponse;
import com.torneo.copaestudiantil.dto.response.*;
import com.torneo.copaestudiantil.entity.*;
import com.torneo.copaestudiantil.exceptions.BadRequestException;
import com.torneo.copaestudiantil.exceptions.ResourceNotFoundException;
import com.torneo.copaestudiantil.repository.*;
import com.torneo.copaestudiantil.service.TablaPosicionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "10. Grupos", description = "Grupos de la fase de grupos del torneo")
@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoRepository grupoRepository;
    private final GrupoEquipoRepository grupoEquipoRepository;
    private final EdicionTorneoRepository edicionTorneoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EquipoRepository equipoRepository;
    private final TablaPosicionService tablaPosicionService;

    private GrupoResponse toGrupoResponse(Grupo g) {
        EdicionTorneo e = g.getEdicion();
        Categoria c = g.getCategoria();
        return GrupoResponse.builder()
                .id(g.getId()).organizadorId(g.getOrganizadorId())
                .nombre(g.getNombre()).activo(g.getActivo())
                .edicion(EdicionTorneoResponse.builder()
                        .id(e.getId()).nombre(e.getNombre())
                        .fechaInicio(e.getFechaInicio()).fechaFin(e.getFechaFin())
                        .activa(e.getActiva()).build())
                .categoria(CategoriaResponse.builder()
                        .id(c.getId()).anioNacimiento(c.getAnioNacimiento())
                        .modalidad(c.getModalidad()).nivel(c.getNivel())
                        .activa(c.getActiva()).build())
                .build();
    }

    private GrupoEquipoResponse toGrupoEquipoResponse(GrupoEquipo ge) {
        Equipo eq = ge.getEquipo();
        return GrupoEquipoResponse.builder()
                .id(ge.getId()).activo(ge.getActivo())
                .grupo(toGrupoResponse(ge.getGrupo()))
                .equipo(EquipoResponse.builder()
                        .id(eq.getId()).nombre(eq.getNombre())
                        .organizadorId(eq.getOrganizadorId())
                        .logoUrl(eq.getLogoUrl()).activo(eq.getActivo()).build())
                .build();
    }

    @Operation(summary = "Listar grupos por edición y categoría")
    @GetMapping
    public ResponseEntity<ApiResponse<List<GrupoResponse>>> listar(
            @RequestParam Long edicionId, @RequestParam Long categoriaId) {
        List<GrupoResponse> result = grupoRepository
                .findByEdicionIdAndCategoriaIdAndActivoTrue(edicionId, categoriaId)
                .stream().map(this::toGrupoResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(result, CodigoNegocio.S_GRU_200_002));
    }

    @Operation(summary = "Obtener grupo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GrupoResponse>> buscarPorId(
            @Parameter(description = "ID del grupo") @PathVariable Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
        return ResponseEntity.ok(ApiResponse.ok(toGrupoResponse(grupo), CodigoNegocio.S_GRU_200_001));
    }

    @Operation(summary = "Crear grupo", description = "Ej: Grupo A, Grupo B")
    @PostMapping
    public ResponseEntity<ApiResponse<GrupoResponse>> crear(
            @RequestParam Long organizadorId, @RequestParam Long edicionId,
            @RequestParam Long categoriaId, @RequestParam String nombre) {

        EdicionTorneo edicion = edicionTorneoRepository.findById(edicionId)
                .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        Grupo grupo = Grupo.builder()
                .organizadorId(organizadorId).edicion(edicion)
                .categoria(categoria).nombre(nombre).activo(true).build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toGrupoResponse(grupoRepository.save(grupo)),
                        CodigoNegocio.S_GRU_201_001));
    }

    @Operation(
            summary = "Agregar equipo al grupo",
            description = "También inicializa la fila del equipo en la tabla de posiciones"
    )
    @PostMapping("/{grupoId}/equipos/{equipoId}")
    public ResponseEntity<ApiResponse<GrupoEquipoResponse>> agregarEquipo(
            @PathVariable Long grupoId, @PathVariable Long equipoId) {

        if (grupoEquipoRepository.existsByGrupoIdAndEquipoId(grupoId, equipoId))
            throw new BadRequestException("El equipo ya pertenece a este grupo");

        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));

        GrupoEquipo guardado = grupoEquipoRepository.save(
                GrupoEquipo.builder().grupo(grupo).equipo(equipo).activo(true).build());
        tablaPosicionService.inicializarEquipo(equipo, grupo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(toGrupoEquipoResponse(guardado),
                        CodigoNegocio.S_GRU_201_002));
    }

    @Operation(summary = "Listar equipos de un grupo")
    @GetMapping("/{grupoId}/equipos")
    public ResponseEntity<ApiResponse<List<GrupoEquipoResponse>>> listarEquipos(
            @PathVariable Long grupoId) {
        List<GrupoEquipoResponse> result = grupoEquipoRepository.findByGrupoId(grupoId)
                .stream().map(this::toGrupoEquipoResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(result, CodigoNegocio.S_GRU_200_002));
    }

    @Operation(summary = "Desactivar grupo", description = "Soft delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
        grupo.setActivo(false);
        grupoRepository.save(grupo);
        return ResponseEntity.ok(ApiResponse.noContent(CodigoNegocio.S_GRU_200_001));
    }
}
