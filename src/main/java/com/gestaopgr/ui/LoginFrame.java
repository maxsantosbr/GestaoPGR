package com.gestaopgr.ui;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JLabel lblStatus;

    public LoginFrame() {
        setTitle("Gestão PGR - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        // ALTERADO: carregamento da logo com redução em etapas (sem serrilhado)
        // Carregamento da logo com redimensionamento de alta qualidade
ImageIcon logoIcon = null;

try {
    java.net.URL logoUrl =
            getClass().getClassLoader().getResource("gestao-pgr-logo.png");

    if (logoUrl != null) {
        java.awt.image.BufferedImage original =
                javax.imageio.ImageIO.read(logoUrl);

        if (original != null) {
            int targetWidth = 360;

            int targetHeight = Math.max(
                    1,
                    (int) Math.round(
                            targetWidth * (original.getHeight()
                                    / (double) original.getWidth())
                    )
            );

            java.awt.image.BufferedImage imagemFinal =
                    redimensionarComQualidade(
                            original,
                            targetWidth,
                            targetHeight
                    );

            logoIcon = new ImageIcon(imagemFinal);
        }
    }
} catch (Exception e) {
    e.printStackTrace();
}

        if (logoIcon != null) {
            final ImageIcon finalLogo = logoIcon;
            JPanel logoPanel = new JPanel() {
                // ALTERADO: desenha a imagem pronta, sem redimensionar de novo
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    finalLogo.paintIcon(this, g, 0, 0);
                }
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(finalLogo.getIconWidth(), finalLogo.getIconHeight());
                }
                @Override
                public Dimension getMinimumSize() {
                    return getPreferredSize();
                }
                @Override
                public Dimension getMaximumSize() {
                    return getPreferredSize();
                }
            };
            logoPanel.setOpaque(false);
            logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(logoPanel);
        } else {
            // Fallback: escudo + texto
            JLabel icon = new JLabel("🛡");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(icon);
            card.add(Box.createVerticalStrut(10));

            JLabel title = new JLabel("Gestão PGR");
            title.setFont(new Font("Segoe UI", Font.BOLD, 24));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(title);
        }
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

        txtLogin.addActionListener(e -> txtSenha.requestFocusInWindow());
        txtSenha.addActionListener(e -> autenticar());

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

        // ALTERADO: removido "JLabel" na frente, para usar o campo da classe
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

        add(card);
    }

    private void autenticar() {
        try {
            String login = txtLogin.getText().trim();
            String senha = new String(txtSenha.getPassword());

            if (login.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha usuário e senha.");
                return;
            }

            java.sql.Connection conn = com.gestaopgr.config.DatabaseConfig.getInstance().getConnection();
            try (java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT id, nome, perfil FROM usuario WHERE login=? AND senha_hash=? AND ativo=1")) {
                ps.setString(1, login);
                ps.setString(2, senha);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Extrair valores antes de fechar o ResultSet
                        final long userId = rs.getLong("id");
                        final String userName = rs.getString("nome");
                        final String userPerfil = rs.getString("perfil");
                        // Login bem-sucedido
                        dispose();
                        // Abrir MainFrame
                        SwingUtilities.invokeLater(() -> {
                            MainFrame mainFrame = new MainFrame();
                            mainFrame.setUsuarioLogado(userId, userName, userPerfil);
                            mainFrame.mostrarSistema();
                            mainFrame.setVisible(true);
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ALTERADO: NOVO MÉTODO - reduz a imagem pela metade, em etapas, até chegar perto do tamanho final
    private static java.awt.image.BufferedImage redimensionarComQualidade(
            java.awt.image.BufferedImage origem, int larguraAlvo, int alturaAlvo) {
        java.awt.image.BufferedImage atual = origem;
        int w = origem.getWidth();
        int h = origem.getHeight();
        while (w / 2 >= larguraAlvo && h / 2 >= alturaAlvo) {
            w /= 2;
            h /= 2;
            atual = passoDeReducao(atual, w, h);
        }
        if (w != larguraAlvo || h != alturaAlvo) {
            atual = passoDeReducao(atual, larguraAlvo, alturaAlvo);
        }
        return atual;
    }

    // ALTERADO: NOVO MÉTODO - um passo de redução com interpolação bicúbica
    private static java.awt.image.BufferedImage passoDeReducao(
            java.awt.image.BufferedImage origem, int w, int h) {
        java.awt.image.BufferedImage destino =
                new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = destino.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(origem, 0, 0, w, h, null);
        g2.dispose();
        return destino;
    }
}