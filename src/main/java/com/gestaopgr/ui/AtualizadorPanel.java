package com.gestaopgr.ui;

import com.gestaopgr.service.Atualizador;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AtualizadorPanel extends JPanel {
    private final MainFrame main;
    private Atualizador atualizador;
    private JLabel lblVersaoAtual, lblVersaoDisponivel, lblStatus;
    private JProgressBar progressBar;
    private JTextArea txtLog;
    private JButton btnVerificar, btnBaixar;

    public AtualizadorPanel(MainFrame main) {
        this.main = main;
        this.atualizador = new Atualizador();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Atualizador do Software");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        // Versão atual
        JPanel versaoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        versaoPanel.setOpaque(false);
        versaoPanel.add(new JLabel("Versao atual:"));
        lblVersaoAtual = new JLabel(Atualizador.VERSAO_ATUAL);
        lblVersaoAtual.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblVersaoAtual.setForeground(new Color(59, 130, 246));
        versaoPanel.add(lblVersaoAtual);
        versaoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(versaoPanel);

        // Versão disponível
        JPanel dispPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        dispPanel.setOpaque(false);
        dispPanel.add(new JLabel("Versao disponivel:"));
        lblVersaoDisponivel = new JLabel("-");
        lblVersaoDisponivel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dispPanel.add(lblVersaoDisponivel);
        dispPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(dispPanel);

        card.add(Box.createVerticalStrut(8));

        // Barra de progresso
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setMaximumSize(new Dimension(400, 22));
        card.add(progressBar);
        card.add(Box.createVerticalStrut(5));

        // Status
        lblStatus = new JLabel("Clique em 'Verificar Atualizacoes' para comecar.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(100, 116, 139));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblStatus);
        card.add(Box.createVerticalStrut(8));

        // Log
        txtLog = new JTextArea(6, 40);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setBackground(new Color(248, 250, 252));
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollLog.setMaximumSize(new Dimension(500, 120));
        scrollLog.setPreferredSize(new Dimension(500, 120));
        scrollLog.setBorder(BorderFactory.createEmptyBorder());
        scrollLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollLog.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        card.add(scrollLog);
        card.add(Box.createVerticalStrut(8));

        // Botões
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btns.setOpaque(false);
        btnVerificar = new JButton("Verificar Atualizacoes");
        btnVerificar.addActionListener(e -> verificar());
        btnBaixar = new JButton("Baixar e Instalar");
        btnBaixar.setEnabled(false);
        btnBaixar.addActionListener(e -> baixar());
        btns.add(btnVerificar);
        btns.add(btnBaixar);
        btns.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(btns);

        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append(msg + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }

    private void verificar() {
        btnVerificar.setEnabled(false);
        btnBaixar.setEnabled(false);
        txtLog.setText("");
        progressBar.setValue(0);
        lblStatus.setText("Verificando...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            protected Boolean doInBackground() {
                return atualizador.verificarAtualizacao((percent, message) -> {
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(percent);
                        lblStatus.setText(message);
                        log(message);
                    });
                });
            }
            protected void done() {
                try {
                    boolean temAtualizacao = get();
                    if (temAtualizacao) {
                        lblVersaoDisponivel.setText(atualizador.getVersaoDisponivel());
                        lblVersaoDisponivel.setForeground(new Color(34, 197, 94));
                        btnBaixar.setEnabled(true);
                        log("Nova versao disponivel: " + atualizador.getVersaoDisponivel());
                        if (!atualizador.getReleaseNotes().isEmpty()) {
                            log("Notas: " + atualizador.getReleaseNotes());
                        }
                    } else {
                        lblVersaoDisponivel.setText(atualizador.getVersaoAtual());
                        lblVersaoDisponivel.setForeground(new Color(100, 116, 139));
                        log("Voce ja esta na versao mais recente.");
                    }
                } catch (Exception e) {
                    log("Erro: " + e.getMessage());
                }
                btnVerificar.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void baixar() {
        btnVerificar.setEnabled(false);
        btnBaixar.setEnabled(false);
        progressBar.setValue(0);
        lblStatus.setText("Baixando...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            protected Boolean doInBackground() {
                String destino = System.getProperty("user.home") + File.separator + "GestaoPGR" + File.separator + "atualizacao.jar";
                new File(System.getProperty("user.home") + File.separator + "GestaoPGR").mkdirs();
                return atualizador.baixarAtualizacao(destino, (percent, message) -> {
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(percent);
                        lblStatus.setText(message);
                        log(message);
                    });
                });
            }
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        log("Atualizacao baixada com sucesso!");
                        log("Para instalar, feche o software e execute o arquivo atualizacao.jar");
                        lblStatus.setText("Download concluido. Reinicie para instalar.");
                        JOptionPane.showMessageDialog(AtualizadorPanel.this,
                            "Atualizacao baixada com sucesso!\n\nLocal: " + System.getProperty("user.home") + File.separator + "GestaoPGR" + File.separator + "atualizacao.jar" +
                            "\n\nFeche o software e execute o arquivo para instalar.",
                            "Atualizacao", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        log("Falha no download.");
                        lblStatus.setText("Falha no download.");
                    }
                } catch (Exception e) {
                    log("Erro: " + e.getMessage());
                }
                btnVerificar.setEnabled(true);
                btnBaixar.setEnabled(true);
            }
        };
        worker.execute();
    }
}
