package backendProyectoParqueo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.model.Cajero;
import backendProyectoParqueo.repository.CajeroRepository;

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
