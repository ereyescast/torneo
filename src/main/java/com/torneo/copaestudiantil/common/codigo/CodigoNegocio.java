package com.torneo.copaestudiantil.common.codigo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Códigos de negocio estándar del torneo.
 *
 * Formato: [S|E]_[ENTIDAD]_[HTTP]_[SECUENCIAL]
 *   S = Success
 *   E = Error
 *
 * Entidades:
 *   JUG = Jugador
 *   EQU = Equipo
 *   CAT = Categoria
 *   SED = Sede
 *   EDI = EdicionTorneo
 *   PAR = Partido
 *   INS = Inscripcion
 *   GRU = Grupo
 *   ARB = Arbitro
 *   TEC = Tecnico
 *   AUT = Auth
 *   GEN = General
 */
@Getter
@RequiredArgsConstructor
public enum CodigoNegocio {

    // ── Auth ─────────────────────────────────────────────────────────────────
    S_AUT_200_001("S_AUT_200_001", "Login exitoso"),
    S_AUT_201_001("S_AUT_201_001", "Usuario registrado exitosamente"),
    E_AUT_401_001("E_AUT_401_001", "Credenciales inválidas"),
    E_AUT_401_002("E_AUT_401_002", "Token expirado o inválido"),
    E_AUT_403_001("E_AUT_403_001", "No tienes permisos para realizar esta acción"),
    E_AUT_409_001("E_AUT_409_001", "El email ya está registrado"),

    // ── Jugador ──────────────────────────────────────────────────────────────
    S_JUG_200_001("S_JUG_200_001", "Jugador encontrado"),
    S_JUG_200_002("S_JUG_200_002", "Jugadores recuperados exitosamente"),
    S_JUG_200_003("S_JUG_200_003", "Jugador actualizado exitosamente"),
    S_JUG_201_001("S_JUG_201_001", "Jugador creado exitosamente"),
    S_JUG_204_001("S_JUG_204_001", "Jugador desactivado exitosamente"),
    E_JUG_400_001("E_JUG_400_001", "DNI duplicado"),
    E_JUG_400_002("E_JUG_400_002", "Edad no corresponde a la categoría"),
    E_JUG_400_003("E_JUG_400_003", "Archivo de imagen inválido"),
    E_JUG_404_001("E_JUG_404_001", "Jugador no encontrado"),
    E_JUG_409_001("E_JUG_409_001", "El jugador ya está inscrito en esta edición"),

    // ── Equipo ───────────────────────────────────────────────────────────────
    S_EQU_200_001("S_EQU_200_001", "Equipo encontrado"),
    S_EQU_200_002("S_EQU_200_002", "Equipos recuperados exitosamente"),
    S_EQU_200_003("S_EQU_200_003", "Equipo actualizado exitosamente"),
    S_EQU_201_001("S_EQU_201_001", "Equipo creado exitosamente"),
    S_EQU_204_001("S_EQU_204_001", "Equipo desactivado exitosamente"),
    E_EQU_400_001("E_EQU_400_001", "Cupo máximo de jugadores alcanzado"),
    E_EQU_404_001("E_EQU_404_001", "Equipo no encontrado"),
    E_EQU_409_001("E_EQU_409_001", "Ya existe un equipo con ese nombre en esta edición"),

    // ── Categoría ─────────────────────────────────────────────────────────────
    S_CAT_200_001("S_CAT_200_001", "Categoría encontrada"),
    S_CAT_200_002("S_CAT_200_002", "Categorías recuperadas exitosamente"),
    S_CAT_200_003("S_CAT_200_003", "Categoría actualizada exitosamente"),
    S_CAT_201_001("S_CAT_201_001", "Categoría creada exitosamente"),
    S_CAT_204_001("S_CAT_204_001", "Categoría desactivada exitosamente"),
    E_CAT_404_001("E_CAT_404_001", "Categoría no encontrada"),
    E_CAT_409_001("E_CAT_409_001", "Ya existe una categoría con ese año y nivel en esta edición"),

    // ── Sede ─────────────────────────────────────────────────────────────────
    S_SED_200_001("S_SED_200_001", "Sede encontrada"),
    S_SED_200_002("S_SED_200_002", "Sedes recuperadas exitosamente"),
    S_SED_200_003("S_SED_200_003", "Sede actualizada exitosamente"),
    S_SED_201_001("S_SED_201_001", "Sede creada exitosamente"),
    S_SED_204_001("S_SED_204_001", "Sede desactivada exitosamente"),
    E_SED_404_001("E_SED_404_001", "Sede no encontrada"),

    // ── Edición ──────────────────────────────────────────────────────────────
    S_EDI_200_001("S_EDI_200_001", "Edición encontrada"),
    S_EDI_200_002("S_EDI_200_002", "Ediciones recuperadas exitosamente"),
    S_EDI_200_003("S_EDI_200_003", "Edición actualizada exitosamente"),
    S_EDI_201_001("S_EDI_201_001", "Edición creada exitosamente"),
    S_EDI_204_001("S_EDI_204_001", "Edición desactivada exitosamente"),
    E_EDI_404_001("E_EDI_404_001", "Edición no encontrada"),

    // ── Partido ──────────────────────────────────────────────────────────────
    S_PAR_200_001("S_PAR_200_001", "Partido encontrado"),
    S_PAR_200_002("S_PAR_200_002", "Partidos recuperados exitosamente"),
    S_PAR_200_003("S_PAR_200_003", "Resultado registrado exitosamente"),
    S_PAR_200_004("S_PAR_200_004", "WO registrado exitosamente"),
    S_PAR_201_001("S_PAR_201_001", "Partido creado exitosamente"),
    E_PAR_400_001("E_PAR_400_001", "El partido ya fue finalizado"),
    E_PAR_400_002("E_PAR_400_002", "WO: el equipo incurrió en falta"),
    E_PAR_404_001("E_PAR_404_001", "Partido no encontrado"),

    // ── Inscripción ──────────────────────────────────────────────────────────
    S_INS_201_001("S_INS_201_001", "Jugador inscrito exitosamente"),
    S_INS_204_001("S_INS_204_001", "Jugador desinscrito exitosamente"),
    E_INS_400_001("E_INS_400_001", "El jugador ya está inscrito en esta edición (Art. 22)"),
    E_INS_400_002("E_INS_400_002", "Cupo máximo alcanzado (Art. 11)"),
    E_INS_400_003("E_INS_400_003", "Año de nacimiento no corresponde a la categoría (Art. III)"),

    // ── Grupo ────────────────────────────────────────────────────────────────
    S_GRU_200_001("S_GRU_200_001", "Grupo encontrado"),
    S_GRU_200_002("S_GRU_200_002", "Grupos recuperados exitosamente"),
    S_GRU_201_001("S_GRU_201_001", "Grupo creado exitosamente"),
    S_GRU_201_002("S_GRU_201_002", "Equipo agregado al grupo exitosamente"),
    E_GRU_400_001("E_GRU_400_001", "El equipo ya pertenece a este grupo"),
    E_GRU_404_001("E_GRU_404_001", "Grupo no encontrado"),

    // ── Árbitro ──────────────────────────────────────────────────────────────
    S_ARB_200_001("S_ARB_200_001", "Árbitro encontrado"),
    S_ARB_200_002("S_ARB_200_002", "Árbitros recuperados exitosamente"),
    S_ARB_200_003("S_ARB_200_003", "Árbitro actualizado exitosamente"),
    S_ARB_201_001("S_ARB_201_001", "Árbitro creado exitosamente"),
    S_ARB_204_001("S_ARB_204_001", "Árbitro desactivado exitosamente"),
    E_ARB_404_001("E_ARB_404_001", "Árbitro no encontrado"),

    // ── Técnico ──────────────────────────────────────────────────────────────
    S_TEC_200_001("S_TEC_200_001", "Técnico encontrado"),
    S_TEC_200_002("S_TEC_200_002", "Técnicos recuperados exitosamente"),
    S_TEC_200_003("S_TEC_200_003", "Técnico actualizado exitosamente"),
    S_TEC_201_001("S_TEC_201_001", "Técnico creado exitosamente"),
    S_TEC_204_001("S_TEC_204_001", "Técnico desactivado exitosamente"),
    E_TEC_404_001("E_TEC_404_001", "Técnico no encontrado"),

    // ── General ──────────────────────────────────────────────────────────────
    E_GEN_400_001("E_GEN_400_001", "Solicitud inválida"),
    E_GEN_404_001("E_GEN_404_001", "Recurso no encontrado"),
    E_GEN_409_001("E_GEN_409_001", "Ya existe un registro con esos datos"),
    E_GEN_500_001("E_GEN_500_001", "Error interno del servidor");

    private final String codigo;
    private final String descripcion;
}
