package backendProyectoParqueo.service;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.dto.ClienteDTO;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.repository.ClienteRepository;
import backendProyectoParqueo.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public void registrarCliente(ClienteDTO dto) throws IOException {
        if (usuarioRepository.existsByCi(dto.getCi()) || usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("CI o correo ya están registrados.");
        }

        Usuario usuario = new Usuario();
        usuario.setCi(dto.getCi());
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setUsername(dto.getCorreo());
        usuario.setNroCelular(dto.getTelefono());
        usuario.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        usuario = usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setEntidad(dto.getEntidad());
        cliente.setTipo(dto.getTipoCliente());
        byte[] fotoBytes = Base64.getDecoder().decode(dto.getFotoUsuarioBase64());
        cliente.setFoto(fotoBytes);
        cliente.setFoto(fotoBytes);
        clienteRepository.save(cliente);
    }
}
