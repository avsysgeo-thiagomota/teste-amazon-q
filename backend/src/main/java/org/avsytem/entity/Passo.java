package org.avsytem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

@Entity
@Table(name = "passos")
public class Passo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Ordem do passo é obrigatória")
    @Column(nullable = false)
    private Integer ordem;

    @NotBlank(message = "Descrição do passo é obrigatória")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receita_id")
    @JsonIgnore
    private Receita receita;

    // Constructors
    public Passo() {}

    public Passo(Integer ordem, String descricao, Receita receita) {
        this.ordem = ordem;
        this.descricao = descricao;
        this.receita = receita;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
        Passo passo = (Passo) o;
        return Objects.equals(id, passo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
