package org.avsytem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ReceitaRequest {

    @NotBlank(message = "Nome da receita é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String nome;

    private String descricao;

    private Integer tempoPreparoMin;

    private Integer porcoes;

    @Size(max = 50, message = "Dificuldade deve ter no máximo 50 caracteres")
    private String dificuldade;

    private List<IngredienteRequest> ingredientes;

    private List<PassoRequest> passos;

    // Constructors
    public ReceitaRequest() {}

    // Getters and Setters
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

    public List<IngredienteRequest> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<IngredienteRequest> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<PassoRequest> getPassos() {
        return passos;
    }

    public void setPassos(List<PassoRequest> passos) {
        this.passos = passos;
    }

    // Inner classes for nested objects
    public static class IngredienteRequest {
        @NotBlank(message = "Nome do ingrediente é obrigatório")
        private String nome;
        private Double quantidade;
        private String unidade;

        // Constructors
        public IngredienteRequest() {}

        // Getters and Setters
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

    public static class PassoRequest {
        private Integer ordem;
        @NotBlank(message = "Descrição do passo é obrigatória")
        private String descricao;

        // Constructors
        public PassoRequest() {}

        // Getters and Setters
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
    }
}
