package backendProyectoParqueo.service;

import java.util.List;
import java.util.NoSuchElementException;
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

    public Short obtenerPrimerPuestoLibre() {
        int maxPuestos = 100;
        List<Parqueo> activos = parqueoRepository.findByEstado(Parqueo.EstadoParqueo.Activo);
        Set<Short> ocupados = activos.stream()
                .map(Parqueo::getNroEspacio)
                .collect(Collectors.toSet());

        for (short i = 1; i <= maxPuestos; i++) {
            if (!ocupados.contains(i))
                return i;
        }
        return null;
    }

}
