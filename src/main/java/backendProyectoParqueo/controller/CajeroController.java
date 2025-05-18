package backendProyectoParqueo.controller;

import backendProyectoParqueo.model.Cajero;
import backendProyectoParqueo.repository.CajeroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cajero")
public class CajeroController {

    @Autowired
    private CajeroRepository cajeroRepository;

    @GetMapping("/getCajero")
    public List<Cajero> getAllCajero() {
        return cajeroRepository.findAll();
    }

    @PostMapping
    public Cajero createCajero(@RequestBody Cajero cajero) {
        return cajeroRepository.save(cajero);
    }


}
