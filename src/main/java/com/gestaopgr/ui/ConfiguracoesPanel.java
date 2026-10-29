package com.gestaopgr.ui;

import javax.swing.*;
import java.awt.*;

public class ConfiguracoesPanel extends JPanel {
    private final MainFrame main;
    private JTextField txtNome, txtRegistro, txtEmail, txtTelefone, txtEmpresa;
    private JComboBox<String> cbTema;

    public ConfiguracoesPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Configuracoes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome = new JTextField(30); txtRegistro = new JTextField(30);
        txtEmail = new JTextField(30); txtTelefone = new JTextField(30);
        txtEmpresa = new JTextField(30);
        cbTema = new JComboBox<>(new String[]{"Claro", "Escuro", "Nord", "Dracula"});

        // Load config
        try {
            java.sql.Connection conn = com.gestaopgr.config.DatabaseConfig.getInstance().getConnection();
            java.sql.ResultSet rs = conn.createStatement().executeQuery("SELECT chave, valor FROM configuracao");
            while (rs.next()) {
                String chave = rs.getString("chave");
                String valor = rs.getString("valor");
                if ("tecnico_nome".equals(chave)) txtNome.setText(valor != null ? valor : "");
                else if ("tecnico_registro".equals(chave)) txtRegistro.setText(valor != null ? valor : "");
                else if ("tecnico_email".equals(chave)) txtEmail.setText(valor != null ? valor : "");
                else if ("tecnico_telefone".equals(chave)) txtTelefone.setText(valor != null ? valor : "");
                else if ("empresa_nome".equals(chave)) txtEmpresa.setText(valor != null ? valor : "");
                else if ("tema".equals(chave)) {
                    if (valor != null) {
                        switch (valor) {
                            case "claro": cbTema.setSelectedIndex(0); break;
                            case "escuro": cbTema.setSelectedIndex(1); break;
                            case "nord": cbTema.setSelectedIndex(2); break;
                            case "dracula": cbTema.setSelectedIndex(3); break;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        int row = 0;
        addField(form, gbc, row++, "Tema:", cbTema);
        addField(form, gbc, row++, "Nome do Tecnico:", txtNome);
        addField(form, gbc, row++, "Registro (CREA/CFT):", txtRegistro);
        addField(form, gbc, row++, "E-mail:", txtEmail);
        addField(form, gbc, row++, "Telefone:", txtTelefone);
        addField(form, gbc, row++, "Nome da Empresa/Consultoria:", txtEmpresa);

        add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar Configuracoes");
        btnSalvar.addActionListener(e -> salvar());
        JButton btnAplicarTema = new JButton("Aplicar Tema");
        btnAplicarTema.addActionListener(e -> aplicarTema());
        btns.add(btnAplicarTema); btns.add(btnSalvar);
        add(btns, BorderLayout.SOUTH);
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(field, gbc);
    }

    private void salvar() {
        try {
            java.sql.Connection conn = com.gestaopgr.config.DatabaseConfig.getInstance().getConnection();
            String[] chaves = {"tecnico_nome", "tecnico_registro", "tecnico_email", "tecnico_telefone", "empresa_nome", "tema"};
            String[] valores = {txtNome.getText(), txtRegistro.getText(), txtEmail.getText(), txtTelefone.getText(), txtEmpresa.getText(), getTemaKey()};
            for (int i = 0; i < chaves.length; i++) {
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR REPLACE INTO configuracao (chave, valor, updated_at) VALUES (?,?,datetime('now'))")) {
                    ps.setString(1, chaves[i]); ps.setString(2, valores[i]);
                    ps.executeUpdate();
                }
            }
            JOptionPane.showMessageDialog(this, "Configuracoes salvas com sucesso.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarTema() {
        try {
            String tema = getTemaKey();
            com.formdev.flatlaf.FlatLaf laf = null;
            switch (tema) {
                case "claro": laf = new com.formdev.flatlaf.FlatLightLaf(); break;
                case "escuro": laf = new com.formdev.flatlaf.FlatDarkLaf(); break;
                case "nord": laf = new com.formdev.flatlaf.intellijthemes.FlatNordIJTheme(); break;
                case "dracula": laf = new com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme(); break;
            }
            if (laf != null) {
                javax.swing.UIManager.setLookAndFeel(laf);
                javax.swing.SwingUtilities.updateComponentTreeUI(main);
                JOptionPane.showMessageDialog(this, "Tema aplicado. Reinicie a aplicacao para efeito completo.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao aplicar tema: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getTemaKey() {
        int idx = cbTema.getSelectedIndex();
        switch (idx) {
            case 0: return "claro";
            case 1: return "escuro";
            case 2: return "nord";
            case 3: return "dracula";
            default: return "claro";
        }
    }
}
