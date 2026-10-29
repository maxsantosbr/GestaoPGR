package com.gestaopgr.ui;

import com.gestaopgr.model.Empresa;
import com.gestaopgr.model.Perigo;
import com.gestaopgr.model.Risco;
import com.gestaopgr.model.Setor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RiscoPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbFiltroEmpresa, cbFiltroClassificacao;

    public RiscoPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Inventário de Riscos Ocupacionais");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNovo = new JButton("Novo Risco");
        btnNovo.addActionListener(e -> abrirFormulario(null));
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarSelecionado());
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnPanel.add(btnNovo); btnPanel.add(btnEditar); btnPanel.add(btnExcluir);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Filtros
        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtroPanel.add(new JLabel("Empresa:"));
        cbFiltroEmpresa = new JComboBox<>();
        cbFiltroEmpresa.addItem("Todas");
        try {
            for (Empresa e : main.empresaDao.findAtivas())
                cbFiltroEmpresa.addItem(e.getId() + " - " + e.getRazaoSocial());
        } catch (Exception ignored) {}
        cbFiltroEmpresa.addActionListener(e -> carregarDados());
        filtroPanel.add(cbFiltroEmpresa);

        filtroPanel.add(new JLabel("Classificação:"));
        cbFiltroClassificacao = new JComboBox<>(new String[]{"Todas", "BAIXO", "MEDIO", "ALTO", "CRITICO"});
        cbFiltroClassificacao.addActionListener(e -> carregarDados());
        filtroPanel.add(cbFiltroClassificacao);

        add(filtroPanel, BorderLayout.CENTER);

        // Tabela
        String[] cols = {"ID", "Empresa", "Setor", "Perigo/Risco", "Categoria", "Prob.", "Sev.", "Nível", "Classificação", "Status"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Colorir coluna de classificação
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (v != null) {
                    String cls = v.toString();
                    switch (cls) {
                        case "BAIXO": comp.setBackground(new Color(220, 252, 231)); break;
                        case "MEDIO": comp.setBackground(new Color(254, 243, 199)); break;
                        case "ALTO": comp.setBackground(new Color(254, 226, 226)); break;
                        case "CRITICO": comp.setBackground(new Color(252, 165, 165)); break;
                        default: comp.setBackground(Color.WHITE);
                    }
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scroll, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.SOUTH);

        // Ajustar tamanho
        setLayout(new BorderLayout(10, 10));
        // Re-add components in correct order
        removeAll();
        add(topPanel, BorderLayout.NORTH);
        add(filtroPanel, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);

        // Set preferred size for scroll
        scroll.setPreferredSize(new Dimension(0, 400));

        carregarDados();
    }

    private void carregarDados() {
        tableModel.setRowCount(0);
        try {
            String filtroEmp = (String) cbFiltroEmpresa.getSelectedItem();
            String filtroCls = (String) cbFiltroClassificacao.getSelectedItem();

            List<Risco> riscos;
            if (filtroEmp != null && !filtroEmp.equals("Todas")) {
                Long empresaId = Long.parseLong(filtroEmp.split(" - ")[0]);
                if (filtroCls != null && !filtroCls.equals("Todas")) {
                    riscos = main.riscoDao.findByClassificacao(empresaId, filtroCls);
                } else {
                    riscos = main.riscoDao.findByEmpresa(empresaId);
                }
            } else {
                riscos = main.riscoDao.findAll();
            }

            for (Risco r : riscos) {
                tableModel.addRow(new Object[]{
                    r.getId(), r.getEmpresaNome(), r.getSetorNome(), r.getPerigoDescricao(),
                    r.getCategoria(), r.getProbabilidade(), r.getSeveridade(),
                    r.getNivelRisco(), r.getClassificacao(), r.getStatus()
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void abrirFormulario(Risco risco) {
        RiscoFormDialog d = new RiscoFormDialog(main, risco);
        d.setVisible(true);
        if (d.isSaved()) carregarDados();
    }

    private void editarSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um risco."); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            Risco r = main.riscoDao.findById(id).orElse(null);
            if (r != null) abrirFormulario(r);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void excluirSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um risco."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir risco?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try { main.riscoDao.delete(id); carregarDados(); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}

class RiscoFormDialog extends JDialog {
    private boolean saved = false;
    private final MainFrame main;
    private Risco risco;
    private JComboBox<String> cbEmpresa, cbSetor, cbPerigo, cbCategoria, cbStatus;
    private JTextField txtPerigoDesc, txtFonte, txtAtividade, txtNumTrab, txtMedidasExistentes, txtMedidasPropostas, txtEpi;
    private JSpinner spProb, spSev;
    private JLabel lblNivel, lblClassificacao;

    public RiscoFormDialog(MainFrame main, Risco risco) {
        super(main, "Risco Ocupacional", true);
        this.main = main; this.risco = risco;
        setSize(650, 620); setLocationRelativeTo(main);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbEmpresa = new JComboBox<>(); cbSetor = new JComboBox<>();
        cbPerigo = new JComboBox<>(); cbCategoria = new JComboBox<>(new String[]{"FISICO", "QUIMICO", "BIOLOGICO", "ERGONOMICO", "ACIDENTE", "PSICOSSOCIAL"});
        cbStatus = new JComboBox<>(Risco.STATUS);
        txtPerigoDesc = new JTextField(30); txtFonte = new JTextField(30);
        txtAtividade = new JTextField(30); txtNumTrab = new JTextField(30);
        txtMedidasExistentes = new JTextField(30); txtMedidasPropostas = new JTextField(30);
        txtEpi = new JTextField(30);
        spProb = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        spSev = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        lblNivel = new JLabel("1"); lblClassificacao = new JLabel("BAIXO");

        try {
            for (Empresa e : main.empresaDao.findAtivas())
                cbEmpresa.addItem(e.getId() + " - " + e.getRazaoSocial());
            for (Perigo p : main.perigoDao.findAll())
                cbPerigo.addItem(p.getId() + " - " + p.getNome() + " (" + p.getCategoria() + ")");
        } catch (Exception ignored) {}

        cbEmpresa.addActionListener(e -> carregarSetores());
        spProb.addChangeListener(e -> atualizarNivel());
        spSev.addChangeListener(e -> atualizarNivel());

        int row = 0;
        addField(form, gbc, row++, "Empresa:*", cbEmpresa);
        addField(form, gbc, row++, "Setor:", cbSetor);
        addField(form, gbc, row++, "Perigo (biblioteca):", cbPerigo);
        addField(form, gbc, row++, "Categoria:*", cbCategoria);
        addField(form, gbc, row++, "Descrição do Risco:*", txtPerigoDesc);
        addField(form, gbc, row++, "Fonte:", txtFonte);
        addField(form, gbc, row++, "Atividade:", txtAtividade);
        addField(form, gbc, row++, "Nº Trabalhadores Expostos:", txtNumTrab);
        addField(form, gbc, row++, "Probabilidade (1-5):", spProb);
        addField(form, gbc, row++, "Severidade (1-5):", spSev);

        JPanel nivelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nivelPanel.add(new JLabel("Nível de Risco:"));
        lblNivel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nivelPanel.add(lblNivel);
        nivelPanel.add(new JLabel("  Classificação:"));
        lblClassificacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nivelPanel.add(lblClassificacao);
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        form.add(nivelPanel, gbc);
        gbc.gridwidth = 1;

        addField(form, gbc, row++, "Medidas Existentes:", txtMedidasExistentes);
        addField(form, gbc, row++, "Medidas Propostas:", txtMedidasPropostas);
        addField(form, gbc, row++, "EPI Recomendado:", txtEpi);
        addField(form, gbc, row++, "Status:", cbStatus);

        if (risco != null) {
            txtPerigoDesc.setText(risco.getPerigoDescricao());
            cbCategoria.setSelectedItem(risco.getCategoria());
            txtFonte.setText(risco.getFonte());
            txtAtividade.setText(risco.getAtividade());
            txtNumTrab.setText(String.valueOf(risco.getNumTrabalhadoresExpostos()));
            spProb.setValue(risco.getProbabilidade());
            spSev.setValue(risco.getSeveridade());
            txtMedidasExistentes.setText(risco.getMedidasExistentes());
            txtMedidasPropostas.setText(risco.getMedidasPropostas());
            txtEpi.setText(risco.getEpiRecomendado());
            cbStatus.setSelectedItem(risco.getStatus());
            atualizarNivel();
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

    private void carregarSetores() {
        cbSetor.removeAllItems();
        try {
            String sel = (String) cbEmpresa.getSelectedItem();
            if (sel != null) {
                Long empresaId = Long.parseLong(sel.split(" - ")[0]);
                for (Setor s : main.setorDao.findByEmpresa(empresaId))
                    cbSetor.addItem(s.getId() + " - " + s.getNome());
            }
        } catch (Exception ignored) {}
    }

    private void atualizarNivel() {
        int prob = (int) spProb.getValue();
        int sev = (int) spSev.getValue();
        int nivel = prob * sev;
        lblNivel.setText(String.valueOf(nivel));
        String cls;
        if (nivel <= 4) cls = "BAIXO";
        else if (nivel <= 9) cls = "MEDIO";
        else if (nivel <= 16) cls = "ALTO";
        else cls = "CRITICO";
        lblClassificacao.setText(cls);
        switch (cls) {
            case "BAIXO": lblClassificacao.setForeground(new Color(34, 197, 94)); break;
            case "MEDIO": lblClassificacao.setForeground(new Color(249, 115, 22)); break;
            case "ALTO": lblClassificacao.setForeground(new Color(239, 68, 68)); break;
            case "CRITICO": lblClassificacao.setForeground(new Color(127, 29, 29)); break;
        }
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(field, gbc);
    }

    private void salvar() {
        try {
            if (cbEmpresa.getSelectedItem() == null || txtPerigoDesc.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Empresa e Descrição do Risco são obrigatórios."); return;
            }
            Long empresaId = Long.parseLong(((String) cbEmpresa.getSelectedItem()).split(" - ")[0]);
            Long setorId = null;
            if (cbSetor.getSelectedItem() != null)
                setorId = Long.parseLong(((String) cbSetor.getSelectedItem()).split(" - ")[0]);
            Long perigoId = null;
            if (cbPerigo.getSelectedItem() != null)
                perigoId = Long.parseLong(((String) cbPerigo.getSelectedItem()).split(" - ")[0]);

            Risco r = risco != null ? risco : new Risco();
            r.setEmpresaId(empresaId);
            r.setSetorId(setorId);
            r.setPerigoId(perigoId);
            r.setPerigoDescricao(txtPerigoDesc.getText().trim());
            r.setCategoria((String) cbCategoria.getSelectedItem());
            r.setFonte(txtFonte.getText().trim());
            r.setAtividade(txtAtividade.getText().trim());
            if (!txtNumTrab.getText().trim().isEmpty())
                r.setNumTrabalhadoresExpostos(Integer.parseInt(txtNumTrab.getText().trim()));
            r.setProbabilidade((int) spProb.getValue());
            r.setSeveridade((int) spSev.getValue());
            r.setMedidasExistentes(txtMedidasExistentes.getText().trim());
            r.setMedidasPropostas(txtMedidasPropostas.getText().trim());
            r.setEpiRecomendado(txtEpi.getText().trim());
            r.setStatus((String) cbStatus.getSelectedItem());

            main.riscoDao.save(r);
            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}

