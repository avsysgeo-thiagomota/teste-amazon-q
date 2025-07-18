package org.avsytem.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Receita {

    private int id;
    private int usuario_id;
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

    public int getUsuario_id(){
        return usuario_id;
    }
    public void setUsuario_id(int usuario_id){
        this.usuario_id = usuario_id;
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

    /**
     * Compara este objeto Receita com outro.
     * @return true se todos os campos, incluindo o conteúdo das listas, forem iguais.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receita receita = (Receita) o;
        return id == receita.id &&
                tempoDePreparo == receita.tempoDePreparo &&
                porcoes == receita.porcoes &&
                Objects.equals(nome, receita.nome) &&
                Objects.equals(descricao, receita.descricao) &&
                Objects.equals(dificuldade, receita.dificuldade) &&
                Objects.equals(ingredientes, receita.ingredientes) && // A lista de ingredientes deve ser igual
                Objects.equals(passos, receita.passos);               // A lista de passos deve ser igual
    }

    /**
     * Gera um código hash para o objeto Receita.
     * @return um valor int que representa o objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, nome, descricao, tempoDePreparo, porcoes, dificuldade, ingredientes, passos);
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
