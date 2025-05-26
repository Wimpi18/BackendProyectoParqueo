package backendProyectoParqueo.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.model.Cajero;
import backendProyectoParqueo.repository.CajeroRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CajeroService {
    @Autowired
    private CajeroRepository cajeroRepository;

    public Optional<Cajero> findById(UUID id) {
        return cajeroRepository.findById(id);
    }
}
