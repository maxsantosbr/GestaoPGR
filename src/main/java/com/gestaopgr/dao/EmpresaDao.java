package com.gestaopgr.dao;

import com.gestaopgr.model.Empresa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EmpresaDao extends BaseDao {
    private Empresa map(ResultSet rs) throws SQLException {
        Empresa e = new Empresa();
        e.setId(rs.getLong("id"));
        e.setRazaoSocial(rs.getString("razao_social"));
        e.setNomeFantasia(rs.getString("nome_fantasia"));
        e.setCnpj(rs.getString("cnpj"));
        e.setCnae(rs.getString("cnae"));
        e.setCnaeDescricao(rs.getString("cnae_descricao"));
        e.setEndereco(rs.getString("endereco"));
        e.setCidade(rs.getString("cidade"));
        e.setUf(rs.getString("uf"));
        e.setCep(rs.getString("cep"));
        e.setTelefone(rs.getString("telefone"));
        e.setEmail(rs.getString("email"));
        e.setResponsavel(rs.getString("responsavel"));
        e.setNumEmpregados(rs.getInt("num_empregados"));
        e.setLogoPath(rs.getString("logo_path"));
        e.setAtivo(rs.getInt("ativo") == 1);
        e.setCreatedAt(parseDateTime(rs.getString("created_at")));
        e.setUpdatedAt(parseDateTime(rs.getString("updated_at")));
        return e;
    }

    public Empresa save(Empresa e) throws SQLException {
        if (e.getId() == null) {
            long id = executeInsert(
                "INSERT INTO empresa (razao_social, nome_fantasia, cnpj, cnae, cnae_descricao, endereco, cidade, uf, cep, telefone, email, responsavel, num_empregados, logo_path, ativo) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                e.getRazaoSocial(), e.getNomeFantasia(), e.getCnpj(), e.getCnae(), e.getCnaeDescricao(),
                e.getEndereco(), e.getCidade(), e.getUf(), e.getCep(), e.getTelefone(), e.getEmail(),
                e.getResponsavel(), e.getNumEmpregados(), e.getLogoPath(), e.isAtivo()
            );
            e.setId(id);
        } else {
            executeUpdate(
                "UPDATE empresa SET razao_social=?, nome_fantasia=?, cnpj=?, cnae=?, cnae_descricao=?, endereco=?, cidade=?, uf=?, cep=?, telefone=?, email=?, responsavel=?, num_empregados=?, logo_path=?, ativo=?, updated_at=datetime('now') WHERE id=?",
                e.getRazaoSocial(), e.getNomeFantasia(), e.getCnpj(), e.getCnae(), e.getCnaeDescricao(),
                e.getEndereco(), e.getCidade(), e.getUf(), e.getCep(), e.getTelefone(), e.getEmail(),
                e.getResponsavel(), e.getNumEmpregados(), e.getLogoPath(), e.isAtivo(), e.getId()
            );
        }
        return e;
    }

    public Optional<Empresa> findById(Long id) throws SQLException {
        return queryOne("SELECT * FROM empresa WHERE id=?", this::map, id);
    }

    public List<Empresa> findAll() throws SQLException {
        return query("SELECT * FROM empresa ORDER BY razao_social", this::map);
    }

    public List<Empresa> findAtivas() throws SQLException {
        return query("SELECT * FROM empresa WHERE ativo=1 ORDER BY razao_social", this::map);
    }

    public void delete(Long id) throws SQLException {
        executeUpdate("DELETE FROM empresa WHERE id=?", id);
    }

    private LocalDateTime parseDateTime(String s) {
        if (s == null) return null;
        return LocalDateTime.parse(s.replace(' ', 'T'));
    }
}
