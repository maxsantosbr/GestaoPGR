package com.gestaopgr.dao;

import com.gestaopgr.model.Risco;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RiscoDao extends BaseDao {
    private Risco map(ResultSet rs) throws SQLException {
        Risco r = new Risco();
        r.setId(rs.getLong("id"));
        r.setEmpresaId(rs.getLong("empresa_id"));
        r.setSetorId(getLongOrNull(rs, "setor_id"));
        r.setCargoId(getLongOrNull(rs, "cargo_id"));
        r.setPerigoId(getLongOrNull(rs, "perigo_id"));
        r.setPerigoDescricao(rs.getString("perigo_descricao"));
        r.setCategoria(rs.getString("categoria"));
        r.setFonte(rs.getString("fonte"));
        r.setAtividade(rs.getString("atividade"));
        r.setNumTrabalhadoresExpostos(rs.getInt("num_trabalhadores_expostos"));
        r.setProbabilidade(rs.getInt("probabilidade"));
        r.setSeveridade(rs.getInt("severidade"));
        r.setNivelRisco(rs.getInt("nivel_risco"));
        r.setClassificacao(rs.getString("classificacao"));
        r.setMedidasExistentes(rs.getString("medidas_existentes"));
        r.setMedidasPropostas(rs.getString("medidas_propostas"));
        r.setEpiRecomendado(rs.getString("epi_recomendado"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(parseDateTime(rs.getString("created_at")));
        r.setUpdatedAt(parseDateTime(rs.getString("updated_at")));
        try { r.setSetorNome(rs.getString("setor_nome")); } catch (SQLException ignored) {}
        try { r.setCargoNome(rs.getString("cargo_nome")); } catch (SQLException ignored) {}
        try { r.setEmpresaNome(rs.getString("empresa_nome")); } catch (SQLException ignored) {}
        return r;
    }

    public Risco save(Risco r) throws SQLException {
        r.calcularNivel();
        if (r.getId() == null) {
            long id = executeInsert(
                "INSERT INTO risco (empresa_id, setor_id, cargo_id, perigo_id, perigo_descricao, categoria, fonte, atividade, num_trabalhadores_expostos, probabilidade, severidade, nivel_risco, classificacao, medidas_existentes, medidas_propostas, epi_recomendado, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                r.getEmpresaId(), r.getSetorId(), r.getCargoId(), r.getPerigoId(), r.getPerigoDescricao(),
                r.getCategoria(), r.getFonte(), r.getAtividade(), r.getNumTrabalhadoresExpostos(),
                r.getProbabilidade(), r.getSeveridade(), r.getNivelRisco(), r.getClassificacao(),
                r.getMedidasExistentes(), r.getMedidasPropostas(), r.getEpiRecomendado(), r.getStatus()
            );
            r.setId(id);
        } else {
            executeUpdate(
                "UPDATE risco SET setor_id=?, cargo_id=?, perigo_id=?, perigo_descricao=?, categoria=?, fonte=?, atividade=?, num_trabalhadores_expostos=?, probabilidade=?, severidade=?, nivel_risco=?, classificacao=?, medidas_existentes=?, medidas_propostas=?, epi_recomendado=?, status=?, updated_at=datetime('now') WHERE id=?",
                r.getSetorId(), r.getCargoId(), r.getPerigoId(), r.getPerigoDescricao(), r.getCategoria(),
                r.getFonte(), r.getAtividade(), r.getNumTrabalhadoresExpostos(), r.getProbabilidade(),
                r.getSeveridade(), r.getNivelRisco(), r.getClassificacao(), r.getMedidasExistentes(),
                r.getMedidasPropostas(), r.getEpiRecomendado(), r.getStatus(), r.getId()
            );
        }
        return r;
    }

    public Optional<Risco> findById(Long id) throws SQLException {
        String sql = "SELECT r.*, s.nome as setor_nome, c.nome as cargo_nome, e.razao_social as empresa_nome " +
                     "FROM risco r LEFT JOIN setor s ON r.setor_id=s.id LEFT JOIN cargo c ON r.cargo_id=c.id " +
                     "LEFT JOIN empresa e ON r.empresa_id=e.id WHERE r.id=?";
        return queryOne(sql, this::map, id);
    }

    public List<Risco> findByEmpresa(Long empresaId) throws SQLException {
        String sql = "SELECT r.*, s.nome as setor_nome, c.nome as cargo_nome, e.razao_social as empresa_nome " +
                     "FROM risco r LEFT JOIN setor s ON r.setor_id=s.id LEFT JOIN cargo c ON r.cargo_id=c.id " +
                     "LEFT JOIN empresa e ON r.empresa_id=e.id WHERE r.empresa_id=? ORDER BY r.classificacao DESC, r.nivel_risco DESC";
        return query(sql, this::map, empresaId);
    }

    public List<Risco> findAll() throws SQLException {
        String sql = "SELECT r.*, s.nome as setor_nome, c.nome as cargo_nome, e.razao_social as empresa_nome " +
                     "FROM risco r LEFT JOIN setor s ON r.setor_id=s.id LEFT JOIN cargo c ON r.cargo_id=c.id " +
                     "LEFT JOIN empresa e ON r.empresa_id=e.id ORDER BY r.classificacao DESC";
        return query(sql, this::map);
    }

    public List<Risco> findByClassificacao(Long empresaId, String classificacao) throws SQLException {
        String sql = "SELECT r.*, s.nome as setor_nome, c.nome as cargo_nome, e.razao_social as empresa_nome " +
                     "FROM risco r LEFT JOIN setor s ON r.setor_id=s.id LEFT JOIN cargo c ON r.cargo_id=c.id " +
                     "LEFT JOIN empresa e ON r.empresa_id=e.id WHERE r.empresa_id=? AND r.classificacao=? ORDER BY r.nivel_risco DESC";
        return query(sql, this::map, empresaId, classificacao);
    }

    public void delete(Long id) throws SQLException { executeUpdate("DELETE FROM risco WHERE id=?", id); }

    private Long getLongOrNull(ResultSet rs, String col) throws SQLException { long v = rs.getLong(col); return rs.wasNull() ? null : v; }
    private LocalDateTime parseDateTime(String s) { if (s == null) return null; return LocalDateTime.parse(s.replace(' ', 'T')); }
}
