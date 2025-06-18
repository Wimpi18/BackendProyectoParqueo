package backendProyectoParqueo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.model.Vehiculo;
import backendProyectoParqueo.repository.VehiculoEnParqueoRepository;
import backendProyectoParqueo.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    private final VehiculoEnParqueoRepository vehiculoEnParqueoRepository;

    public List<Vehiculo> listarTodos() {
        return vehiculoRepository.findAll();
    }

    public List<Object> obtenerVehiculosActivosPorClienteId(UUID id) {
        List<Object> result = vehiculoRepository.obtenerVehiculosActivosPorClienteId(id);
        if (result.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El cliente no tiene vehículos asociados a su cuenta");

        return result;
    }

    public List<VehiculoDTO> obtenerVehiculosPorClienteId(UUID id) {
        List<VehiculoDTO> result = vehiculoRepository.obtenerVehiculosPorClienteId(id);
        if (result.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El cliente no tiene vehículos asociados a su cuenta");

        return result;
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

        if (!vehiculoEnParqueoRepository.findByVehiculo(new Vehiculo(id)).isEmpty()) {
            throw new IllegalStateException("No se puede eliminar: el vehículo está siendo usado en parqueos.");
        }

        vehiculoRepository.deleteById(id);
    }
}
