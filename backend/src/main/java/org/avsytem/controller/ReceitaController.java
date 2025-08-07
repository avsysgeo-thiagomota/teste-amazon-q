package org.avsytem.controller;

import jakarta.validation.Valid;
import org.avsytem.dto.ReceitaRequest;
import org.avsytem.dto.ReceitaResponse;
import org.avsytem.dto.ReceitaSummaryResponse;
import org.avsytem.entity.Receita;
import org.avsytem.entity.Usuario;
import org.avsytem.service.ReceitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/receitas")
public class ReceitaController {

    @Autowired
    private ReceitaService receitaService;

    @GetMapping
    public ResponseEntity<List<ReceitaSummaryResponse>> getAllReceitas(Authentication authentication,
                                                       @RequestParam(required = false) String nome,
                                                       @RequestParam(required = false) String dificuldade,
                                                       @RequestParam(defaultValue = "false") boolean withDetails) {
        Usuario currentUser = (Usuario) authentication.getPrincipal();
        Integer usuarioId = currentUser.getId();

        List<Receita> receitas;

        if (nome != null && !nome.trim().isEmpty()) {
            receitas = receitaService.findByUsuarioIdAndNomeContaining(usuarioId, nome);
        } else if (dificuldade != null && !dificuldade.trim().isEmpty()) {
            receitas = receitaService.findByUsuarioIdAndDificuldade(usuarioId, dificuldade);
        } else {
            receitas = receitaService.findByUsuarioId(usuarioId);
        }

        List<ReceitaSummaryResponse> receitaResponses = receitas.stream()
                .map(ReceitaSummaryResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(receitaResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReceitaById(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = (Usuario) authentication.getPrincipal();
        Optional<Receita> receita = receitaService.findByIdWithDetails(id);

        if (receita.isPresent()) {
            // Check if the user owns this recipe
            if (!receita.get().getUsuario().getId().equals(currentUser.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Você não tem permissão para visualizar esta receita");
                return ResponseEntity.badRequest().body(error);
            }
            ReceitaResponse receitaResponse = new ReceitaResponse(receita.get());
            return ResponseEntity.ok(receitaResponse);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Receita não encontrada");
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createReceita(@Valid @RequestBody ReceitaRequest receitaRequest,
                                         Authentication authentication) {
        try {
            Usuario currentUser = (Usuario) authentication.getPrincipal();
            Receita receita = receitaService.save(receitaRequest, currentUser.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Receita criada com sucesso!");
            response.put("receita", new ReceitaResponse(receita));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReceita(@PathVariable Integer id,
                                         @Valid @RequestBody ReceitaRequest receitaRequest,
                                         Authentication authentication) {
        try {
            Usuario currentUser = (Usuario) authentication.getPrincipal();
            Receita receita = receitaService.update(id, receitaRequest, currentUser.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Receita atualizada com sucesso!");
            response.put("receita", new ReceitaResponse(receita));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReceita(@PathVariable Integer id, Authentication authentication) {
        try {
            Usuario currentUser = (Usuario) authentication.getPrincipal();
            receitaService.deleteById(id, currentUser.getId());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Receita deletada com sucesso!");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getReceitaCount(Authentication authentication) {
        Usuario currentUser = (Usuario) authentication.getPrincipal();
        Long count = receitaService.countByUsuarioId(currentUser.getId());

        Map<String, Long> response = new HashMap<>();
        response.put("count", count);

        return ResponseEntity.ok(response);
    }
}
