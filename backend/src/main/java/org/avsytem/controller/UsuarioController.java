package org.avsytem.controller;

import jakarta.validation.Valid;
import org.avsytem.dto.UsuarioRequest;
import org.avsytem.entity.Usuario;
import org.avsytem.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.findAllActive();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Usuário não encontrado");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        Optional<Usuario> usuario = usuarioService.findByUsername(username);
        
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Usuário não encontrado");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Integer id, 
                                         @Valid @RequestBody UsuarioRequest usuarioRequest,
                                         Authentication authentication) {
        try {
            // Users can only update their own profile
            Usuario currentUser = (Usuario) authentication.getPrincipal();
            if (!currentUser.getId().equals(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Você só pode editar seu próprio perfil");
                return ResponseEntity.badRequest().body(error);
            }

            Usuario usuario = usuarioService.update(id, usuarioRequest);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuário atualizado com sucesso!");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Integer id, Authentication authentication) {
        try {
            // Users can only delete their own profile
            Usuario currentUser = (Usuario) authentication.getPrincipal();
            if (!currentUser.getId().equals(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Você só pode deletar seu próprio perfil");
                return ResponseEntity.badRequest().body(error);
            }

            usuarioService.deleteById(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuário desativado com sucesso!");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateUsuario(@PathVariable Integer id) {
        try {
            usuarioService.activateUser(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuário ativado com sucesso!");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
