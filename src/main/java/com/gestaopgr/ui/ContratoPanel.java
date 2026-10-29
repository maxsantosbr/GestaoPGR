package com.gestaopgr.ui;

import com.gestaopgr.model.Contrato;
import com.gestaopgr.model.Empresa;
import com.gestaopgr.model.Proposta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ContratoPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;

    public ContratoPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Contratos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNovo = new JButton("Novo Contrato");
        btnNovo.addActionListener(e -> abrirFormulario(null));
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarSelecionado());
        btnPanel.add(btnNovo); btnPanel.add(btnEditar);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Código", "Empresa", "Tipo", "Título", "Valor Total", "Status", "Início", "Fim"};
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
            for (Contrato c : main.contratoDao.findAll()) {
                tableModel.addRow(new Object[]{
                    c.getId(), c.getCodigo(), c.getEmpresaNome(), c.getTipo(), c.getTitulo(),
                    String.format("R$ %.2f", c.getValorTotal()), c.getStatus(),
                    c.getDataInicio() != null ? c.getDataInicio().toString() : "",
                    c.getDataFim() != null ? c.getDataFim().toString() : ""
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void abrirFormulario(Contrato contrato) {
        ContratoFormDialog d = new ContratoFormDialog(main, contrato);
        d.setVisible(true);
        if (d.isSaved()) carregarDados();
    }

    private void editarSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um contrato."); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            Contrato c = main.contratoDao.findById(id).orElse(null);
            if (c != null) abrirFormulario(c);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}

class ContratoFormDialog extends JDialog {
    private boolean saved = false;
    private final MainFrame main;
    private Contrato contrato;
    private JComboBox<String> cbEmpresa, cbTipo, cbStatus, cbPeriodicidade;
    private JTextField txtTitulo, txtDescricao, txtValorTotal, txtValorRecorrente, txtDiaVenc, txtInicio, txtFim, txtObs;

    public ContratoFormDialog(MainFrame main, Contrato contrato) {
        super(main, "Contrato", true);
        this.main = main; this.contrato = contrato;
        setSize(520, 520); setLocationRelativeTo(main);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbEmpresa = new JComboBox<>(); cbTipo = new JComboBox<>(Contrato.TIPOS);
        cbStatus = new JComboBox<>(Contrato.STATUS); cbPeriodicidade = new JComboBox<>(Contrato.PERIODICIDADES);
        txtTitulo = new JTextField(25); txtDescricao = new JTextField(25);
        txtValorTotal = new JTextField(25); txtValorRecorrente = new JTextField(25);
        txtDiaVenc = new JTextField(25); txtInicio = new JTextField(25);
        txtFim = new JTextField(25); txtObs = new JTextField(25);

        try {
            for (Empresa e : main.empresaDao.findAtivas())
                cbEmpresa.addItem(e.getId() + " - " + e.getRazaoSocial());
        } catch (Exception ignored) {}

        int row = 0;
        addField(form, gbc, row++, "Empresa:*", cbEmpresa);
        addField(form, gbc, row++, "Tipo:", cbTipo);
        addField(form, gbc, row++, "Status:", cbStatus);
        addField(form, gbc, row++, "Título:*", txtTitulo);
        addField(form, gbc, row++, "Descrição:", txtDescricao);
        addField(form, gbc, row++, "Valor Total (R$):*", txtValorTotal);
        addField(form, gbc, row++, "Valor Recorrente (R$):", txtValorRecorrente);
        addField(form, gbc, row++, "Periodicidade:", cbPeriodicidade);
        addField(form, gbc, row++, "Dia Vencimento:", txtDiaVenc);
        addField(form, gbc, row++, "Data Início:*", txtInicio);
        addField(form, gbc, row++, "Data Fim:", txtFim);
        addField(form, gbc, row++, "Observações:", txtObs);

        if (contrato != null) {
            txtTitulo.setText(contrato.getTitulo());
            txtDescricao.setText(contrato.getDescricao());
            cbTipo.setSelectedItem(contrato.getTipo());
            cbStatus.setSelectedItem(contrato.getStatus());
            txtValorTotal.setText(String.valueOf(contrato.getValorTotal()));
            txtValorRecorrente.setText(String.valueOf(contrato.getValorRecorrente()));
            if (contrato.getPeriodicidade() != null) cbPeriodicidade.setSelectedItem(contrato.getPeriodicidade());
            if (contrato.getDiaVencimento() != null) txtDiaVenc.setText(String.valueOf(contrato.getDiaVencimento()));
            if (contrato.getDataInicio() != null) txtInicio.setText(contrato.getDataInicio().toString());
            if (contrato.getDataFim() != null) txtFim.setText(contrato.getDataFim().toString());
            txtObs.setText(contrato.getObservacoes());
        } else {
            txtInicio.setText(LocalDate.now().toString());
            txtValorTotal.setText("0");
            txtValorRecorrente.setText("0");
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
            if (cbEmpresa.getSelectedItem() == null || txtTitulo.getText().trim().isEmpty() || txtValorTotal.getText().trim().isEmpty() || txtInicio.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha os campos obrigatórios."); return;
            }
            Long empresaId = Long.parseLong(((String) cbEmpresa.getSelectedItem()).split(" - ")[0]);
            Contrato c = contrato != null ? contrato : new Contrato();
            c.setEmpresaId(empresaId);
            c.setTipo((String) cbTipo.getSelectedItem());
            c.setStatus((String) cbStatus.getSelectedItem());
            c.setTitulo(txtTitulo.getText().trim());
            c.setDescricao(txtDescricao.getText().trim());
            c.setValorTotal(Double.parseDouble(txtValorTotal.getText().trim().replace(",", ".")));
            try { c.setValorRecorrente(Double.parseDouble(txtValorRecorrente.getText().trim().replace(",", "."))); } catch (Exception ignored) {}
            c.setPeriodicidade((String) cbPeriodicidade.getSelectedItem());
            if (!txtDiaVenc.getText().trim().isEmpty()) c.setDiaVencimento(Integer.parseInt(txtDiaVenc.getText().trim()));
            c.setDataInicio(LocalDate.parse(txtInicio.getText().trim()));
            if (!txtFim.getText().trim().isEmpty()) c.setDataFim(LocalDate.parse(txtFim.getText().trim()));
            c.setObservacoes(txtObs.getText().trim());
            if (contrato == null) c.setCodigo("CONT-" + System.currentTimeMillis() % 10000);

            main.contratoDao.save(c);
            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}

