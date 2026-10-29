package com.gestaopgr.ui;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

public class VendasPanel extends JPanel {
    private final MainFrame main;

    public VendasPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Gerador de Proposta de Vendas (PDF)");
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
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

        JLabel icon = new JLabel("💼");
        icon.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(icon);
        card.add(Box.createVerticalStrut(15));

        JLabel lblTitle = new JLabel("Proposta de Vendas - Serviços de PGR");
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(8));

        JLabel lblDesc = new JLabel("<html><div style='text-align:center; width:450px'>"
            + "Gere um PDF profissional e irresistível para apresentar seus serviços de "
            + "elaboração de PGR a empresas. Inclui capa, benefícios, preços, cronograma "
            + "e chamada para ação.</div></html>");
        lblDesc.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        lblDesc.setForeground(new Color(100, 116, 139));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(20));

        // Campos personalizáveis
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtEmpresa = new JTextField(25);
        JTextField txtContato = new JTextField(25);
        JTextField txtValor = new JTextField("1.500,00");
        JComboBox<String> cbPorte = new JComboBox<>(new String[]{"Pequena (até 20 empregados)", "Média (21-100)", "Grande (acima de 100)"});

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        form.add(new JLabel("Empresa Prospecto:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(txtEmpresa, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        form.add(new JLabel("Contato:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(txtContato, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        form.add(new JLabel("Porte da Empresa:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(cbPorte, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        form.add(new JLabel("Valor Proposto (R$):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(txtValor, gbc); row++;

        form.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(form);
        card.add(Box.createVerticalStrut(20));

        JButton btnGerar = new JButton("Gerar Proposta de Vendas (PDF)");
        btnGerar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        btnGerar.setForeground(Color.WHITE);
        btnGerar.setBackground(new Color(34, 197, 94));
        btnGerar.setFocusPainted(false);
        btnGerar.setBorderPainted(false);
        btnGerar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGerar.setPreferredSize(new Dimension(260, 40));
        btnGerar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGerar.addActionListener(e -> gerarPropostaVendas(txtEmpresa.getText(), txtContato.getText(), (String) cbPorte.getSelectedItem(), txtValor.getText()));
        card.add(btnGerar);

        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private void gerarPropostaVendas(String empresa, String contato, String porte, String valor) {
        try {
            String dir = System.getProperty("user.home") + File.separator + "GestaoPGR" + File.separator + "vendas";
            new File(dir).mkdirs();
            String path = dir + File.separator + "Proposta_Vendas_PGR.pdf";

            Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(path));

            writer.setPageEvent(new PdfPageEventHelper() {
                public void onEndPage(PdfWriter w, Document d) {
                    try {
                        PdfContentByte cb = w.getDirectContent();
                        cb.beginText();
                        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);
                        cb.setFontAndSize(bf, 8);
                        cb.setTextMatrix(50, 25);
                        cb.showText("Proposta de Servicos - Gestao PGR  |  Pagina " + w.getPageNumber());
                        cb.endText();
                    } catch (Exception ignored) {}
                }
            });

            doc.open();

            // Cores
            Color azul = new Color(59, 130, 246);
            Color verde = new Color(34, 197, 94);
            Color cinza = new Color(100, 116, 139);
            Color escuro = new Color(30, 41, 59);

            // ===== CAPA =====
            Paragraph p = new Paragraph(" ", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 40, java.awt.Font.BOLD));
            p.setAlignment(Element.ALIGN_CENTER);
            doc.add(p);

            Paragraph capaTitle = new Paragraph("PROPOSTA DE SERVICOS", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 26, java.awt.Font.BOLD));
            capaTitle.setAlignment(Element.ALIGN_CENTER);
            capaTitle.setSpacingAfter(5);
            doc.add(capaTitle);

            Paragraph capaSub = new Paragraph("Elaboracao de PGR - Programa de Gerenciamento de Riscos", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.NORMAL));
            capaSub.setAlignment(Element.ALIGN_CENTER);
            capaSub.setSpacingAfter(30);
            doc.add(capaSub);

            // Linha
            PdfPTable linha = new PdfPTable(1);
            linha.setWidthPercentage(60);
            PdfPCell cell = new PdfPCell();
            cell.setBorderWidthBottom(2);
            cell.setBorderColor(azul);
            cell.setFixedHeight(2);
            linha.addCell(cell);
            doc.add(linha);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            Paragraph empresaP = new Paragraph("Preparado para: " + (empresa.isEmpty() ? "Empresa Prospecto" : empresa), new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, java.awt.Font.BOLD));
            empresaP.setAlignment(Element.ALIGN_CENTER);
            doc.add(empresaP);

            Paragraph contatoP = new Paragraph("Contato: " + (contato.isEmpty() ? "Responsavel" : contato), new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.NORMAL));
            contatoP.setAlignment(Element.ALIGN_CENTER);
            doc.add(contatoP);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            Paragraph valorP = new Paragraph("Investimento: R$ " + valor, new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, java.awt.Font.BOLD));
            valorP.setAlignment(Element.ALIGN_CENTER);
            valorP.setSpacingBefore(10);
            doc.add(valorP);

            Paragraph porteP = new Paragraph("Porte: " + porte, new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.ITALIC));
            porteP.setAlignment(Element.ALIGN_CENTER);
            doc.add(porteP);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            Paragraph validade = new Paragraph("Proposta valida por 30 dias", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.ITALIC));
            validade.setAlignment(Element.ALIGN_CENTER);
            doc.add(validade);

            Paragraph data = new Paragraph("Data: " + java.time.LocalDate.now().toString(), new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.NORMAL));
            data.setAlignment(Element.ALIGN_CENTER);
            doc.add(data);

            doc.newPage();

            // ===== POR QUE SUA EMPRESA PRECISA DE UM PGR =====
            Paragraph h1 = new Paragraph("Por que sua empresa precisa de um PGR?", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, java.awt.Font.BOLD));
            h1.setSpacingAfter(10);
            doc.add(h1);

            String[] beneficios = {
                "OBRIGACAO LEGAL: A NR-01 exige que toda empresa com empregados tenha um PGR (Programa de Gerenciamento de Riscos). Sem ele, sua empresa esta sujeita a multas de R$ 1.000 a R$ 50.000 por irregularidade.",
                "MULTAS EVITADAS: Empresas sem PGR ou com PGR desatualizado podem ser multadas em fiscalizacoes de rotina. O investimento no PGR se paga na primeira fiscalizacao evitada.",
                "PROTECAO JURIDICA: O PGR e a base legal para defesa em processos trabalhistas e de aposentadoria especial. Sem ele, a empresa fica desprotegida.",
                "SAUDE DOS TRABALHADORES: Identificar e controlar riscos previne acidentes, doencas ocupacionais e afastamentos. Menos acidentes = menos custos com CAT, INSS e substituicoes.",
                "IMAGEM E REPUTACAO: Empresas com gestao de riscos adequada tem melhor imagem perante clientes, fornecedores e orgaos fiscalizadores.",
                "NOVIDADE 2026 - RISCOS PSICOSSOCIAIS: A partir de maio de 2026, a NR-01 exige a inclusao de riscos psicossociais (estresse, assedio, burnout) no PGR. Empresas que nao se adequarem serao autuadas."
            };

            for (String b : beneficios) {
                Paragraph bp = new Paragraph("• " + b, new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.NORMAL));
                bp.setSpacingAfter(8);
                bp.setAlignment(Element.ALIGN_JUSTIFIED);
                doc.add(bp);
            }

            doc.add(new Paragraph(" "));

            // ===== O QUE ESTA INCLUIDO =====
            Paragraph h2 = new Paragraph("O que esta incluido no servico", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, java.awt.Font.BOLD));
            h2.setSpacingAfter(10);
            doc.add(h2);

            String[] itens = {
                "Visita tecnica ao local de trabalho",
                "Levantamento de todos os riscos ocupacionais (fisicos, quimicos, biologicos, ergonomicos, de acidente e psicossociais)",
                "Inventario de riscos com matriz de risco (probabilidade x severidade)",
                "Plano de acao com medidas de controle, responsaveis e prazos",
                "Documento PGR completo em PDF (pronto para fiscalizacao)",
                "Relatorio de conformidade com a NR-01",
                "Orientacao sobre medidas de controle e EPIs",
                "Suporte por 30 dias apos a entrega"
            };

            for (String item : itens) {
                Paragraph ip = new Paragraph("✓ " + item, new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.NORMAL));
                ip.setSpacingAfter(4);
                doc.add(ip);
            }

            doc.add(new Paragraph(" "));

            // ===== TABELA DE PRECOS =====
            Paragraph h3 = new Paragraph("Tabela de Precos", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, java.awt.Font.BOLD));
            h3.setSpacingAfter(10);
            doc.add(h3);

            PdfPTable tabela = new PdfPTable(3);
            tabela.setWidthPercentage(100);
            tabela.setWidths(new float[]{3, 1, 1.5f});

            // Header
            String[] headers = {"Servico", "Porte", "Valor"};
            for (String h : headers) {
                PdfPCell hc = new PdfPCell(new Phrase(h, new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, java.awt.Font.BOLD)));
                hc.setBackgroundColor(azul);
                hc.setPadding(8);
                hc.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabela.addCell(hc);
            }

            String[][] precos = {
                {"Elaboracao de PGR", "Pequena (ate 20 emp.)", "R$ 800,00"},
                {"Elaboracao de PGR", "Media (21-100 emp.)", "R$ 1.500,00"},
                {"Elaboracao de PGR", "Grande (acima de 100)", "R$ 3.000,00"},
                {"Elaboracao de PCMSO", "Qualquer porte", "R$ 800,00"},
                {"Elaboracao de LTCAT", "Qualquer porte", "R$ 1.200,00"},
                {"Consultoria de EPI", "Qualquer porte", "R$ 500,00"},
                {"Inspecao de Seguranca", "Por visita", "R$ 300,00"},
                {"Medicao de Ruido", "Por medicao", "R$ 200,00"},
                {"Treinamento NR-33", "Por aluno", "R$ 250,00"},
                {"Treinamento NR-35", "Por aluno", "R$ 150,00"}
            };

            for (String[] row : precos) {
                for (int i = 0; i < 3; i++) {
                    PdfPCell c = new PdfPCell(new Phrase(row[i], new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.NORMAL)));
                    c.setPadding(6);
                    if (i == 2) c.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tabela.addCell(c);
                }
            }
            doc.add(tabela);

            doc.add(new Paragraph(" "));

            // ===== CRONOGRAMA =====
            Paragraph h4 = new Paragraph("Cronograma de Execucao", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, java.awt.Font.BOLD));
            h4.setSpacingAfter(10);
            doc.add(h4);

            String[] cronograma = {
                "Dia 1: Visita tecnica e levantamento de riscos",
                "Dia 2-3: Elaboracao do inventario de riscos e matriz de risco",
                "Dia 4: Elaboracao do plano de acao",
                "Dia 5: Revisao e entrega do PGR em PDF",
                "Dia 6-30: Suporte e ajustes"
            };
            for (String c : cronograma) {
                Paragraph cp = new Paragraph("→ " + c, new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.NORMAL));
                cp.setSpacingAfter(4);
                doc.add(cp);
            }

            doc.add(new Paragraph(" "));

            // ===== CHAMADA PARA ACAO =====
            Paragraph cta = new Paragraph("NAO ESPERE A FISCALIZACAO CHEGAR!", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, java.awt.Font.BOLD));
            cta.setAlignment(Element.ALIGN_CENTER);
            cta.setSpacingBefore(15);
            doc.add(cta);

            Paragraph cta2 = new Paragraph("Proteja sua empresa, seus trabalhadores e seu patrimonio. " +
                "Entre em contato agora e solicite um orcamento personalizado.", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.NORMAL));
            cta2.setAlignment(Element.ALIGN_CENTER);
            cta2.setSpacingAfter(15);
            doc.add(cta2);

            // Box de contato
            PdfPTable contatoBox = new PdfPTable(1);
            contatoBox.setWidthPercentage(80);
            PdfPCell cb = new PdfPCell();
            cb.setBackgroundColor(new Color(240, 253, 244));
            cb.setPadding(15);
            cb.setBorderColor(verde);
            cb.setBorderWidth(2);
            Paragraph contatoText = new Paragraph(
                "Entre em contato:\n" +
                "Telefone: (00) 0000-0000\n" +
                "WhatsApp: (00) 00000-0000\n" +
                "E-mail: contato@suaempresa.com.br\n" +
                "Site: www.suaempresa.com.br",
                new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.NORMAL)
            );
            contatoText.setAlignment(Element.ALIGN_CENTER);
            cb.addElement(contatoText);
            contatoBox.addCell(cb);
            doc.add(contatoBox);

            doc.add(new Paragraph(" "));
            Paragraph rodape = new Paragraph("Este documento e uma proposta comercial e nao tem valor contratual. Os valores podem variar conforme a complexidade do ambiente de trabalho.", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.ITALIC));
            rodape.setAlignment(Element.ALIGN_CENTER);
            doc.add(rodape);

            doc.close();

            JOptionPane.showMessageDialog(this, "Proposta de vendas gerada!\n\nLocal: " + path, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(new File(path));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}



