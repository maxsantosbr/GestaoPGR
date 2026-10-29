package com.gestaopgr.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Pgr {
    public static final String[] STATUS = {"EM_ELABORACAO", "REVISAO", "APROVADO", "VENCIDO"};

    private Long id;
    private Long empresaId;
    private String codigo;
    private String titulo;
    private int versao;
    private LocalDate dataElaboracao;
    private LocalDate dataValidade;
    private String elaborador;
    private String elaboradorRegistro;
    private String status;
    private String observacoes;
    private String pdfPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String empresaNome;

    public Pgr() {
        this.versao = 1;
        this.status = "EM_ELABORACAO";
        this.dataElaboracao = LocalDate.now();
        this.dataValidade = LocalDate.now().plusYears(1);
    }

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long v) { this.empresaId = v; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String v) { this.codigo = v; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String v) { this.titulo = v; }
    public int getVersao() { return versao; }
    public void setVersao(int v) { this.versao = v; }
    public LocalDate getDataElaboracao() { return dataElaboracao; }
    public void setDataElaboracao(LocalDate v) { this.dataElaboracao = v; }
    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate v) { this.dataValidade = v; }
    public String getElaborador() { return elaborador; }
    public void setElaborador(String v) { this.elaborador = v; }
    public String getElaboradorRegistro() { return elaboradorRegistro; }
    public void setElaboradorRegistro(String v) { this.elaboradorRegistro = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String v) { this.observacoes = v; }
    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String v) { this.pdfPath = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
    public String getEmpresaNome() { return empresaNome; }
    public void setEmpresaNome(String v) { this.empresaNome = v; }
}
