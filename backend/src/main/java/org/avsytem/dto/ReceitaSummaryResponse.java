package org.avsytem.dto;

import org.avsytem.entity.Receita;

public class ReceitaSummaryResponse {
    private Integer id;
    private String nome;
    private String descricao;
    private Integer tempoPreparoMin;
    private Integer porcoes;
    private String dificuldade;
    private String usuarioNome;
    private Integer totalIngredientes;
    private Integer totalPassos;

    public ReceitaSummaryResponse() {}

    public ReceitaSummaryResponse(Receita receita) {
        this.id = receita.getId();
        this.nome = receita.getNome();
        this.descricao = receita.getDescricao();
        this.tempoPreparoMin = receita.getTempoPreparoMin();
        this.porcoes = receita.getPorcoes();
        this.dificuldade = receita.getDificuldade();
        
        if (receita.getUsuario() != null) {
            this.usuarioNome = receita.getUsuario().getNomeCompleto() != null 
                ? receita.getUsuario().getNomeCompleto() 
                : receita.getUsuario().getUsername();
        }
        
        this.totalIngredientes = receita.getIngredientes() != null ? receita.getIngredientes().size() : 0;
        this.totalPassos = receita.getPassos() != null ? receita.getPassos().size() : 0;
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

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public Integer getTotalIngredientes() {
        return totalIngredientes;
    }

    public void setTotalIngredientes(Integer totalIngredientes) {
        this.totalIngredientes = totalIngredientes;
    }

    public Integer getTotalPassos() {
        return totalPassos;
    }

    public void setTotalPassos(Integer totalPassos) {
        this.totalPassos = totalPassos;
    }
}
