package org.avsytem.model;

public class Passo {
    private int ordem;
    private String descricao;

    public Passo(int ordem, String descricao) {
        this.ordem = ordem;
        this.descricao = descricao;
    }

    public Passo() {}

    // Getters e Setters
    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "Passo " + ordem + ": " + descricao;
    }
}
