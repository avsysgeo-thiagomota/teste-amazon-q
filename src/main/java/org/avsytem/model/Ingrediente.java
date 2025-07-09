package org.avsytem.model;

public class Ingrediente {
    private String nome;
    private double quantidade;
    private String unidade; // Ex: "xícaras", "gramas", "colheres de sopa"

    public Ingrediente(String nome, double quantidade, String unidade) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidade = unidade;
    }

    public Ingrediente() {}


    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    @Override
    public String toString() {
        return quantidade + " " + unidade + " de " + nome;
    }

}
