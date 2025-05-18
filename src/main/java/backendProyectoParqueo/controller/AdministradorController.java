package backendProyectoParqueo.controller;

import backendProyectoParqueo.model.Administrador;
import backendProyectoParqueo.repository.AdministradorRepository;
import backendProyectoParqueo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administrador")
public class AdministradorController {

    @Autowired
    private AdministradorRepository administradorRepository;

    @GetMapping
    public List<Administrador> getAllAdministradores() {
        return administradorRepository.findAll();
    }

    @PostMapping
    public Administrador createAdministrador(@RequestBody Administrador administrador) {
        return administradorRepository.save(administrador);
    }
}
