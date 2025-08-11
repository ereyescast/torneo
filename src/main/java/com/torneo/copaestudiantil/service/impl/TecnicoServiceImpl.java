package com.torneo.copaestudiantil.service.impl;

import com.torneo.copaestudiantil.entity.Tecnico;
import com.torneo.copaestudiantil.repository.TecnicoRepository;
import com.torneo.copaestudiantil.service.TecnicoService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TecnicoServiceImpl implements TecnicoService {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Override
    public Tecnico registrarTecnico(Tecnico tecnico) {
        return tecnicoRepository.save(tecnico);
    }

    @Override
    public List<Tecnico> listarTecnicos() {
//        List<Tecnico> tecnicos = tecnicoRepository.findAll();
//        return tecnicos.stream().toList();
        return tecnicoRepository.findAll();
    }

    @Override
    public Optional<Tecnico> buscarPorNombre(String nombres) {
        return tecnicoRepository.findByNombres(nombres);
   }

    @Override
    public Optional<Tecnico> buscarPorId(Long idTecnico) {
        return tecnicoRepository.findById(idTecnico);
    }

    @Override
    @SneakyThrows
    public Tecnico actualizarTecnico(Long idTecnico, Tecnico tecnico) {
        Tecnico tecnicoExistente = tecnicoRepository.findById(idTecnico)
                .orElseThrow(()  ->   new Exception("Tecnico con ID "+idTecnico+" no encontrado"));
        tecnicoExistente.setNombres(tecnico.getNombres());
        tecnicoExistente.setNombres(tecnico.getNombres());
        tecnicoExistente.setApellidosPaterno(tecnico.getApellidosPaterno());
        tecnicoExistente.setApellidosMaterno(tecnico.getApellidosMaterno());
        tecnicoExistente.setNacionalidad(tecnico.getNacionalidad());
        tecnicoExistente.setFechaNac(tecnico.getFechaNac());
        tecnicoExistente.setTipoDocumento(tecnico.getTipoDocumento());
        tecnicoExistente.setNumeroDocumento(tecnico.getNumeroDocumento());
        tecnicoExistente.setProfile_image(tecnico.getProfile_image());

        return tecnicoRepository.save(tecnicoExistente);
    }

    @Override
    @SneakyThrows
    public void eliminarTecnico(Long idTecnico) {
        tecnicoRepository.findById(idTecnico)
                .orElseThrow(()  ->   new Exception("Tecnico con ID "+ idTecnico+" no encontrado"));
        tecnicoRepository.deleteById(idTecnico);
    }
}
