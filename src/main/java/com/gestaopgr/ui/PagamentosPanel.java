package com.gestaopgr.ui;

import com.gestaopgr.model.Pagamento;
import com.gestaopgr.model.Fatura;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PagamentosPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbFiltroEmpresa;

    public PagamentosPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Pagamentos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNovo = new JButton("Registrar Pagamento");
        btnNovo.addActionListener(e -> registrarPagamento());
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnPanel.add(btnNovo); btnPanel.add(btnExcluir);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Filtro
        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtroPanel.add(new JLabel("Empresa:"));
        cbFiltroEmpresa = new JComboBox<>();
        cbFiltroEmpresa.addItem("Todas");
        try {
            for (com.gestaopgr.model.Empresa e : main.empresaDao.findAtivas())
                cbFiltroEmpresa.addItem(e.getId() + " - " + e.getRazaoSocial());
        } catch (Exception ignored) {}
        cbFiltroEmpresa.addActionListener(e -> carregarDados());
        filtroPanel.add(cbFiltroEmpresa);

        // Total recebido
        JLabel lblTotal = new JLabel("Total recebido: R$ 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotal.setForeground(new Color(34, 197, 94));
        filtroPanel.add(Box.createHorizontalStrut(30));
        filtroPanel.add(lblTotal);

        add(filtroPanel, BorderLayout.CENTER);

        String[] cols = {"ID", "Empresa", "Fatura", "Data Pagamento", "Valor", "Forma", "Status"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 400));
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scroll, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.SOUTH);

        carregarDados();
    }

    private void carregarDados() {
        tableModel.setRowCount(0);
        double totalRecebido = 0;
        try {
            String filtroEmp = (String) cbFiltroEmpresa.getSelectedItem();
            List<Pagamento> pagamentos;
            if (filtroEmp != null && !filtroEmp.equals("Todas")) {
                Long empresaId = Long.parseLong(filtroEmp.split(" - ")[0]);
                pagamentos = main.pagamentoDao.findByEmpresa(empresaId);
            } else {
                pagamentos = main.pagamentoDao.findAll();
            }

            for (Pagamento p : pagamentos) {
                tableModel.addRow(new Object[]{
                    p.getId(), "", "Fatura #" + p.getFaturaId(),
                    p.getDataPagamento() != null ? p.getDataPagamento().toString() : "",
                    String.format("R$ %.2f", p.getValor()), p.getFormaPagamento(), p.getStatus()
                });
                if ("CONFIRMADO".equals(p.getStatus())) totalRecebido += p.getValor();
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void registrarPagamento() {
        try {
            // Selecionar fatura pendente
            List<Fatura> faturas = main.faturaDao.findPendentes();
            if (faturas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nao ha faturas pendentes.");
                return;
            }

            JComboBox<String> cbFatura = new JComboBox<>();
            for (Fatura f : faturas) {
                cbFatura.addItem(f.getId() + " - " + f.getEmpresaNome() + " - " + f.getDescricao() + " (R$ " + String.format("%.2f", f.getValorFinal()) + ")");
            }

            JTextField txtValor = new JTextField(20);
            JComboBox<String> cbForma = new JComboBox<>(Pagamento.FORMAS);
            JTextField txtData = new JTextField(LocalDate.now().toString());
            JTextField txtObs = new JTextField(20);

            JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
            form.add(new JLabel("Fatura:*")); form.add(cbFatura);
            form.add(new JLabel("Valor (R$):*")); form.add(txtValor);
            form.add(new JLabel("Forma Pagamento:*")); form.add(cbForma);
            form.add(new JLabel("Data (yyyy-MM-dd):")); form.add(txtData);
            form.add(new JLabel("Observacao:")); form.add(txtObs);

            // Auto-fill valor when fatura selected
            cbFatura.addActionListener(e -> {
                String sel = (String) cbFatura.getSelectedItem();
                if (sel != null) {
                    Long fid = Long.parseLong(sel.split(" - ")[0]);
                    for (Fatura f : faturas) {
                        if (f.getId().equals(fid)) {
                            txtValor.setText(String.format("%.2f", f.getValorFinal()).replace(",", "."));
                        }
                    }
                }
            });

            int res = JOptionPane.showConfirmDialog(this, form, "Registrar Pagamento", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            Long faturaId = Long.parseLong(((String) cbFatura.getSelectedItem()).split(" - ")[0]);
            Fatura fatura = main.faturaDao.findById(faturaId).orElse(null);
            if (fatura == null) return;

            double valor = Double.parseDouble(txtValor.getText().trim().replace(",", "."));
            Pagamento pg = new Pagamento();
            pg.setFaturaId(faturaId);
            pg.setEmpresaId(fatura.getEmpresaId());
            pg.setValor(valor);
            pg.setFormaPagamento((String) cbForma.getSelectedItem());
            pg.setDataPagamento(LocalDate.parse(txtData.getText().trim()));
            pg.setObservacao(txtObs.getText().trim());
            pg.setStatus("CONFIRMADO");
            main.pagamentoDao.save(pg);

            // Atualizar status da fatura
            if (valor >= fatura.getValorFinal()) {
                fatura.setStatus("PAGA");
            } else {
                fatura.setStatus("PARCIAL");
            }
            main.faturaDao.save(fatura);

            JOptionPane.showMessageDialog(this, "Pagamento registrado com sucesso.");
            carregarDados();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um pagamento."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir pagamento?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try { main.pagamentoDao.delete(id); carregarDados(); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}
