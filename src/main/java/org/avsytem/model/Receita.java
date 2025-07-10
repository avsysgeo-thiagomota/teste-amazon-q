package org.avsytem.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Receita {

    private int id;
    private String nome;
    private String descricao;
    private int tempoDePreparo; // em minutos
    private int porcoes; // rende X porções
    private String dificuldade; // "Fácil", "Médio", "Difícil"
    private List<Ingrediente> ingredientes;
    private List<Passo> passos;

    public Receita() {
        this.ingredientes = new ArrayList<>();
        this.passos = new ArrayList<>();
    }

    // Getters e Setters para todos os campos...
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getTempoDePreparo() {
        return tempoDePreparo;
    }

    public void setTempoDePreparo(int tempoDePreparo) {
        this.tempoDePreparo = tempoDePreparo;
    }

    public int getPorcoes() {
        return porcoes;
    }

    public void setPorcoes(int porcoes) {
        this.porcoes = porcoes;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<Passo> getPassos() {
        return passos;
    }

    public void setPassos(List<Passo> passos) {
        this.passos = passos;
    }

    @Override
    public String toString() {
        return "Receita{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", tempoDePreparo=" + tempoDePreparo +
                ", porcoes=" + porcoes +
                ", dificuldade='" + dificuldade + '\'' +
                ", ingredientes=" + ingredientes.stream().map(Ingrediente::toString).collect(Collectors.joining(", ")) +
                ", passos=" + passos.stream().map(Passo::toString).collect(Collectors.joining(", ")) +
                '}';
    }
}
