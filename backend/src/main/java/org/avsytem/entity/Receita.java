package org.avsytem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receitas")
public class Receita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da receita é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "tempo_preparo_min")
    private Integer tempoPreparoMin;

    private Integer porcoes;

    @Size(max = 50, message = "Dificuldade deve ter no máximo 50 caracteres")
    private String dificuldade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Ingrediente> ingredientes = new ArrayList<>();

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Passo> passos = new ArrayList<>();

    // Constructors
    public Receita() {}

    public Receita(String nome, String descricao, Integer tempoPreparoMin, Integer porcoes, String dificuldade, Usuario usuario) {
        this.nome = nome;
        this.descricao = descricao;
        this.tempoPreparoMin = tempoPreparoMin;
        this.porcoes = porcoes;
        this.dificuldade = dificuldade;
        this.usuario = usuario;
    }

    // Helper methods
    public void addIngrediente(Ingrediente ingrediente) {
        ingredientes.add(ingrediente);
        ingrediente.setReceita(this);
    }

    public void removeIngrediente(Ingrediente ingrediente) {
        ingredientes.remove(ingrediente);
        ingrediente.setReceita(null);
    }

    public void addPasso(Passo passo) {
        passos.add(passo);
        passo.setReceita(this);
    }

    public void removePasso(Passo passo) {
        passos.remove(passo);
        passo.setReceita(null);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    // Convenience method to get usuario_id for JSON serialization
    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }
}
