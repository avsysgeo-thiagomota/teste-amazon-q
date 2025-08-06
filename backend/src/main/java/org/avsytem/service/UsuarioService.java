package org.avsytem.service;

import org.avsytem.dto.UsuarioRequest;
import org.avsytem.entity.Usuario;
import org.avsytem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> findAllActive() {
        return usuarioRepository.findAllActive();
    }

    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario save(UsuarioRequest usuarioRequest) {
        if (existsByUsername(usuarioRequest.getUsername())) {
            throw new RuntimeException("Erro: Username já está em uso!");
        }

        if (usuarioRequest.getEmail() != null && existsByEmail(usuarioRequest.getEmail())) {
            throw new RuntimeException("Erro: Email já está em uso!");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioRequest.getUsername());
        usuario.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));
        usuario.setNomeCompleto(usuarioRequest.getNomeCompleto());
        usuario.setEmail(usuarioRequest.getEmail());

        return usuarioRepository.save(usuario);
    }

    public Usuario update(Integer id, UsuarioRequest usuarioRequest) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));

        // Check if username is being changed and if it's already in use
        if (!usuario.getUsername().equals(usuarioRequest.getUsername()) && 
            existsByUsername(usuarioRequest.getUsername())) {
            throw new RuntimeException("Erro: Username já está em uso!");
        }

        // Check if email is being changed and if it's already in use
        if (usuarioRequest.getEmail() != null && 
            !usuarioRequest.getEmail().equals(usuario.getEmail()) && 
            existsByEmail(usuarioRequest.getEmail())) {
            throw new RuntimeException("Erro: Email já está em uso!");
        }

        usuario.setUsername(usuarioRequest.getUsername());
        if (usuarioRequest.getPassword() != null && !usuarioRequest.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));
        }
        usuario.setNomeCompleto(usuarioRequest.getNomeCompleto());
        usuario.setEmail(usuarioRequest.getEmail());

        return usuarioRepository.save(usuario);
    }

    public void deleteById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        
        // Soft delete - just mark as inactive
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public void activateUser(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }
}
