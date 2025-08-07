package org.avsytem.dto;

import org.avsytem.entity.Passo;

public class PassoResponse {
    private Integer id;
    private Integer ordem;
    private String descricao;

    public PassoResponse() {}

    public PassoResponse(Passo passo) {
        this.id = passo.getId();
        this.ordem = passo.getOrdem();
        this.descricao = passo.getDescricao();
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
}
