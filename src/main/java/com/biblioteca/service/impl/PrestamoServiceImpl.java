package com.biblioteca.service.impl;

import com.biblioteca.dto.PrestamoRequest;
import com.biblioteca.dto.PrestamoResponse;
import com.biblioteca.model.Ejemplar;
import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.repository.PrestamoRepository;
import com.biblioteca.repository.UsuarioRepository;
import com.biblioteca.service.PrestamoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final EjemplarRepository ejemplarRepository;
    private final UsuarioRepository usuarioRepository;

    public PrestamoServiceImpl(PrestamoRepository prestamoRepository, EjemplarRepository ejemplarRepository, UsuarioRepository usuarioRepository) {
        this.prestamoRepository = prestamoRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public PrestamoResponse crearPrestamo(PrestamoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Ejemplar ejemplar = ejemplarRepository.findById(request.getEjemplarId()).orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));
        if (!"DISPONIBLE".equals(ejemplar.getEstado())) {
            throw new RuntimeException("El ejemplar no está disponible");
        }
        ejemplar.setEstado("PRESTADO");
        ejemplarRepository.save(ejemplar);

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuarioId(usuario.getId());
        prestamo.setEjemplarId(request.getEjemplarId());
        prestamo.setFechaPrestamo(request.getFechaPrestamo());
        prestamo.setFechaDevolucionEsperada(request.getFechaDevolucionEsperada());
        prestamo.setFechaDevolucionReal(null);
        prestamo.setEstado("ACTIVO");

        Prestamo guardado = prestamoRepository.save(prestamo);
        return mapToResponse(guardado);
    }

    @Override
    public List<PrestamoResponse> listarPrestamos() {
        return prestamoRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public PrestamoResponse consultarPrestamo(String id) {
        Prestamo prestamo = prestamoRepository.findById(id).orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        return mapToResponse(prestamo);
    }

    @Override
    public PrestamoResponse registrarDevolucion(String id) {
        Prestamo prestamo = prestamoRepository.findById(id).orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        prestamo.setEstado("DEVUELTO");
        prestamo.setFechaDevolucionReal(LocalDate.now());
        Prestamo actualizado = prestamoRepository.save(prestamo);

        Ejemplar ejemplar = ejemplarRepository.findById(prestamo.getEjemplarId()).orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));
        ejemplar.setEstado("DISPONIBLE");
        ejemplarRepository.save(ejemplar);

        return mapToResponse(actualizado);
    }

    @Override
    public void eliminarPrestamo(String id) {
        Prestamo prestamo = prestamoRepository.findById(id).orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        prestamoRepository.delete(prestamo);
    }

    private PrestamoResponse mapToResponse(Prestamo prestamo) {
        return new PrestamoResponse(
                prestamo.getId(),
                prestamo.getUsuarioId(),
                prestamo.getEjemplarId(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucionEsperada(),
                prestamo.getFechaDevolucionReal(),
                prestamo.getEstado()
        );
    }
}
