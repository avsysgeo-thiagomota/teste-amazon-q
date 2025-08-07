package org.avsytem.dto;

import org.avsytem.entity.Receita;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReceitaResponse {
    private Integer id;
    private String nome;
    private String descricao;
    private Integer tempoPreparoMin;
    private Integer porcoes;
    private String dificuldade;
    private UsuarioResponse usuario;
    private Set<IngredienteResponse> ingredientes;
    private List<PassoResponse> passos;

    public ReceitaResponse() {}

    public ReceitaResponse(Receita receita) {
        this.id = receita.getId();
        this.nome = receita.getNome();
        this.descricao = receita.getDescricao();
        this.tempoPreparoMin = receita.getTempoPreparoMin();
        this.porcoes = receita.getPorcoes();
        this.dificuldade = receita.getDificuldade();
        
        // Convert nested entities to DTOs
        if (receita.getUsuario() != null) {
            this.usuario = new UsuarioResponse(receita.getUsuario());
        }
        
        if (receita.getIngredientes() != null) {
            this.ingredientes = receita.getIngredientes().stream()
                    .map(IngredienteResponse::new)
                    .collect(Collectors.toSet());
        }
        
        if (receita.getPassos() != null) {
            this.passos = receita.getPassos().stream()
                    .map(PassoResponse::new)
                    .collect(Collectors.toList());
        }
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getTempoPreparoMin() {
        return tempoPreparoMin;
    }

    public void setTempoPreparoMin(Integer tempoPreparoMin) {
        this.tempoPreparoMin = tempoPreparoMin;
    }

    public Integer getPorcoes() {
        return porcoes;
    }

    public void setPorcoes(Integer porcoes) {
        this.porcoes = porcoes;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }

    public UsuarioResponse getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponse usuario) {
        this.usuario = usuario;
    }

    public Set<IngredienteResponse> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(Set<IngredienteResponse> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<PassoResponse> getPassos() {
        return passos;
    }

    public void setPassos(List<PassoResponse> passos) {
        this.passos = passos;
    }
}
