package com.gestaopgr.ui;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private final MainFrame main;
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JLabel lblStatus;

    public LoginPanel(MainFrame main) {
        this.main = main;
        setLayout(new GridBagLayout());
        setBackground(new Color(248, 250, 252));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));

        JLabel icon = new JLabel("🛡");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(icon);
        card.add(Box.createVerticalStrut(10));

        JLabel title = new JLabel("Gestão PGR");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Faça login para continuar");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(100, 116, 139));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(25));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtLogin = new JTextField(20);
        txtSenha = new JPasswordField(20);

        // Enter no campo de login desce para o campo de senha
        txtLogin.addActionListener(e -> txtSenha.requestFocusInWindow());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        form.add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(txtLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        form.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(txtSenha, gbc);

        form.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(form);
        card.add(Box.createVerticalStrut(15));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(239, 68, 68));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblStatus);
        card.add(Box.createVerticalStrut(10));

        JButton btnLogin = new JButton("Entrar");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(59, 130, 246));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(200, 36));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> autenticar());
        card.add(btnLogin);

        txtSenha.addActionListener(e -> autenticar());

        add(card);
    }

    private void autenticar() {
        try {
            String login = txtLogin.getText().trim();
            String senha = new String(txtSenha.getPassword());

            if (login.isEmpty() || senha.isEmpty()) {
                lblStatus.setText("Preencha usuário e senha.");
                return;
            }

            // Buscar usuário no banco
            java.sql.Connection conn = com.gestaopgr.config.DatabaseConfig.getInstance().getConnection();
            try (java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT id, nome, perfil FROM usuario WHERE login=? AND senha_hash=? AND ativo=1")) {
                ps.setString(1, login);
                ps.setString(2, senha);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        lblStatus.setText(" ");
                        main.setUsuarioLogado(rs.getLong("id"), rs.getString("nome"), rs.getString("perfil"));
                        main.mostrarSistema();
                    } else {
                        lblStatus.setText("Usuário ou senha inválidos.");
                    }
                }
            }
        } catch (Exception e) {
            lblStatus.setText("Erro: " + e.getMessage());
        }
    }
}
