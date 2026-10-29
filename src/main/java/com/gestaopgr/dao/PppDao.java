package com.gestaopgr.dao;

import com.gestaopgr.model.Ppp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PppDao extends BaseDao {
    private Ppp map(ResultSet rs) throws SQLException {
        Ppp p = new Ppp();
        p.setId(rs.getLong("id"));
        p.setEmpresaId(rs.getLong("empresa_id"));
        p.setTrabalhadorNome(rs.getString("trabalhador_nome"));
        p.setTrabalhadorCpf(rs.getString("trabalhador_cpf"));
        p.setTrabalhadorNit(rs.getString("trabalhador_nit"));
        String dn = rs.getString("data_nascimento");
        if (dn != null && !dn.isEmpty()) p.setDataNascimento(LocalDate.parse(dn.substring(0, 10)));
        String da = rs.getString("data_admissao");
        if (da != null && !da.isEmpty()) p.setDataAdmissao(LocalDate.parse(da.substring(0, 10)));
        String dd = rs.getString("data_demissao");
        if (dd != null && !dd.isEmpty()) p.setDataDemissao(LocalDate.parse(dd.substring(0, 10)));
        p.setCargo(rs.getString("cargo"));
        p.setSetor(rs.getString("setor"));
        p.setAgenteNocivo(rs.getString("agente_nocivo"));
        p.setIntensidade(rs.getString("intensidade"));
        p.setTecnicaMedicao(rs.getString("tecnica_medicao"));
        p.setEpc(rs.getString("epc"));
        p.setEpi(rs.getString("epi"));
        p.setResultadoMedicao(rs.getString("resultado_medicao"));
        String dp = rs.getString("data_ppp");
        if (dp != null && !dp.isEmpty()) p.setDataPpp(LocalDate.parse(dp.substring(0, 10)));
        p.setObservacoes(rs.getString("observacoes"));
        p.setPdfPath(rs.getString("pdf_path"));
        p.setCreatedAt(parseDateTime(rs.getString("created_at")));
        p.setUpdatedAt(parseDateTime(rs.getString("updated_at")));
        try { p.setEmpresaNome(rs.getString("empresa_nome")); } catch (SQLException ignored) {}
        return p;
    }
    public Ppp save(Ppp p) throws SQLException {
        if (p.getId() == null) {
            long id = executeInsert(
                "INSERT INTO ppp (empresa_id, trabalhador_nome, trabalhador_cpf, trabalhador_nit, data_nascimento, data_admissao, data_demissao, cargo, setor, agente_nocivo, intensidade, tecnica_medicao, epc, epi, resultado_medicao, data_ppp, observacoes, pdf_path) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                p.getEmpresaId(), p.getTrabalhadorNome(), p.getTrabalhadorCpf(), p.getTrabalhadorNit(), p.getDataNascimento(), p.getDataAdmissao(), p.getDataDemissao(), p.getCargo(), p.getSetor(), p.getAgenteNocivo(), p.getIntensidade(), p.getTecnicaMedicao(), p.getEpc(), p.getEpi(), p.getResultadoMedicao(), p.getDataPpp(), p.getObservacoes(), p.getPdfPath()
            );
            p.setId(id);
        } else {
            executeUpdate(
                "UPDATE ppp SET trabalhador_nome=?, trabalhador_cpf=?, trabalhador_nit=?, data_nascimento=?, data_admissao=?, data_demissao=?, cargo=?, setor=?, agente_nocivo=?, intensidade=?, tecnica_medicao=?, epc=?, epi=?, resultado_medicao=?, data_ppp=?, observacoes=?, pdf_path=?, updated_at=datetime('now') WHERE id=?",
                p.getTrabalhadorNome(), p.getTrabalhadorCpf(), p.getTrabalhadorNit(), p.getDataNascimento(), p.getDataAdmissao(), p.getDataDemissao(), p.getCargo(), p.getSetor(), p.getAgenteNocivo(), p.getIntensidade(), p.getTecnicaMedicao(), p.getEpc(), p.getEpi(), p.getResultadoMedicao(), p.getDataPpp(), p.getObservacoes(), p.getPdfPath(), p.getId()
            );
        }
        return p;
    }
    public Optional<Ppp> findById(Long id) throws SQLException {
        return queryOne("SELECT p.*, e.razao_social as empresa_nome FROM ppp p LEFT JOIN empresa e ON p.empresa_id=e.id WHERE p.id=?", this::map, id);
    }
    public List<Ppp> findByEmpresa(Long empresaId) throws SQLException {
        return query("SELECT p.*, e.razao_social as empresa_nome FROM ppp p LEFT JOIN empresa e ON p.empresa_id=e.id WHERE p.empresa_id=? ORDER BY p.trabalhador_nome", this::map, empresaId);
    }
    public List<Ppp> findAll() throws SQLException {
        return query("SELECT p.*, e.razao_social as empresa_nome FROM ppp p LEFT JOIN empresa e ON p.empresa_id=e.id ORDER BY p.trabalhador_nome", this::map);
    }
    public void delete(Long id) throws SQLException { executeUpdate("DELETE FROM ppp WHERE id=?", id); }
    private LocalDateTime parseDateTime(String s) { if (s == null) return null; return LocalDateTime.parse(s.replace(' ', 'T')); }
}
