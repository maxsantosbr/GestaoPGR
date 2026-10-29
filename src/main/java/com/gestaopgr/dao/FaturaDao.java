package com.gestaopgr.dao;

import com.gestaopgr.model.Fatura;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public class FaturaDao extends BaseDao {
    private Fatura map(ResultSet rs) throws SQLException {
        Fatura f = new Fatura();
        f.setId(rs.getLong("id"));
        f.setEmpresaId(rs.getLong("empresa_id"));
        f.setContratoId(getLongOrNull(rs, "contrato_id"));
        f.setPropostaId(getLongOrNull(rs, "proposta_id"));
        f.setCodigo(rs.getString("codigo"));
        f.setDescricao(rs.getString("descricao"));
        String de = rs.getString("data_emissao");
        if (de != null && !de.isEmpty()) f.setDataEmissao(LocalDateTime.parse(de.replace(' ', 'T')));
        String dv = rs.getString("data_vencimento");
        if (dv != null && !dv.isEmpty()) f.setDataVencimento(LocalDate.parse(dv.substring(0, 10)));
        f.setValor(rs.getDouble("valor"));
        f.setDesconto(rs.getDouble("desconto"));
        f.setValorFinal(rs.getDouble("valor_final"));
        f.setStatus(rs.getString("status"));
        f.setObservacoes(rs.getString("observacoes"));
        f.setCreatedAt(parseDateTime(rs.getString("created_at")));
        f.setUpdatedAt(parseDateTime(rs.getString("updated_at")));
        try { f.setEmpresaNome(rs.getString("empresa_nome")); } catch (SQLException ignored) {}
        return f;
    }
    public Fatura save(Fatura f) throws SQLException {
        if (f.getId() == null) {
            long id = executeInsert(
                "INSERT INTO fatura (empresa_id, contrato_id, proposta_id, codigo, descricao, data_emissao, data_vencimento, valor, desconto, valor_final, status, observacoes) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
                f.getEmpresaId(), f.getContratoId(), f.getPropostaId(), f.getCodigo(), f.getDescricao(), f.getDataEmissao(), f.getDataVencimento(), f.getValor(), f.getDesconto(), f.getValorFinal(), f.getStatus(), f.getObservacoes()
            );
            f.setId(id);
        } else {
            executeUpdate(
                "UPDATE fatura SET codigo=?, descricao=?, data_vencimento=?, valor=?, desconto=?, valor_final=?, status=?, observacoes=?, updated_at=datetime('now') WHERE id=?",
                f.getCodigo(), f.getDescricao(), f.getDataVencimento(), f.getValor(), f.getDesconto(), f.getValorFinal(), f.getStatus(), f.getObservacoes(), f.getId()
            );
        }
        return f;
    }
    public Optional<Fatura> findById(Long id) throws SQLException {
        return queryOne("SELECT f.*, e.razao_social as empresa_nome FROM fatura f LEFT JOIN empresa e ON f.empresa_id=e.id WHERE f.id=?", this::map, id);
    }
    public List<Fatura> findByEmpresa(Long empresaId) throws SQLException {
        return query("SELECT f.*, e.razao_social as empresa_nome FROM fatura f LEFT JOIN empresa e ON f.empresa_id=e.id WHERE f.empresa_id=? ORDER BY f.data_vencimento DESC", this::map, empresaId);
    }
    public List<Fatura> findAll() throws SQLException {
        return query("SELECT f.*, e.razao_social as empresa_nome FROM fatura f LEFT JOIN empresa e ON f.empresa_id=e.id ORDER BY f.data_vencimento DESC", this::map);
    }
    public List<Fatura> findPendentes() throws SQLException {
        return query("SELECT f.*, e.razao_social as empresa_nome FROM fatura f LEFT JOIN empresa e ON f.empresa_id=e.id WHERE f.status IN ('PENDENTE','PARCIAL') ORDER BY f.data_vencimento", this::map);
    }
    public List<Fatura> findVencidas() throws SQLException {
        return query("SELECT f.*, e.razao_social as empresa_nome FROM fatura f LEFT JOIN empresa e ON f.empresa_id=e.id WHERE f.status='PENDENTE' AND f.data_vencimento < date('now') ORDER BY f.data_vencimento", this::map);
    }
    public void delete(Long id) throws SQLException { executeUpdate("DELETE FROM fatura WHERE id=?", id); }
    private Long getLongOrNull(ResultSet rs, String col) throws SQLException { long v = rs.getLong(col); return rs.wasNull() ? null : v; }
    private LocalDateTime parseDateTime(String s) { if (s == null) return null; return LocalDateTime.parse(s.replace(' ', 'T')); }
}


