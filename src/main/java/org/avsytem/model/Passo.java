package org.avsytem.model;

import java.util.Objects;

public class Passo {
    private int ordem;
    private String descricao;

    public Passo(int ordem, String descricao) {
        this.ordem = ordem;
        this.descricao = descricao;
    }

    public Passo() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passo passo = (Passo) o;
        return ordem == passo.ordem &&
                Objects.equals(descricao, passo.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordem, descricao);
    }

}
