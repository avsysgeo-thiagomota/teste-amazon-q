package org.avsytem.dto;

import org.avsytem.entity.Ingrediente;

public class IngredienteResponse {
    private Integer id;
    private String nome;
    private Double quantidade;
    private String unidade;

    public IngredienteResponse() {}

    public IngredienteResponse(Ingrediente ingrediente) {
        this.id = ingrediente.getId();
        this.nome = ingrediente.getNome();
        this.quantidade = ingrediente.getQuantidade();
        this.unidade = ingrediente.getUnidade();
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

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
}
