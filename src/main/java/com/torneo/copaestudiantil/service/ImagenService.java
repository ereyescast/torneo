package com.torneo.copaestudiantil.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImagenService {

    String guardarImagenTecnico(Long tecnicoId, MultipartFile file);
}