package com.gestaopgr.dao;

import com.gestaopgr.model.Contrato;
import com.gestaopgr.model.Fatura;
import com.gestaopgr.model.Pagamento;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ContratoDao extends BaseDao {
    private Contrato map(ResultSet rs) throws SQLException {
        Contrato c = new Contrato();
        c.setId(rs.getLong("id"));
        c.setEmpresaId(rs.getLong("empresa_id"));
        c.setPropostaId(getLongOrNull(rs, "proposta_id"));
        c.setCodigo(rs.getString("codigo"));
        c.setTitulo(rs.getString("titulo"));
        c.setDescricao(rs.getString("descricao"));
        c.setTipo(rs.getString("tipo"));
        c.setStatus(rs.getString("status"));
        String di = rs.getString("data_inicio");
        if (di != null && !di.isEmpty()) c.setDataInicio(LocalDate.parse(di.substring(0, 10)));
        String df = rs.getString("data_fim");
        if (df != null && !df.isEmpty()) c.setDataFim(LocalDate.parse(df.substring(0, 10)));
        c.setValorTotal(rs.getDouble("valor_total"));
        c.setValorRecorrente(rs.getDouble("valor_recorrente"));
        c.setPeriodicidade(rs.getString("periodicidade"));
        c.setDiaVencimento(rs.getObject("dia_vencimento") != null ? rs.getInt("dia_vencimento") : null);
        c.setObservacoes(rs.getString("observacoes"));
        c.setCreatedAt(parseDateTime(rs.getString("created_at")));
        c.setUpdatedAt(parseDateTime(rs.getString("updated_at")));
        try { c.setEmpresaNome(rs.getString("empresa_nome")); } catch (SQLException ignored) {}
        return c;
    }
    public Contrato save(Contrato c) throws SQLException {
        if (c.getId() == null) {
            long id = executeInsert(
                "INSERT INTO contrato (empresa_id, proposta_id, codigo, titulo, descricao, tipo, status, data_inicio, data_fim, valor_total, valor_recorrente, periodicidade, dia_vencimento, observacoes) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                c.getEmpresaId(), c.getPropostaId(), c.getCodigo(), c.getTitulo(), c.getDescricao(), c.getTipo(), c.getStatus(), c.getDataInicio(), c.getDataFim(), c.getValorTotal(), c.getValorRecorrente(), c.getPeriodicidade(), c.getDiaVencimento(), c.getObservacoes()
            );
            c.setId(id);
        } else {
            executeUpdate(
                "UPDATE contrato SET codigo=?, titulo=?, descricao=?, tipo=?, status=?, data_inicio=?, data_fim=?, valor_total=?, valor_recorrente=?, periodicidade=?, dia_vencimento=?, observacoes=?, updated_at=datetime('now') WHERE id=?",
                c.getCodigo(), c.getTitulo(), c.getDescricao(), c.getTipo(), c.getStatus(), c.getDataInicio(), c.getDataFim(), c.getValorTotal(), c.getValorRecorrente(), c.getPeriodicidade(), c.getDiaVencimento(), c.getObservacoes(), c.getId()
            );
        }
        return c;
    }
    public Optional<Contrato> findById(Long id) throws SQLException {
        return queryOne("SELECT c.*, e.razao_social as empresa_nome FROM contrato c LEFT JOIN empresa e ON c.empresa_id=e.id WHERE c.id=?", this::map, id);
    }
    public List<Contrato> findByEmpresa(Long empresaId) throws SQLException {
        return query("SELECT c.*, e.razao_social as empresa_nome FROM contrato c LEFT JOIN empresa e ON c.empresa_id=e.id WHERE c.empresa_id=? ORDER BY c.created_at DESC", this::map, empresaId);
    }
    public List<Contrato> findAll() throws SQLException {
        return query("SELECT c.*, e.razao_social as empresa_nome FROM contrato c LEFT JOIN empresa e ON c.empresa_id=e.id ORDER BY c.created_at DESC", this::map);
    }
    public List<Contrato> findAtivos() throws SQLException {
        return query("SELECT c.*, e.razao_social as empresa_nome FROM contrato c LEFT JOIN empresa e ON c.empresa_id=e.id WHERE c.status='ATIVO' ORDER BY c.created_at DESC", this::map);
    }
    public void delete(Long id) throws SQLException { executeUpdate("DELETE FROM contrato WHERE id=?", id); }
    private Long getLongOrNull(ResultSet rs, String col) throws SQLException { long v = rs.getLong(col); return rs.wasNull() ? null : v; }
    private LocalDateTime parseDateTime(String s) { if (s == null) return null; return LocalDateTime.parse(s.replace(' ', 'T')); }
}

