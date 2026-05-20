package com.biblioteca.service.impl;

import com.biblioteca.dto.EjemplarRequest;
import com.biblioteca.dto.EjemplarResponse;
import com.biblioteca.model.Ejemplar;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.service.EjemplarService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EjemplarServiceImpl implements EjemplarService {

    private final EjemplarRepository ejemplarRepository;

    public EjemplarServiceImpl(EjemplarRepository ejemplarRepository) {
        this.ejemplarRepository = ejemplarRepository;
    }

    @Override
    public EjemplarResponse crearEjemplar(EjemplarRequest request) {
        Ejemplar ejemplar = mapToEntity(request);
        Ejemplar guardado = ejemplarRepository.save(ejemplar);
        return mapToResponse(guardado);
    }

    @Override
    public List<EjemplarResponse> listarEjemplares() {
        return ejemplarRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public EjemplarResponse consultarEjemplar(String id) {
        Ejemplar ejemplar = ejemplarRepository.findById(id).orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));
        return mapToResponse(ejemplar);
    }

    @Override
    public EjemplarResponse actualizarEjemplar(String id, EjemplarRequest request) {
        Ejemplar ejemplar = ejemplarRepository.findById(id).orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));
        ejemplar.setCodigoEjemplar(request.getCodigoEjemplar());
        ejemplar.setEstado(request.getEstado());
        ejemplar.setUbicacion(request.getUbicacion());
        ejemplar.setLibroId(request.getLibroId());
        Ejemplar actualizado = ejemplarRepository.save(ejemplar);
        return mapToResponse(actualizado);
    }

    @Override
    public void eliminarEjemplar(String id) {
        Ejemplar ejemplar = ejemplarRepository.findById(id).orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));
        ejemplarRepository.delete(ejemplar);
    }

    private Ejemplar mapToEntity(EjemplarRequest request) {
        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setCodigoEjemplar(request.getCodigoEjemplar());
        ejemplar.setEstado(request.getEstado());
        ejemplar.setUbicacion(request.getUbicacion());
        ejemplar.setLibroId(request.getLibroId());
        return ejemplar;
    }

    private EjemplarResponse mapToResponse(Ejemplar ejemplar) {
        return new EjemplarResponse(
                ejemplar.getId(),
                ejemplar.getCodigoEjemplar(),
                ejemplar.getEstado(),
                ejemplar.getUbicacion(),
                ejemplar.getLibroId()
        );
    }
}
