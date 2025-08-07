package org.avsytem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

@Entity
@Table(name = "ingredientes")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Nome do ingrediente é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    @Column(nullable = false)
    private String nome;

    private Double quantidade;

    @Size(max = 50, message = "Unidade deve ter no máximo 50 caracteres")
    private String unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receita_id")
    @JsonIgnore
    private Receita receita;

    // Constructors
    public Ingrediente() {}

    public Ingrediente(String nome, Double quantidade, String unidade, Receita receita) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.receita = receita;
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

    public Receita getReceita() {
        return receita;
    }

    public void setReceita(Receita receita) {
        this.receita = receita;
    }

    // Convenience method to get receita_id for JSON serialization
    public Integer getReceitaId() {
        return receita != null ? receita.getId() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingrediente that = (Ingrediente) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
