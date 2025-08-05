package org.avsytem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "passos")
public class Passo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
    public Long getReceitaId() {
        return receita != null ? receita.getId() : null;
    }
}
