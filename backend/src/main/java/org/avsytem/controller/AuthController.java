package org.avsytem.controller;

import jakarta.validation.Valid;
import org.avsytem.dto.JwtResponse;
import org.avsytem.dto.LoginRequest;
import org.avsytem.dto.UsuarioRequest;
import org.avsytem.entity.Usuario;
import org.avsytem.security.JwtUtils;
import org.avsytem.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            Usuario usuario = (Usuario) authentication.getPrincipal();

            return ResponseEntity.ok(new JwtResponse(jwt,
                    usuario.getId(),
                    usuario.getUsername(),
                    usuario.getNomeCompleto(),
                    usuario.getEmail()));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Credenciais inválidas");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UsuarioRequest signUpRequest) {
        try {
            Usuario usuario = usuarioService.save(signUpRequest);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuário registrado com sucesso!");
            response.put("username", usuario.getUsername());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            
            if (jwtUtils.validateJwtToken(jwt)) {
                String newToken = jwtUtils.generateTokenFromUsername(username);
                
                Map<String, String> response = new HashMap<>();
                response.put("token", newToken);
                response.put("type", "Bearer");
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Token inválido");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erro ao renovar token");
            return ResponseEntity.badRequest().body(error);
        }
    }
}
