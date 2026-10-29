package com.gestaopgr.ui;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

public class ManualPanel extends JPanel {
    private final MainFrame main;

    public ManualPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Manual do Usuário");
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

        JLabel icon = new JLabel("📖");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(icon);
        card.add(Box.createVerticalStrut(15));

        JLabel lblTitle = new JLabel("Manual do Sistema Gestão PGR");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(8));

        JLabel lblDesc = new JLabel("<html><div style='text-align:center; width:400px'>"
            + "Guia completo de uso do software para técnicos em segurança do trabalho. "
            + "Inclui instruções passo a passo, capturas de tela e dicas de boas práticas.</div></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(100, 116, 139));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(25));

        JButton btnGerar = new JButton("Gerar Manual em PDF");
        btnGerar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGerar.setForeground(Color.WHITE);
        btnGerar.setBackground(new Color(59, 130, 246));
        btnGerar.setFocusPainted(false);
        btnGerar.setBorderPainted(false);
        btnGerar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGerar.setPreferredSize(new Dimension(220, 40));
        btnGerar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGerar.addActionListener(e -> gerarManual());
        card.add(btnGerar);
        card.add(Box.createVerticalStrut(10));

        JLabel lblInfo = new JLabel("O PDF será salvo na pasta Documentos/GestaoPGR/");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(new Color(148, 163, 184));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblInfo);

        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private void gerarManual() {
        try {
            String dir = System.getProperty("user.home") + File.separator + "GestaoPGR" + File.separator + "manuais";
            new File(dir).mkdirs();
            String path = dir + File.separator + "Manual_GestaoPGR.pdf";

            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(path));

            // Rodapé com número de páginas
            writer.setPageEvent(new PdfPageEventHelper() {
                public void onEndPage(PdfWriter w, Document d) {
                    try {
                        PdfContentByte cb = w.getDirectContent();
                        cb.beginText();
                        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);
                        cb.setFontAndSize(bf, 9);
                        cb.setTextMatrix(50, 30);
                        cb.showText("Manual do Sistema Gestao PGR  |  Pagina " + w.getPageNumber());
                        cb.endText();
                    } catch (Exception ignored) {}
                }
            });

            doc.open();

            // ===== CAPA =====
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            com.lowagie.text.Font coverTitle = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 28, com.lowagie.text.Font.BOLD);
            Paragraph t = new Paragraph("MANUAL DO USUÁRIO", coverTitle);
            t.setAlignment(Element.ALIGN_CENTER);
            doc.add(t);

            com.lowagie.text.Font coverSub = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.NORMAL);
            Paragraph sub = new Paragraph("Sistema Gestão PGR\nPrograma de Gerenciamento de Riscos", coverSub);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingBefore(10);
            doc.add(sub);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            com.lowagie.text.Font coverInfo = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.NORMAL);
            Paragraph info = new Paragraph(
                "Versão 1.0\n" +
                "Para técnicos e engenheiros de segurança do trabalho\n" +
                "Elaborado para uso com o software Gestão PGR\n\n" +
                "Data: " + java.time.LocalDate.now().toString(), coverInfo);
            info.setAlignment(Element.ALIGN_CENTER);
            doc.add(info);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            com.lowagie.text.Font footer = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.ITALIC);
            Paragraph ft = new Paragraph("Documento confidencial - Uso interno", footer);
            ft.setAlignment(Element.ALIGN_CENTER);
            doc.add(ft);

            doc.newPage();

            // ===== SUMÁRIO =====
            com.lowagie.text.Font h1 = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD);
            doc.add(new Paragraph("SUMÁRIO", h1));
            doc.add(new Paragraph(" "));

            String[] sumario = {
                "1. Introdução",
                "2. Primeiros Passos",
                "3. Cadastro de Empresas",
                "4. Tipos de Serviço e Preços",
                "5. Propostas / Orçamentos",
                "6. Contratos",
                "7. Faturas",
                "8. Pagamentos",
                "9. Dashboard Financeiro",
                "10. Dicas e Boas Práticas",
                "11. Solução de Problemas"
            };
            for (String s : sumario) {
                doc.add(new Paragraph(s, new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.NORMAL)));
            }

            doc.newPage();

            // ===== CAPÍTULOS =====
            addCapitulo(doc, "1. Introdução",
                "O Sistema Gestão PGR é uma ferramenta desktop desenvolvida para técnicos e engenheiros de segurança do trabalho. " +
                "Com ele, você pode gerenciar empresas clientes, elaborar propostas comerciais, contratos, faturas e controlar pagamentos. " +
                "O software também auxilia na elaboração de PGR (Programa de Gerenciamento de Riscos) conforme a NR-01.\n\n" +
                "O sistema é multi-tenant, ou seja, você pode atender várias empresas clientes em um único software. " +
                "Todos os dados são armazenados localmente em um banco SQLite, sem necessidade de internet.\n\n" +
                "Principais funcionalidades:\n" +
                "• Cadastro de empresas e clientes\n" +
                "• Tipos de serviço com preços\n" +
                "• Propostas e orçamentos\n" +
                "• Contratos (pontual, recorrente, manutenção)\n" +
                "• Faturas e controle de vencimentos\n" +
                "• Registro de pagamentos (PIX, boleto, cartão, etc.)\n" +
                "• Dashboard financeiro\n" +
                "• Inventário de riscos e PGR\n" +
                "• Relatórios e alertas");

            addCapitulo(doc, "2. Primeiros Passos",
                "2.1 Instalação\n" +
                "O software não requer instalação. Basta executar o arquivo gestao-pgr.jar. " +
                "Na primeira execução, o banco de dados será criado automaticamente na pasta " +
                "C:\\Users\\SeuUsuario\\GestaoPGR\\.\n\n" +
                "2.2 Primeiro Acesso\n" +
                "Ao abrir o software, você verá a tela principal com um menu lateral. " +
                "O sistema já vem com um usuário padrão (admin / admin123) e dados de exemplo.\n\n" +
                "2.3 Navegação\n" +
                "Use o menu lateral para navegar entre as telas. Clique em qualquer item para abrir o módulo correspondente. " +
                "A tela principal exibe um dashboard com indicadores gerais.\n\n" +
                "2.4 Configuração Inicial\n" +
                "Recomendamos começar por:\n" +
                "1. Cadastrar suas empresas clientes (menu Empresas)\n" +
                "2. Cadastrar os tipos de serviço que você oferece (menu Tipos de Serviço)\n" +
                "3. Criar propostas para seus clientes (menu Propostas)");

            addCapitulo(doc, "3. Cadastro de Empresas",
                "3.1 Como cadastrar uma empresa\n" +
                "1. Clique em 'Empresas / Clientes' no menu lateral.\n" +
                "2. Clique no botão 'Nova Empresa'.\n" +
                "3. Preencha os campos: Razão Social (obrigatório), CNPJ, CNAE, Endereço, Cidade, UF, CEP, Telefone, E-mail, Responsável e Número de Empregados.\n" +
                "4. Clique em 'Salvar'.\n\n" +
                "3.2 Editar ou excluir\n" +
                "Selecione uma empresa na tabela e clique em 'Editar' para alterar os dados, ou 'Excluir' para remover.\n\n" +
                "3.3 Dicas\n" +
                "• O CNPJ é importante para identificação fiscal.\n" +
                "• O CNAE ajuda na classificação de riscos por atividade.\n" +
                "• O número de empregados influencia no preço do PGR (pequena/média/grande empresa).");

            addCapitulo(doc, "4. Tipos de Serviço e Preços",
                "4.1 O que são tipos de serviço\n" +
                "São os serviços que você oferece (PGR, PCMSO, LTCAT, treinamentos, consultorias, medições, etc.) " +
                "com seus respectivos preços base.\n\n" +
                "4.2 Como cadastrar\n" +
                "1. Clique em 'Tipos de Serviço' no menu lateral.\n" +
                "2. Clique em 'Novo Serviço'.\n" +
                "3. Preencha: Nome, Descrição, Preço Base (R$) e Unidade (EMPRESA, UN, HORA, M²).\n" +
                "4. Clique em 'Salvar'.\n\n" +
                "4.3 Preços sugeridos (já cadastrados)\n" +
                "• Elaboração de PGR (Empresa Pequena): R$ 800,00\n" +
                "• Elaboração de PGR (Empresa Média): R$ 1.500,00\n" +
                "• Elaboração de PGR (Empresa Grande): R$ 3.000,00\n" +
                "• Elaboração de PCMSO: R$ 800,00\n" +
                "• Elaboração de LTCAT: R$ 1.200,00\n" +
                "• Treinamento NR-33 (Espaço Confinado): R$ 250,00/aluno\n" +
                "• Treinamento NR-35 (Trabalho em Altura): R$ 150,00/aluno\n" +
                "• Medição de Ruído: R$ 200,00\n" +
                "• Consultoria de EPI: R$ 500,00");

            addCapitulo(doc, "5. Propostas / Orçamentos",
                "5.1 Como criar uma proposta\n" +
                "1. Clique em 'Propostas / Orçamentos' no menu lateral.\n" +
                "2. Clique em 'Nova Proposta'.\n" +
                "3. Selecione a empresa cliente.\n" +
                "4. Preencha o título, descrição, desconto e condições de pagamento.\n" +
                "5. Clique em 'Adicionar Item' para incluir serviços.\n" +
                "6. Selecione o serviço, quantidade e valor unitário.\n" +
                "7. Clique em 'Salvar Proposta'.\n\n" +
                "5.2 Status da proposta\n" +
                "• Rascunho: proposta em elaboração.\n" +
                "• Enviada: proposta enviada ao cliente.\n" +
                "• Aprovada: cliente aceitou a proposta.\n" +
                "• Rejeitada: cliente recusou.\n" +
                "• Expirada: prazo de validade vencido.\n\n" +
                "5.3 Dicas\n" +
                "• Defina um prazo de validade (ex: 30 dias).\n" +
                "• Inclua condições de pagamento (à vista, parcelado, etc.).\n" +
                "• O valor final é calculado automaticamente (total - desconto).");

            addCapitulo(doc, "6. Contratos",
                "6.1 Como criar um contrato\n" +
                "1. Clique em 'Contratos' no menu lateral.\n" +
                "2. Clique em 'Novo Contrato'.\n" +
                "3. Selecione a empresa e preencha: tipo (pontual, recorrente, manutenção), título, valor total, data de início e fim.\n" +
                "4. Para contratos recorrentes, preencha o valor recorrente, periodicidade e dia de vencimento.\n" +
                "5. Clique em 'Salvar'.\n\n" +
                "6.2 Tipos de contrato\n" +
                "• Pontual: serviço único (ex: elaboração de PGR).\n" +
                "• Recorrente: serviço periódico (ex: manutenção mensal de PGR).\n" +
                "• Manutenção: contrato de manutenção contínua.\n\n" +
                "6.3 Status\n" +
                "• Ativo: contrato em vigor.\n" +
                "• Suspenso: temporariamente pausado.\n" +
                "• Encerrado: concluído.\n" +
                "• Cancelado: cancelado antes do prazo.");

            addCapitulo(doc, "7. Faturas",
                "7.1 Como criar uma fatura\n" +
                "1. Clique em 'Faturas' no menu lateral.\n" +
                "2. Clique em 'Nova Fatura'.\n" +
                "3. Selecione a empresa, preencha a descrição, valor, desconto e data de vencimento.\n" +
                "4. Clique em 'Salvar'.\n\n" +
                "7.2 Status da fatura\n" +
                "• Pendente: aguardando pagamento.\n" +
                "• Paga: pagamento confirmado.\n" +
                "• Vencida: passou da data de vencimento.\n" +
                "• Parcial: pagamento parcial recebido.\n" +
                "• Cancelada: fatura cancelada.\n\n" +
                "7.3 Registrar pagamento\n" +
                "1. Selecione a fatura na tabela.\n" +
                "2. Clique em 'Registrar Pagamento'.\n" +
                "3. Informe o valor e a forma de pagamento (PIX, boleto, cartão, etc.).\n" +
                "4. O status da fatura será atualizado automaticamente.");

            addCapitulo(doc, "8. Pagamentos",
                "8.1 Formas de pagamento aceitas\n" +
                "• PIX\n" +
                "• Boleto\n" +
                "• Cartão de Crédito\n" +
                "• Cartão de Débito\n" +
                "• Transferência Bancária\n" +
                "• Dinheiro\n" +
                "• Cheque\n\n" +
                "8.2 Controle\n" +
                "Todos os pagamentos são registrados com data, valor, forma e status. " +
                "O sistema calcula automaticamente o total recebido por empresa.");

            addCapitulo(doc, "9. Dashboard Financeiro",
                "O dashboard financeiro exibe os principais indicadores:\n" +
                "• Receita Total: soma de todos os pagamentos confirmados.\n" +
                "• A Receber: valor de faturas pendentes.\n" +
                "• Vencido: valor de faturas vencidas.\n" +
                "• Contratos Ativos: número de contratos em vigor.\n\n" +
                "Use este painel para acompanhar a saúde financeira do seu negócio.");

            addCapitulo(doc, "10. Dicas e Boas Práticas",
                "• Mantenha os cadastros de empresas atualizados.\n" +
                "• Cadastre todos os tipos de serviço antes de criar propostas.\n" +
                "• Defina preços realistas (considere seu tempo e custos).\n" +
                "• Use propostas para negociar antes de fechar contrato.\n" +
                "• Registre pagamentos assim que receber (não deixe acumular).\n" +
                "• Acompanhe faturas vencidas semanalmente.\n" +
                "• Faça backup do banco de dados regularmente.\n" +
                "• Use o dashboard para tomar decisões sobre preços e serviços.\n" +
                "• Ofereça descontos para contratos recorrentes (fidelização).\n" +
                "• Mantenha as faturas organizadas por empresa.");

            addCapitulo(doc, "11. Solução de Problemas",
                "Problema: O software não abre.\n" +
                "Solução: Verifique se o Java 21 está instalado. Execute: java -version\n\n" +
                "Problema: Erro ao salvar dados.\n" +
                "Solução: Verifique se a pasta GestaoPGR tem permissão de escrita.\n\n" +
                "Problema: Valores com ponto/vírgula.\n" +
                "Solução: Use ponto (.) como separador decimal. Ex: 1500.00\n\n" +
                "Problema: Proposta não salva itens.\n" +
                "Solução: Cadastre tipos de serviço antes de criar propostas.\n\n" +
                "Problema: Fatura não atualiza status.\n" +
                "Solução: Registre o pagamento completo (valor igual ao valor final da fatura).\n\n" +
                "Suporte: Em caso de dúvidas, consulte este manual ou entre em contato com o desenvolvedor.");

            doc.close();

            // Atualizar MainFrame para incluir Manual no menu
            JOptionPane.showMessageDialog(this,
                "Manual gerado com sucesso!\n\nLocal: " + path,
                "Manual Gerado", JOptionPane.INFORMATION_MESSAGE);

            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(new File(path));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar manual: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addCapitulo(Document doc, String titulo, String texto) throws DocumentException {
        com.lowagie.text.Font h1 = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.BOLD);
        Paragraph t = new Paragraph(titulo, h1);
        t.setSpacingBefore(10);
        t.setSpacingAfter(8);
        doc.add(t);

        com.lowagie.text.Font body = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.NORMAL);
        for (String para : texto.split("\n\n")) {
            Paragraph p = new Paragraph(para.trim(), body);
            p.setSpacingAfter(8);
            p.setAlignment(Element.ALIGN_JUSTIFIED);
            doc.add(p);
        }
    }
}


