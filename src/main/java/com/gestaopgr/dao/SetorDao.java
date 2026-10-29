package com.gestaopgr.dao;

import com.gestaopgr.model.Setor;
import com.gestaopgr.model.Cargo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class SetorDao extends BaseDao {
    private Setor map(ResultSet rs) throws SQLException {
        Setor s = new Setor();
        s.setId(rs.getLong("id"));
        s.setEmpresaId(rs.getLong("empresa_id"));
        s.setNome(rs.getString("nome"));
        s.setDescricao(rs.getString("descricao"));
        s.setNumTrabalhadores(rs.getInt("num_trabalhadores"));
        s.setAtivo(rs.getInt("ativo") == 1);
        s.setCreatedAt(parseDateTime(rs.getString("created_at")));
        try { s.setEmpresaNome(rs.getString("empresa_nome")); } catch (SQLException ignored) {}
        return s;
    }
    public Setor save(Setor s) throws SQLException {
        if (s.getId() == null) {
            long id = executeInsert("INSERT INTO setor (empresa_id, nome, descricao, num_trabalhadores, ativo) VALUES (?,?,?,?,?)",
                s.getEmpresaId(), s.getNome(), s.getDescricao(), s.getNumTrabalhadores(), s.isAtivo());
            s.setId(id);
        } else {
            executeUpdate("UPDATE setor SET nome=?, descricao=?, num_trabalhadores=?, ativo=? WHERE id=?",
                s.getNome(), s.getDescricao(), s.getNumTrabalhadores(), s.isAtivo(), s.getId());
        }
        return s;
    }
    public Optional<Setor> findById(Long id) throws SQLException {
        return queryOne("SELECT s.*, e.razao_social as empresa_nome FROM setor s LEFT JOIN empresa e ON s.empresa_id=e.id WHERE s.id=?", this::map, id);
    }
    public List<Setor> findByEmpresa(Long empresaId) throws SQLException {
        return query("SELECT s.*, e.razao_social as empresa_nome FROM setor s LEFT JOIN empresa e ON s.empresa_id=e.id WHERE s.empresa_id=? ORDER BY s.nome", this::map, empresaId);
    }
    public List<Setor> findAll() throws SQLException {
        return query("SELECT s.*, e.razao_social as empresa_nome FROM setor s LEFT JOIN empresa e ON s.empresa_id=e.id ORDER BY e.razao_social, s.nome", this::map);
    }
    public void delete(Long id) throws SQLException { executeUpdate("DELETE FROM setor WHERE id=?", id); }
    private LocalDateTime parseDateTime(String s) { if (s == null) return null; return LocalDateTime.parse(s.replace(' ', 'T')); }
}

