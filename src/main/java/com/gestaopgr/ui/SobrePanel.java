package com.gestaopgr.ui;

import javax.swing.*;
import java.awt.*;

public class SobrePanel extends JPanel {
    public SobrePanel(MainFrame main) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Sobre o Sistema");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        JLabel icon = new JLabel("🛡");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(icon);
        card.add(Box.createVerticalStrut(15));

        JLabel nome = new JLabel("Gestão PGR");
        nome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nome.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nome);
        card.add(Box.createVerticalStrut(5));

        JLabel versao = new JLabel("Versão 1.0.0");
        versao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        versao.setForeground(new Color(100, 116, 139));
        versao.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(versao);
        card.add(Box.createVerticalStrut(15));

        JLabel desc = new JLabel("<html><div style='text-align:center; width:400px'>"
            + "Sistema desktop para gestão de PGR (Programa de Gerenciamento de Riscos) "
            + "conforme NR-01. Desenvolvido para técnicos e engenheiros de segurança do trabalho.</div></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(100, 116, 139));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(desc);
        card.add(Box.createVerticalStrut(20));

        JLabel info = new JLabel("<html><div style='text-align:center'>"
            + "Desenvolvido por Maxwell Souza dos Santos<br>"
            + "CNPJ 54.718.287/0001-19<br>"
            + "© 2026 - Todos os direitos reservados</div></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(new Color(148, 163, 184));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(info);

        center.add(card);
        add(center, BorderLayout.CENTER);
    }
}
