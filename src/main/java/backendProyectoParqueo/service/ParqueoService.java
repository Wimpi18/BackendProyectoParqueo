package backendProyectoParqueo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import backendProyectoParqueo.exception.BusinessException;
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

    public List<Short> obtenerEspaciosDisponibles() {
        List<Short> ocupados = parqueoRepository.findEspaciosOcupados();

        return IntStream.rangeClosed(1, 113)
                .mapToObj(i -> (short) i)
                .filter(i -> !ocupados.contains(i))
                .collect(Collectors.toList());
    }

    public Parqueo findById(Long id) {
        return parqueoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Parqueo no encontrado.", "idParqueo"));
    }
}
