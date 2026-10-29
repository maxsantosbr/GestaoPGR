package com.gestaopgr.ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class BackupPanel extends JPanel {
    private final MainFrame main;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public BackupPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Backup e Restauracao");
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

        JLabel icon = new JLabel("💾");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(icon);
        card.add(Box.createVerticalStrut(15));

        JLabel desc = new JLabel("<html><div style='text-align:center; width:400px'>"
            + "Exporte o banco de dados para um arquivo de backup ou restaure a partir de um backup existente.</div></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(100, 116, 139));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(desc);
        card.add(Box.createVerticalStrut(20));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setMaximumSize(new Dimension(400, 25));
        card.add(progressBar);
        card.add(Box.createVerticalStrut(10));

        statusLabel = new JLabel("Pronto");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(100, 116, 139));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(20));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btns.setOpaque(false);
        JButton btnExportar = new JButton("Exportar Backup");
        btnExportar.addActionListener(e -> exportarBackup());
        JButton btnImportar = new JButton("Restaurar Backup");
        btnImportar.addActionListener(e -> importarBackup());
        btns.add(btnExportar); btns.add(btnImportar);
        btns.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(btns);

        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private void exportarBackup() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Salvar Backup");
        fc.setFileFilter(new FileNameExtensionFilter("Banco SQLite (*.db)", "db"));
        fc.setSelectedFile(new File("gestaopgr_backup_" + java.time.LocalDate.now() + ".db"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            protected Void doInBackground() throws Exception {
                statusLabel.setText("Exportando...");
                for (int i = 0; i <= 100; i += 10) {
                    Thread.sleep(100);
                    publish(i);
                }
                // Copy db file
                String dbPath = com.gestaopgr.config.DatabaseConfig.getDbPath();
                java.nio.file.Files.copy(new File(dbPath).toPath(), fc.getSelectedFile().toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return null;
            }
            protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }
            protected void done() {
                try { get(); statusLabel.setText("Backup exportado com sucesso!"); }
                catch (Exception e) { statusLabel.setText("Erro: " + e.getMessage()); }
            }
        };
        worker.execute();
    }

    private void importarBackup() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Restaurar Backup");
        fc.setFileFilter(new FileNameExtensionFilter("Banco SQLite (*.db)", "db"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        int conf = JOptionPane.showConfirmDialog(this, "Restaurar backup ira substituir todos os dados atuais. Continuar?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            protected Void doInBackground() throws Exception {
                statusLabel.setText("Restaurando...");
                for (int i = 0; i <= 100; i += 10) {
                    Thread.sleep(100);
                    publish(i);
                }
                String dbPath = com.gestaopgr.config.DatabaseConfig.getDbPath();
                java.nio.file.Files.copy(fc.getSelectedFile().toPath(), new File(dbPath).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return null;
            }
            protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }
            protected void done() {
                try { get(); statusLabel.setText("Backup restaurado. Reinicie a aplicacao."); }
                catch (Exception e) { statusLabel.setText("Erro: " + e.getMessage()); }
            }
        };
        worker.execute();
    }
}
