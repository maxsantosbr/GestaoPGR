package com.gestaopgr.model;

import java.time.LocalDateTime;

public class Usuario {
    public static enum Perfil { SUPER_ADMIN, ADMIN, TECNICO, CONSULTA }

    private Long id;
    private String login;
    private String senhaHash;
    private String nome;
    private String email;
    private Perfil perfil;
    private boolean ativo;
    private LocalDateTime createdAt;

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public String getLogin() { return login; }
    public void setLogin(String v) { this.login = v; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String v) { this.senhaHash = v; }
    public String getNome() { return nome; }
    public void setNome(String v) { this.nome = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil v) { this.perfil = v; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean v) { this.ativo = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}
