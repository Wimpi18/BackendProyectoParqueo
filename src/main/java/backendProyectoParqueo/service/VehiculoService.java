package backendProyectoParqueo.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.model.Vehiculo;
import backendProyectoParqueo.repository.ParqueoRepository;
import backendProyectoParqueo.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    @Autowired
    private final VehiculoRepository vehiculoRepository;

    private final ParqueoRepository parqueoRepository;

    public List<Vehiculo> listarTodos() {
        return vehiculoRepository.findAll();
    }

    public Vehiculo crearVehiculo(Vehiculo vehiculo) {
        if (vehiculoRepository.existsByPlaca(vehiculo.getPlaca())) {
            throw new IllegalArgumentException("La placa ya está registrada.");
        }
        return vehiculoRepository.save(vehiculo);
    }

    public void eliminarVehiculo(Long id) {
        if (!vehiculoRepository.existsById(id)) {
            throw new NoSuchElementException("Vehículo no encontrado");
        }

        if (!parqueoRepository.findByVehiculo_Id(id).isEmpty()) {
            throw new IllegalStateException("No se puede eliminar: el vehículo está siendo usado en parqueos.");
        }

        vehiculoRepository.deleteById(id);
    }

}
