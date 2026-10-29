package com.gestaopgr.dao;

import com.gestaopgr.model.PlanoAcao;
import com.gestaopgr.model.Pgr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PlanoAcaoDao extends BaseDao {
    private PlanoAcao map(ResultSet rs) throws SQLException {
        PlanoAcao p = new PlanoAcao();
        p.setId(rs.getLong("id"));
        p.setRiscoId(rs.getLong("risco_id"));
        p.setEmpresaId(rs.getLong("empresa_id"));
        p.setAcao(rs.getString("acao"));
        p.setTipo(rs.getString("tipo"));
        p.setResponsavel(rs.getString("responsavel"));
        String prazo = rs.getString("prazo");
        if (prazo != null && !prazo.isEmpty()) p.setPrazo(LocalDate.parse(prazo.substring(0, 10)));
        p.setCustoEstimado(rs.getObject("custo_estimado") != null ? rs.getDouble("custo_estimado") : null);
        p.setStatus(rs.getString("status"));
        String dc = rs.getString("data_conclusao");
        if (dc != null && !dc.isEmpty()) p.setDataConclusao(LocalDate.parse(dc.substring(0, 10)));
        p.setObservacao(rs.getString("observacao"));
        p.setCreatedAt(parseDateTime(rs.getString("created_at")));
        p.setUpdatedAt(parseDateTime(rs.getString("updated_at")));
        try { p.setRiscoDescricao(rs.getString("risco_descricao")); } catch (SQLException ignored) {}
        try { p.setEmpresaNome(rs.getString("empresa_nome")); } catch (SQLException ignored) {}
        return p;
    }
    public PlanoAcao save(PlanoAcao p) throws SQLException {
        if (p.getId() == null) {
            long id = executeInsert(
                "INSERT INTO plano_acao (risco_id, empresa_id, acao, tipo, responsavel, prazo, custo_estimado, status, data_conclusao, observacao) VALUES (?,?,?,?,?,?,?,?,?,?)",
                p.getRiscoId(), p.getEmpresaId(), p.getAcao(), p.getTipo(), p.getResponsavel(), p.getPrazo(),
                p.getCustoEstimado(), p.getStatus(), p.getDataConclusao(), p.getObservacao()
            );
            p.setId(id);
        } else {
            executeUpdate(
                "UPDATE plano_acao SET acao=?, tipo=?, responsavel=?, prazo=?, custo_estimado=?, status=?, data_conclusao=?, observacao=?, updated_at=datetime('now') WHERE id=?",
                p.getAcao(), p.getTipo(), p.getResponsavel(), p.getPrazo(), p.getCustoEstimado(),
                p.getStatus(), p.getDataConclusao(), p.getObservacao(), p.getId()
            );
        }
        return p;
    }
    public List<PlanoAcao> findByEmpresa(Long empresaId) throws SQLException {
        String sql = "SELECT pa.*, r.perigo_descricao as risco_descricao, e.razao_social as empresa_nome " +
                     "FROM plano_acao pa LEFT JOIN risco r ON pa.risco_id=r.id LEFT JOIN empresa e ON pa.empresa_id=e.id " +
                     "WHERE pa.empresa_id=? ORDER BY pa.prazo";
        return query(sql, this::map, empresaId);
    }
    public List<PlanoAcao> findByRisco(Long riscoId) throws SQLException {
        String sql = "SELECT pa.*, r.perigo_descricao as risco_descricao, e.razao_social as empresa_nome " +
                     "FROM plano_acao pa LEFT JOIN risco r ON pa.risco_id=r.id LEFT JOIN empresa e ON pa.empresa_id=e.id " +
                     "WHERE pa.risco_id=? ORDER BY pa.prazo";
        return query(sql, this::map, riscoId);
    }
    public List<PlanoAcao> findAtrasados() throws SQLException {
        String sql = "SELECT pa.*, r.perigo_descricao as risco_descricao, e.razao_social as empresa_nome " +
                     "FROM plano_acao pa LEFT JOIN risco r ON pa.risco_id=r.id LEFT JOIN empresa e ON pa.empresa_id=e.id " +
                     "WHERE pa.status IN ('PENDENTE','EM_ANDAMENTO') AND pa.prazo < date('now') ORDER BY pa.prazo";
        return query(sql, this::map);
    }
    public void delete(Long id) throws SQLException { executeUpdate("DELETE FROM plano_acao WHERE id=?", id); }
    public List<PlanoAcao> findAll() throws SQLException {
        String sql = "SELECT pa.*, r.perigo_descricao as risco_descricao, e.razao_social as empresa_nome " +
                     "FROM plano_acao pa LEFT JOIN risco r ON pa.risco_id=r.id LEFT JOIN empresa e ON pa.empresa_id=e.id " +
                     "ORDER BY pa.prazo";
        return query(sql, this::map);
    }
    private LocalDateTime parseDateTime(String s) { if (s == null) return null; return LocalDateTime.parse(s.replace(' ', 'T')); }
}
