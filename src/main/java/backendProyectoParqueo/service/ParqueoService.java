package backendProyectoParqueo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.repository.ParqueoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParqueoService {

    private final ParqueoRepository parqueoRepository;

    public List<Parqueo> listarTodos() {
        return parqueoRepository.findAll();
    }

    public Parqueo crearParqueo(Parqueo parqueo) {
        if (parqueo.getNroEspacio() == null || parqueo.getNroEspacio() <= 0) {
            throw new IllegalArgumentException("Número de espacio inválido.");
        }
        return parqueoRepository.save(parqueo);
    }

    public void eliminarParqueo(Long id) {
        if (!parqueoRepository.existsById(id)) {
            throw new NoSuchElementException("Parqueo no encontrado");
        }
        parqueoRepository.deleteById(id);
    }

    public List<Short> obtenerPuestosLibres() {
        int totalEspacios = 113;

        List<Parqueo> ocupados = parqueoRepository.findByEstado(Parqueo.EstadoParqueo.Activo);
        Set<Short> espaciosOcupados = ocupados.stream()
                .map(Parqueo::getNroEspacio)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Short> disponibles = new ArrayList<>();
        for (short i = 1; i <= totalEspacios; i++) {
            if (!espaciosOcupados.contains(i)) {
                disponibles.add(i);
            }
        }

        return disponibles;
    }

}
