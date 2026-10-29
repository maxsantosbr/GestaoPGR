package com.gestaopgr.dao;

import com.gestaopgr.model.Cargo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CargoDao extends BaseDao {
    private Cargo map(ResultSet rs) throws SQLException {
        Cargo c = new Cargo();
        c.setId(rs.getLong("id"));
        c.setEmpresaId(rs.getLong("empresa_id"));
        c.setNome(rs.getString("nome"));
        c.setCbo(rs.getString("cbo"));
        c.setDescricao(rs.getString("descricao"));
        c.setAtivo(rs.getInt("ativo") == 1);
        c.setCreatedAt(parseDateTime(rs.getString("created_at")));
        return c;
    }
    public Cargo save(Cargo c) throws SQLException {
        if (c.getId() == null) {
            long id = executeInsert("INSERT INTO cargo (empresa_id, nome, cbo, descricao, ativo) VALUES (?,?,?,?,?)",
                c.getEmpresaId(), c.getNome(), c.getCbo(), c.getDescricao(), c.isAtivo());
            c.setId(id);
        } else {
            executeUpdate("UPDATE cargo SET nome=?, cbo=?, descricao=?, ativo=? WHERE id=?",
                c.getNome(), c.getCbo(), c.getDescricao(), c.isAtivo(), c.getId());
        }
        return c;
    }
    public Optional<Cargo> findById(Long id) throws SQLException { return queryOne("SELECT * FROM cargo WHERE id=?", this::map, id); }
    public List<Cargo> findByEmpresa(Long empresaId) throws SQLException { return query("SELECT * FROM cargo WHERE empresa_id=? ORDER BY nome", this::map, empresaId); }
    public List<Cargo> findAll() throws SQLException { return query("SELECT * FROM cargo ORDER BY nome", this::map); }
    public void delete(Long id) throws SQLException { executeUpdate("DELETE FROM cargo WHERE id=?", id); }
    private LocalDateTime parseDateTime(String s) { if (s == null) return null; return LocalDateTime.parse(s.replace(' ', 'T')); }
}
