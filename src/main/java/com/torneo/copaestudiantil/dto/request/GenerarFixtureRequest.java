package com.torneo.copaestudiantil.dto.request;

import lombok.*;

/** organizadorId sale del token en el service. */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GenerarFixtureRequest {
    private Long grupoId;
}
