package org.avsytem.dto;

import org.avsytem.entity.Usuario;

import java.time.OffsetDateTime;

public class UsuarioResponse {
    private Integer id;
    private String username;
    private String nomeCompleto;
    private String email;
    private Boolean ativo;
    private OffsetDateTime dataCriacao;

    public UsuarioResponse() {}

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.nomeCompleto = usuario.getNomeCompleto();
        this.email = usuario.getEmail();
        this.ativo = usuario.getAtivo();
        this.dataCriacao = usuario.getDataCriacao();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public OffsetDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(OffsetDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
