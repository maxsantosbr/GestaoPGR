package com.gestaopgr.ui;

import com.gestaopgr.model.Empresa;
import com.gestaopgr.model.Ppp;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class PppPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;

    public PppPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("PPP - Perfil Profissiografico Previdenciario");
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNovo = new JButton("Novo PPP");
        btnNovo.addActionListener(e -> abrirFormulario(null));
        JButton btnGerarPDF = new JButton("Gerar PPP (PDF)");
        btnGerarPDF.addActionListener(e -> gerarPdf());
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnPanel.add(btnNovo); btnPanel.add(btnGerarPDF); btnPanel.add(btnExcluir);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Empresa", "Trabalhador", "CPF", "Cargo", "Setor", "Agente Nocivo", "Data PPP"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        table.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        add(new JScrollPane(table), BorderLayout.CENTER);

        carregarDados();
    }

    private void carregarDados() {
        tableModel.setRowCount(0);
        try {
            for (Ppp p : main.pppDao.findAll()) {
                tableModel.addRow(new Object[]{
                    p.getId(), p.getEmpresaNome(), p.getTrabalhadorNome(), p.getTrabalhadorCpf(),
                    p.getCargo(), p.getSetor(), p.getAgenteNocivo(),
                    p.getDataPpp() != null ? p.getDataPpp().toString() : ""
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void abrirFormulario(Ppp ppp) {
        PppFormDialog d = new PppFormDialog(main, ppp);
        d.setVisible(true);
        if (d.isSaved()) carregarDados();
    }

    private void gerarPdf() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um PPP."); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            Ppp ppp = main.pppDao.findById(id).orElse(null);
            if (ppp == null) return;
            String path = gerarPppPdf(ppp);
            JOptionPane.showMessageDialog(this, "PPP gerado: " + path);
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(new File(path));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String gerarPppPdf(Ppp ppp) throws Exception {
        Empresa empresa = main.empresaDao.findById(ppp.getEmpresaId()).orElse(null);
        String dir = System.getProperty("user.home") + File.separator + "GestaoPGR" + File.separator + "ppp";
        new File(dir).mkdirs();
        String path = dir + File.separator + "PPP_" + ppp.getTrabalhadorNome().replace(" ", "_") + ".pdf";

        Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
        PdfWriter.getInstance(doc, new FileOutputStream(path));
        doc.open();

        com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, java.awt.Font.BOLD);
        com.lowagie.text.Font h1 = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, java.awt.Font.BOLD);
        com.lowagie.text.Font body = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.NORMAL);

        Paragraph t = new Paragraph("PERFIL PROFISSIOGRAFICO PREVIDENCIARIO (PPP)", titleFont);
        t.setAlignment(Element.ALIGN_CENTER);
        doc.add(t);
        doc.add(new Paragraph(" "));

        Paragraph emp = new Paragraph("Empresa: " + (empresa != null ? empresa.getRazaoSocial() : ""), body);
        doc.add(emp);
        Paragraph cnpj = new Paragraph("CNPJ: " + (empresa != null ? empresa.getCnpj() : ""), body);
        doc.add(cnpj);
        doc.add(new Paragraph(" "));

        String[][] dados = {
            {"Trabalhador", ppp.getTrabalhadorNome()},
            {"CPF", ppp.getTrabalhadorCpf()},
            {"NIT/PIS", ppp.getTrabalhadorNit()},
            {"Data de Nascimento", ppp.getDataNascimento() != null ? ppp.getDataNascimento().toString() : ""},
            {"Data de Admissao", ppp.getDataAdmissao() != null ? ppp.getDataAdmissao().toString() : ""},
            {"Data de Demissao", ppp.getDataDemissao() != null ? ppp.getDataDemissao().toString() : "Ainda empregado"},
            {"Cargo", ppp.getCargo()},
            {"Setor", ppp.getSetor()},
            {"Agente Nocivo", ppp.getAgenteNocivo()},
            {"Intensidade/Concentracao", ppp.getIntensidade()},
            {"Tecnica de Medicao", ppp.getTecnicaMedicao()},
            {"EPC (Eng. de Controle)", ppp.getEpc()},
            {"EPI (Equip. de Protecao)", ppp.getEpi()},
            {"Resultado da Medicao", ppp.getResultadoMedicao()},
            {"Data do PPP", ppp.getDataPpp() != null ? ppp.getDataPpp().toString() : ""}
        };

        PdfPTable tab = new PdfPTable(2);
        tab.setWidthPercentage(100);
        tab.setWidths(new float[]{2, 3});
        for (String[] row : dados) {
            PdfPCell c1 = new PdfPCell(new Phrase(row[0], h1));
            c1.setPadding(4); c1.setBackgroundColor(new Color(240, 240, 245));
            tab.addCell(c1);
            PdfPCell c2 = new PdfPCell(new Phrase(row[1], body));
            c2.setPadding(4);
            tab.addCell(c2);
        }
        doc.add(tab);

        if (ppp.getObservacoes() != null && !ppp.getObservacoes().isEmpty()) {
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Observacoes: " + ppp.getObservacoes(), body));
        }

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("_______________________________________", body));
        doc.add(new Paragraph("Responsavel Tecnico", body));

        doc.close();
        ppp.setPdfPath(path);
        main.pppDao.save(ppp);
        return path;
    }

    private void excluirSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um PPP."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir PPP?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try { main.pppDao.delete(id); carregarDados(); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}

class PppFormDialog extends JDialog {
    private boolean saved = false;
    private final MainFrame main;
    private Ppp ppp;
    private JComboBox<String> cbEmpresa;
    private JTextField txtNome, txtCpf, txtNit, txtCargo, txtSetor, txtAgente, txtIntensidade, txtTecnica, txtEpc, txtEpi, txtResultado, txtDataNasc, txtDataAdm, txtDataDem, txtDataPpp, txtObs;

    public PppFormDialog(MainFrame main, Ppp ppp) {
        super(main, "PPP", true);
        this.main = main; this.ppp = ppp;
        setSize(520, 600); setLocationRelativeTo(main);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbEmpresa = new JComboBox<>();
        txtNome = new JTextField(25); txtCpf = new JTextField(25); txtNit = new JTextField(25);
        txtCargo = new JTextField(25); txtSetor = new JTextField(25); txtAgente = new JTextField(25);
        txtIntensidade = new JTextField(25); txtTecnica = new JTextField(25); txtEpc = new JTextField(25);
        txtEpi = new JTextField(25); txtResultado = new JTextField(25);
        txtDataNasc = new JTextField(25); txtDataAdm = new JTextField(25); txtDataDem = new JTextField(25);
        txtDataPpp = new JTextField(25); txtObs = new JTextField(25);

        try {
            for (Empresa e : main.empresaDao.findAtivas())
                cbEmpresa.addItem(e.getId() + " - " + e.getRazaoSocial());
        } catch (Exception ignored) {}

        int row = 0;
        addField(form, gbc, row++, "Empresa:*", cbEmpresa);
        addField(form, gbc, row++, "Trabalhador:*", txtNome);
        addField(form, gbc, row++, "CPF:", txtCpf);
        addField(form, gbc, row++, "NIT/PIS:", txtNit);
        addField(form, gbc, row++, "Data Nascimento:", txtDataNasc);
        addField(form, gbc, row++, "Data Admissao:", txtDataAdm);
        addField(form, gbc, row++, "Data Demissao:", txtDataDem);
        addField(form, gbc, row++, "Cargo:", txtCargo);
        addField(form, gbc, row++, "Setor:", txtSetor);
        addField(form, gbc, row++, "Agente Nocivo:", txtAgente);
        addField(form, gbc, row++, "Intensidade:", txtIntensidade);
        addField(form, gbc, row++, "Tecnica Medicao:", txtTecnica);
        addField(form, gbc, row++, "EPC:", txtEpc);
        addField(form, gbc, row++, "EPI:", txtEpi);
        addField(form, gbc, row++, "Resultado Medicao:", txtResultado);
        addField(form, gbc, row++, "Data PPP:", txtDataPpp);
        addField(form, gbc, row++, "Observacoes:", txtObs);

        if (ppp != null) {
            txtNome.setText(ppp.getTrabalhadorNome());
            txtCpf.setText(ppp.getTrabalhadorCpf());
            txtNit.setText(ppp.getTrabalhadorNit());
            if (ppp.getDataNascimento() != null) txtDataNasc.setText(ppp.getDataNascimento().toString());
            if (ppp.getDataAdmissao() != null) txtDataAdm.setText(ppp.getDataAdmissao().toString());
            if (ppp.getDataDemissao() != null) txtDataDem.setText(ppp.getDataDemissao().toString());
            txtCargo.setText(ppp.getCargo());
            txtSetor.setText(ppp.getSetor());
            txtAgente.setText(ppp.getAgenteNocivo());
            txtIntensidade.setText(ppp.getIntensidade());
            txtTecnica.setText(ppp.getTecnicaMedicao());
            txtEpc.setText(ppp.getEpc());
            txtEpi.setText(ppp.getEpi());
            txtResultado.setText(ppp.getResultadoMedicao());
            if (ppp.getDataPpp() != null) txtDataPpp.setText(ppp.getDataPpp().toString());
            txtObs.setText(ppp.getObservacoes());
        } else {
            txtDataPpp.setText(LocalDate.now().toString());
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
            if (cbEmpresa.getSelectedItem() == null || txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Empresa e Trabalhador sao obrigatorios."); return;
            }
            Long empresaId = Long.parseLong(((String) cbEmpresa.getSelectedItem()).split(" - ")[0]);
            Ppp p = ppp != null ? ppp : new Ppp();
            p.setEmpresaId(empresaId);
            p.setTrabalhadorNome(txtNome.getText().trim());
            p.setTrabalhadorCpf(txtCpf.getText().trim());
            p.setTrabalhadorNit(txtNit.getText().trim());
            if (!txtDataNasc.getText().trim().isEmpty()) p.setDataNascimento(LocalDate.parse(txtDataNasc.getText().trim()));
            if (!txtDataAdm.getText().trim().isEmpty()) p.setDataAdmissao(LocalDate.parse(txtDataAdm.getText().trim()));
            if (!txtDataDem.getText().trim().isEmpty()) p.setDataDemissao(LocalDate.parse(txtDataDem.getText().trim()));
            p.setCargo(txtCargo.getText().trim());
            p.setSetor(txtSetor.getText().trim());
            p.setAgenteNocivo(txtAgente.getText().trim());
            p.setIntensidade(txtIntensidade.getText().trim());
            p.setTecnicaMedicao(txtTecnica.getText().trim());
            p.setEpc(txtEpc.getText().trim());
            p.setEpi(txtEpi.getText().trim());
            p.setResultadoMedicao(txtResultado.getText().trim());
            if (!txtDataPpp.getText().trim().isEmpty()) p.setDataPpp(LocalDate.parse(txtDataPpp.getText().trim()));
            p.setObservacoes(txtObs.getText().trim());

            main.pppDao.save(p);
            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}


