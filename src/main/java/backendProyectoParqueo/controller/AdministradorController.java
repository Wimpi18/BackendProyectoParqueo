package backendProyectoParqueo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.model.Administrador;
import backendProyectoParqueo.repository.AdministradorRepository;

@RestController
@RequestMapping("administrador")
public class AdministradorController {

    private final AdministradorRepository administradorRepository;

    @Autowired
    public AdministradorController(AdministradorRepository administradorRepository){
        this.administradorRepository = administradorRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<List<Administrador>> getAllAdministradores() {
        List<Administrador> administradores = administradorRepository.findAll();
        return ResponseEntity.ok(administradores);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')") 
    public ResponseEntity<Administrador> createAdministrador(@RequestBody Administrador administrador) {
        if (administrador.getUsuario() == null || administrador.getUsuario().getId() != null) {
            return ResponseEntity.badRequest().body(null); 
        }
        
        try {
            Administrador administradorGuardado = administradorRepository.save(administrador);
            return ResponseEntity.status(HttpStatus.CREATED).body(administradorGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // O mensaje de error
        }
    }

    
}
