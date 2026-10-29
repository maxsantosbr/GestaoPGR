package com.gestaopgr.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuarioPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;

    public UsuarioPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Usuários do Sistema");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNovo = new JButton("Novo Usuário");
        btnNovo.addActionListener(e -> abrirFormulario(null));
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarSelecionado());
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnPanel.add(btnNovo); btnPanel.add(btnEditar); btnPanel.add(btnExcluir);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Login", "Nome", "E-mail", "Perfil", "Ativo"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);

        carregarDados();
    }

    private void carregarDados() {
        tableModel.setRowCount(0);
        try {
            java.sql.Connection conn = com.gestaopgr.config.DatabaseConfig.getInstance().getConnection();
            java.sql.ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM usuario ORDER BY nome");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getLong("id"), rs.getString("login"), rs.getString("nome"),
                    rs.getString("email"), rs.getString("perfil"), rs.getInt("ativo") == 1 ? "Sim" : "Não"
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void abrirFormulario(com.gestaopgr.model.Usuario usuario) {
        UsuarioFormDialog d = new UsuarioFormDialog(main, usuario);
        d.setVisible(true);
        if (d.isSaved()) carregarDados();
    }

    private void editarSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um usuário."); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            java.sql.Connection conn = com.gestaopgr.config.DatabaseConfig.getInstance().getConnection();
            try (java.sql.PreparedStatement ps = conn.prepareStatement("SELECT * FROM usuario WHERE id=?")) {
                ps.setLong(1, id);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        com.gestaopgr.model.Usuario u = new com.gestaopgr.model.Usuario();
                        u.setId(rs.getLong("id"));
                        u.setLogin(rs.getString("login"));
                        u.setSenhaHash(rs.getString("senha_hash"));
                        u.setNome(rs.getString("nome"));
                        u.setEmail(rs.getString("email"));
                        u.setPerfil(com.gestaopgr.model.Usuario.Perfil.valueOf(rs.getString("perfil")));
                        u.setAtivo(rs.getInt("ativo") == 1);
                        abrirFormulario(u);
                    }
                }
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void excluirSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um usuário."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir usuário?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            java.sql.Connection conn = com.gestaopgr.config.DatabaseConfig.getInstance().getConnection();
            try (java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM usuario WHERE id=?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            carregarDados();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}

class UsuarioFormDialog extends JDialog {
    private boolean saved = false;
    private final MainFrame main;
    private com.gestaopgr.model.Usuario usuario;
    private JTextField txtLogin, txtNome, txtEmail, txtSenha;
    private JComboBox<String> cbPerfil;
    private JCheckBox chkAtivo;

    public UsuarioFormDialog(MainFrame main, com.gestaopgr.model.Usuario usuario) {
        super(main, "Usuário", true);
        this.main = main; this.usuario = usuario;
        setSize(420, 320); setLocationRelativeTo(main);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtLogin = new JTextField(20); txtNome = new JTextField(20);
        txtEmail = new JTextField(20); txtSenha = new JTextField(20);
        cbPerfil = new JComboBox<>(new String[]{"SUPER_ADMIN", "ADMIN", "TECNICO", "CONSULTA"});
        chkAtivo = new JCheckBox("Ativo", true);

        int row = 0;
        addField(form, gbc, row++, "Login:*", txtLogin);
        addField(form, gbc, row++, "Nome:*", txtNome);
        addField(form, gbc, row++, "E-mail:", txtEmail);
        addField(form, gbc, row++, "Senha:*", txtSenha);
        addField(form, gbc, row++, "Perfil:", cbPerfil);
        addField(form, gbc, row++, "Ativo:", chkAtivo);

        if (usuario != null) {
            txtLogin.setText(usuario.getLogin());
            txtNome.setText(usuario.getNome());
            txtEmail.setText(usuario.getEmail());
            txtSenha.setText(usuario.getSenhaHash());
            cbPerfil.setSelectedItem(usuario.getPerfil().name());
            chkAtivo.setSelected(usuario.isAtivo());
        }

        add(form, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        btns.add(btnSalvar); btns.add(btnCancelar);
        add(btns, BorderLayout.SOUTH);
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(field, gbc);
    }

    private void salvar() {
        try {
            if (txtLogin.getText().trim().isEmpty() || txtNome.getText().trim().isEmpty() || txtSenha.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Login, Nome e Senha são obrigatórios."); return;
            }
            java.sql.Connection conn = com.gestaopgr.config.DatabaseConfig.getInstance().getConnection();
            if (usuario == null) {
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO usuario (login, senha_hash, nome, email, perfil, ativo) VALUES (?,?,?,?,?,?)")) {
                    ps.setString(1, txtLogin.getText().trim());
                    ps.setString(2, txtSenha.getText().trim());
                    ps.setString(3, txtNome.getText().trim());
                    ps.setString(4, txtEmail.getText().trim());
                    ps.setString(5, (String) cbPerfil.getSelectedItem());
                    ps.setInt(6, chkAtivo.isSelected() ? 1 : 0);
                    ps.executeUpdate();
                }
            } else {
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                    "UPDATE usuario SET login=?, senha_hash=?, nome=?, email=?, perfil=?, ativo=? WHERE id=?")) {
                    ps.setString(1, txtLogin.getText().trim());
                    ps.setString(2, txtSenha.getText().trim());
                    ps.setString(3, txtNome.getText().trim());
                    ps.setString(4, txtEmail.getText().trim());
                    ps.setString(5, (String) cbPerfil.getSelectedItem());
                    ps.setInt(6, chkAtivo.isSelected() ? 1 : 0);
                    ps.setLong(7, usuario.getId());
                    ps.executeUpdate();
                }
            }
            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}

