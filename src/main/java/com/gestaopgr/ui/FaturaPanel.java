package com.gestaopgr.ui;

import com.gestaopgr.model.Fatura;
import com.gestaopgr.model.Empresa;
import com.gestaopgr.model.Pagamento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class FaturaPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;

    public FaturaPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Faturas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNova = new JButton("Nova Fatura");
        btnNova.addActionListener(e -> abrirFormulario(null));
        JButton btnPagar = new JButton("Registrar Pagamento");
        btnPagar.addActionListener(e -> registrarPagamento());
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnPanel.add(btnNova); btnPanel.add(btnPagar); btnPanel.add(btnExcluir);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Código", "Empresa", "Descrição", "Emissão", "Vencimento", "Valor Final", "Status"};
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
            for (Fatura f : main.faturaDao.findAll()) {
                tableModel.addRow(new Object[]{
                    f.getId(), f.getCodigo(), f.getEmpresaNome(), f.getDescricao(),
                    f.getDataEmissao() != null ? f.getDataEmissao().toLocalDate().toString() : "",
                    f.getDataVencimento() != null ? f.getDataVencimento().toString() : "",
                    String.format("R$ %.2f", f.getValorFinal()), f.getStatus()
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void abrirFormulario(Fatura fatura) {
        FaturaFormDialog d = new FaturaFormDialog(main, fatura);
        d.setVisible(true);
        if (d.isSaved()) carregarDados();
    }

    private void registrarPagamento() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma fatura."); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            Fatura f = main.faturaDao.findById(id).orElse(null);
            if (f == null) return;
            if (f.getStatus().equals("PAGA") || f.getStatus().equals("CANCELADA")) {
                JOptionPane.showMessageDialog(this, "Esta fatura não pode receber pagamento (status: " + f.getStatus() + ").");
                return;
            }
            String valorStr = JOptionPane.showInputDialog(this, "Valor do pagamento (R$):", String.format("%.2f", f.getValorFinal()));
            if (valorStr == null) return;
            double valor = Double.parseDouble(valorStr.trim().replace(",", "."));
            String[] formas = Pagamento.FORMAS;
            String forma = (String) JOptionPane.showInputDialog(this, "Forma de pagamento:", "Pagamento",
                JOptionPane.QUESTION_MESSAGE, null, formas, formas[0]);
            if (forma == null) return;

            Pagamento pg = new Pagamento();
            pg.setFaturaId(f.getId());
            pg.setEmpresaId(f.getEmpresaId());
            pg.setValor(valor);
            pg.setFormaPagamento(forma);
            pg.setStatus("CONFIRMADO");
            main.pagamentoDao.save(pg);

            // Atualizar status da fatura
            if (valor >= f.getValorFinal()) {
                f.setStatus("PAGA");
            } else {
                f.setStatus("PARCIAL");
            }
            main.faturaDao.save(f);

            JOptionPane.showMessageDialog(this, "Pagamento registrado com sucesso.");
            carregarDados();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma fatura."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir fatura?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try { main.faturaDao.delete(id); carregarDados(); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}

class FaturaFormDialog extends JDialog {
    private boolean saved = false;
    private final MainFrame main;
    private Fatura fatura;
    private JComboBox<String> cbEmpresa;
    private JTextField txtDescricao, txtValor, txtDesconto, txtVencimento, txtObs;

    public FaturaFormDialog(MainFrame main, Fatura fatura) {
        super(main, "Fatura", true);
        this.main = main; this.fatura = fatura;
        setSize(480, 380); setLocationRelativeTo(main);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbEmpresa = new JComboBox<>();
        txtDescricao = new JTextField(25); txtValor = new JTextField(25);
        txtDesconto = new JTextField(25); txtVencimento = new JTextField(25);
        txtObs = new JTextField(25);

        try {
            for (Empresa e : main.empresaDao.findAtivas())
                cbEmpresa.addItem(e.getId() + " - " + e.getRazaoSocial());
        } catch (Exception ignored) {}

        int row = 0;
        addField(form, gbc, row++, "Empresa:*", cbEmpresa);
        addField(form, gbc, row++, "Descrição:*", txtDescricao);
        addField(form, gbc, row++, "Valor (R$):*", txtValor);
        addField(form, gbc, row++, "Desconto (R$):", txtDesconto);
        addField(form, gbc, row++, "Vencimento (yyyy-MM-dd):*", txtVencimento);
        addField(form, gbc, row++, "Observações:", txtObs);

        if (fatura != null) {
            txtDescricao.setText(fatura.getDescricao());
            txtValor.setText(String.valueOf(fatura.getValor()));
            txtDesconto.setText(String.valueOf(fatura.getDesconto()));
            if (fatura.getDataVencimento() != null) txtVencimento.setText(fatura.getDataVencimento().toString());
            txtObs.setText(fatura.getObservacoes());
        } else {
            txtDesconto.setText("0");
            txtVencimento.setText(LocalDate.now().plusDays(30).toString());
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
            if (cbEmpresa.getSelectedItem() == null || txtDescricao.getText().trim().isEmpty() || txtValor.getText().trim().isEmpty() || txtVencimento.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha os campos obrigatórios."); return;
            }
            Long empresaId = Long.parseLong(((String) cbEmpresa.getSelectedItem()).split(" - ")[0]);
            Fatura f = fatura != null ? fatura : new Fatura();
            f.setEmpresaId(empresaId);
            f.setDescricao(txtDescricao.getText().trim());
            f.setValor(Double.parseDouble(txtValor.getText().trim().replace(",", ".")));
            double desc = 0;
            try { desc = Double.parseDouble(txtDesconto.getText().trim().replace(",", ".")); } catch (Exception ignored) {}
            f.setDesconto(desc);
            f.setValorFinal(f.getValor() - desc);
            f.setDataVencimento(LocalDate.parse(txtVencimento.getText().trim()));
            f.setObservacoes(txtObs.getText().trim());
            if (fatura == null) f.setCodigo("FAT-" + System.currentTimeMillis() % 10000);

            main.faturaDao.save(f);
            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}

