package com.gestaopgr.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Ppp {
    private Long id;
    private Long empresaId;
    private String trabalhadorNome;
    private String trabalhadorCpf;
    private String trabalhadorNit;
    private LocalDate dataNascimento;
    private LocalDate dataAdmissao;
    private LocalDate dataDemissao;
    private String cargo;
    private String setor;
    private String agenteNocivo;
    private String intensidade;
    private String tecnicaMedicao;
    private String epc;
    private String epi;
    private String resultadoMedicao;
    private LocalDate dataPpp;
    private String observacoes;
    private String pdfPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String empresaNome;

    public Ppp() { this.dataPpp = LocalDate.now(); }

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long v) { this.empresaId = v; }
    public String getTrabalhadorNome() { return trabalhadorNome; }
    public void setTrabalhadorNome(String v) { this.trabalhadorNome = v; }
    public String getTrabalhadorCpf() { return trabalhadorCpf; }
    public void setTrabalhadorCpf(String v) { this.trabalhadorCpf = v; }
    public String getTrabalhadorNit() { return trabalhadorNit; }
    public void setTrabalhadorNit(String v) { this.trabalhadorNit = v; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate v) { this.dataNascimento = v; }
    public LocalDate getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(LocalDate v) { this.dataAdmissao = v; }
    public LocalDate getDataDemissao() { return dataDemissao; }
    public void setDataDemissao(LocalDate v) { this.dataDemissao = v; }
    public String getCargo() { return cargo; }
    public void setCargo(String v) { this.cargo = v; }
    public String getSetor() { return setor; }
    public void setSetor(String v) { this.setor = v; }
    public String getAgenteNocivo() { return agenteNocivo; }
    public void setAgenteNocivo(String v) { this.agenteNocivo = v; }
    public String getIntensidade() { return intensidade; }
    public void setIntensidade(String v) { this.intensidade = v; }
    public String getTecnicaMedicao() { return tecnicaMedicao; }
    public void setTecnicaMedicao(String v) { this.tecnicaMedicao = v; }
    public String getEpc() { return epc; }
    public void setEpc(String v) { this.epc = v; }
    public String getEpi() { return epi; }
    public void setEpi(String v) { this.epi = v; }
    public String getResultadoMedicao() { return resultadoMedicao; }
    public void setResultadoMedicao(String v) { this.resultadoMedicao = v; }
    public LocalDate getDataPpp() { return dataPpp; }
    public void setDataPpp(LocalDate v) { this.dataPpp = v; }
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
