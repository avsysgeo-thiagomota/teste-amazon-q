package org.avsytem.service;

import org.avsytem.dto.ReceitaRequest;
import org.avsytem.entity.Ingrediente;
import org.avsytem.entity.Passo;
import org.avsytem.entity.Receita;
import org.avsytem.entity.Usuario;
import org.avsytem.repository.ReceitaRepository;
import org.avsytem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReceitaService {

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Receita> findAll() {
        return receitaRepository.findAll();
    }

    public List<Receita> findByUsuarioId(Long usuarioId) {
        return receitaRepository.findByUsuarioId(usuarioId);
    }

    public List<Receita> findByUsuarioIdWithDetails(Long usuarioId) {
        return receitaRepository.findByUsuarioIdWithDetails(usuarioId);
    }

    public Optional<Receita> findById(Long id) {
        return receitaRepository.findById(id);
    }

    public Optional<Receita> findByIdWithDetails(Long id) {
        return receitaRepository.findByIdWithDetails(id);
    }

    public List<Receita> findByUsuarioIdAndNomeContaining(Long usuarioId, String nome) {
        return receitaRepository.findByUsuarioIdAndNomeContaining(usuarioId, nome);
    }

    public List<Receita> findByUsuarioIdAndDificuldade(Long usuarioId, String dificuldade) {
        return receitaRepository.findByUsuarioIdAndDificuldade(usuarioId, dificuldade);
    }

    public Long countByUsuarioId(Long usuarioId) {
        return receitaRepository.countByUsuarioId(usuarioId);
    }

    public Receita save(ReceitaRequest receitaRequest, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + usuarioId));

        Receita receita = new Receita();
        receita.setNome(receitaRequest.getNome());
        receita.setDescricao(receitaRequest.getDescricao());
        receita.setTempoPreparoMin(receitaRequest.getTempoPreparoMin());
        receita.setPorcoes(receitaRequest.getPorcoes());
        receita.setDificuldade(receitaRequest.getDificuldade());
        receita.setUsuario(usuario);

        // Save the receita first to get the ID
        receita = receitaRepository.save(receita);

        // Add ingredients
        if (receitaRequest.getIngredientes() != null) {
            for (ReceitaRequest.IngredienteRequest ingredienteReq : receitaRequest.getIngredientes()) {
                Ingrediente ingrediente = new Ingrediente();
                ingrediente.setNome(ingredienteReq.getNome());
                ingrediente.setQuantidade(ingredienteReq.getQuantidade());
                ingrediente.setUnidade(ingredienteReq.getUnidade());
                receita.addIngrediente(ingrediente);
            }
        }

        // Add steps
        if (receitaRequest.getPassos() != null) {
            for (int i = 0; i < receitaRequest.getPassos().size(); i++) {
                ReceitaRequest.PassoRequest passoReq = receitaRequest.getPassos().get(i);
                Passo passo = new Passo();
                passo.setOrdem(passoReq.getOrdem() != null ? passoReq.getOrdem() : i + 1);
                passo.setDescricao(passoReq.getDescricao());
                receita.addPasso(passo);
            }
        }

        return receitaRepository.save(receita);
    }

    public Receita update(Long id, ReceitaRequest receitaRequest, Long usuarioId) {
        Receita receita = receitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada com id: " + id));

        // Check if the user owns this recipe
        if (!receita.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Usuário não tem permissão para editar esta receita");
        }

        receita.setNome(receitaRequest.getNome());
        receita.setDescricao(receitaRequest.getDescricao());
        receita.setTempoPreparoMin(receitaRequest.getTempoPreparoMin());
        receita.setPorcoes(receitaRequest.getPorcoes());
        receita.setDificuldade(receitaRequest.getDificuldade());

        // Clear existing ingredients and steps
        receita.getIngredientes().clear();
        receita.getPassos().clear();

        // Add new ingredients
        if (receitaRequest.getIngredientes() != null) {
            for (ReceitaRequest.IngredienteRequest ingredienteReq : receitaRequest.getIngredientes()) {
                Ingrediente ingrediente = new Ingrediente();
                ingrediente.setNome(ingredienteReq.getNome());
                ingrediente.setQuantidade(ingredienteReq.getQuantidade());
                ingrediente.setUnidade(ingredienteReq.getUnidade());
                receita.addIngrediente(ingrediente);
            }
        }

        // Add new steps
        if (receitaRequest.getPassos() != null) {
            for (int i = 0; i < receitaRequest.getPassos().size(); i++) {
                ReceitaRequest.PassoRequest passoReq = receitaRequest.getPassos().get(i);
                Passo passo = new Passo();
                passo.setOrdem(passoReq.getOrdem() != null ? passoReq.getOrdem() : i + 1);
                passo.setDescricao(passoReq.getDescricao());
                receita.addPasso(passo);
            }
        }

        return receitaRepository.save(receita);
    }

    public void deleteById(Long id, Long usuarioId) {
        Receita receita = receitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada com id: " + id));

        // Check if the user owns this recipe
        if (!receita.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Usuário não tem permissão para deletar esta receita");
        }

        receitaRepository.deleteById(id);
    }
}
