package backendProyectoParqueo.service;

import java.util.List;
import java.util.NoSuchElementException;

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
}
