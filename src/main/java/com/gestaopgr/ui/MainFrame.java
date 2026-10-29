package com.gestaopgr.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.gestaopgr.dao.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

    // DAOs
    public final EmpresaDao empresaDao = new EmpresaDao();
    public final SetorDao setorDao = new SetorDao();
    public final CargoDao cargoDao = new CargoDao();
    public final PerigoDao perigoDao = new PerigoDao();
    public final RiscoDao riscoDao = new RiscoDao();
    public final PlanoAcaoDao planoAcaoDao = new PlanoAcaoDao();
    public final PgrDao pgrDao = new PgrDao();
    public final PppDao pppDao = new PppDao();
    public final TipoServicoDao tipoServicoDao = new TipoServicoDao();
    public final PropostaDao propostaDao = new PropostaDao();
    public final PropostaItemDao propostaItemDao = new PropostaItemDao();
    public final ContratoDao contratoDao = new ContratoDao();
    public final FaturaDao faturaDao = new FaturaDao();
    public final PagamentoDao pagamentoDao = new PagamentoDao();

    // Usuário logado
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioPerfil;

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;
    private JPanel mainPanel;

    private static final Color SIDEBAR_BG = new Color(28, 37, 52);
    private static final Color SIDEBAR_HOVER = new Color(45, 58, 80);
    private static final Color SIDEBAR_ACTIVE = new Color(59, 130, 246);
    private static final Color CONTENT_BG = new Color(248, 250, 252);

    public MainFrame() {
        setTitle("Gestão de PGR - Programa de Gerenciamento de Riscos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1440, 900);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1280, 720));

        // Ícone da janela (barra de título + taskbar)
        try {
            java.net.URL iconUrl = getClass().getClassLoader().getResource("gestao-pgr-48.png");
            if (iconUrl != null) {
                setIconImage(new ImageIcon(iconUrl).getImage());
            }
        } catch (Exception ignored) {}

        setLayout(new BorderLayout());

        // Sistema será mostrado após login via mostrarSistema()
    }

    public void setUsuarioLogado(Long id, String nome, String perfil) {
        this.usuarioId = id;
        this.usuarioNome = nome;
        this.usuarioPerfil = perfil;
    }

    public Long getUsuarioId() { return usuarioId; }
    public String getUsuarioNome() { return usuarioNome; }
    public String getUsuarioPerfil() { return usuarioPerfil; }

    public void mostrarLogin() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        add(new LoginPanel(this), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void mostrarSistema() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(CONTENT_BG);

        registerScreens();
        add(contentPanel, BorderLayout.CENTER);

        add(createStatusBar(), BorderLayout.SOUTH);

        cardLayout.show(contentPanel, "DASHBOARD");

        revalidate();
        repaint();
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(260, 0));
        panel.setBackground(SIDEBAR_BG);
        panel.setBorder(new EmptyBorder(10, 8, 10, 8));

        String[][] menuItems = {
            {"DASHBOARD", "📊", "Dashboard"},
            {"EMPRESAS", "🏢", "Empresas / Clientes"},
            {"SETORES", "🏭", "Setores / Ambientes"},
            {"INVENTARIO", "🔺", "Inventário de Riscos"},
            {"MATRIZ", "📈", "Matriz de Risco"},
            {"PLANO_ACAO", "📋", "Plano de Ação"},
            {"PGR", "📄", "PGR / Laudo"},
            {"PPP", "📑", "PPP"},
            {"TIPOS_SERVICO", "🛠️", "Tipos de Serviço"},
            {"PROPOSTAS", "📝", "Propostas / Orçamentos"},
            {"CONTRATOS", "📜", "Contratos"},
            {"FATURAS", "🧾", "Faturas"},
            {"PAGAMENTOS", "💰", "Pagamentos"},
            {"FINANCEIRO", "💵", "Dashboard Financeiro"},
            {"RELATORIOS", "📊", "Relatórios"},
            {"ALERTAS", "🔔", "Alertas"},
            {"CADASTROS", "🔧", "Cadastros Auxiliares"},
            {"USUARIOS", "👥", "Usuários"},
            {"VENDAS", "💼", "Proposta de Vendas"},
            {"MANUAL", "📖", "Manual do Usuário"},
            {"BACKUP", "💾", "Backup / Restore"},
            {"CONFIG", "🔧", "Configurações"},
        };

        for (String[] item : menuItems) {
            panel.add(createMenuButton(item[1], item[2], item[0]));
            panel.add(Box.createVerticalStrut(1));
        }

        panel.add(Box.createVerticalGlue());

        // Atualizador e Sobre - últimos itens no fundo da barra lateral
        panel.add(createMenuButton("🔄", "Atualizador", "ATUALIZADOR"));
        panel.add(Box.createVerticalStrut(2));
        panel.add(createMenuButton("💡", "Sobre", "SOBRE"));

        return panel;
    }

    private JButton createMenuButton(String icon, String text, String cardName) {
        JButton btn = new JButton();
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(245, 36));
        btn.setPreferredSize(new Dimension(245, 36));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, "borderless");
        btn.setLayout(new GridBagLayout());

        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(32, 20));
        iconPanel.setMinimumSize(new Dimension(32, 20));
        iconPanel.setMaximumSize(new Dimension(32, 20));
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        iconLabel.setForeground(new Color(148, 163, 184));
        iconPanel.add(iconLabel);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textLabel.setForeground(new Color(203, 213, 225));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 4, 0, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridx = 0;
        btn.add(iconPanel, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 4, 0, 0);
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btn.add(textLabel, gbc);

        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            for (Component c : sidebar.getComponents()) {
                if (c instanceof JButton) {
                    ((JButton) c).setOpaque(false);
                    ((JButton) c).setBackground(null);
                }
            }
            btn.setOpaque(true);
            btn.setBackground(SIDEBAR_ACTIVE);
        });

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!btn.isOpaque()) {
                    btn.setOpaque(true);
                    btn.setBackground(SIDEBAR_HOVER);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btn.getBackground() != null && btn.getBackground().equals(SIDEBAR_HOVER)) {
                    btn.setOpaque(false);
                    btn.setBackground(null);
                }
            }
        });

        return btn;
    }

    private void registerScreens() {
        contentPanel.add(new DashboardPanel(this), "DASHBOARD");
        contentPanel.add(new EmpresaPanel(this), "EMPRESAS");
        contentPanel.add(new SetorPanel(this), "SETORES");
        contentPanel.add(new RiscoPanel(this), "INVENTARIO");
        contentPanel.add(new MatrizRiscoPanel(this), "MATRIZ");
        contentPanel.add(new PlanoAcaoPanel(this), "PLANO_ACAO");
        contentPanel.add(new PgrPanel(this), "PGR");
        contentPanel.add(new PppPanel(this), "PPP");
        contentPanel.add(new TipoServicoPanel(this), "TIPOS_SERVICO");
        contentPanel.add(new PropostaPanel(this), "PROPOSTAS");
        contentPanel.add(new ContratoPanel(this), "CONTRATOS");
        contentPanel.add(new FaturaPanel(this), "FATURAS");
        contentPanel.add(new PagamentosPanel(this), "PAGAMENTOS");
        contentPanel.add(new FinanceiroDashboardPanel(this), "FINANCEIRO");
        contentPanel.add(new RelatoriosPanel(this), "RELATORIOS");
        contentPanel.add(new AlertasPanel(this), "ALERTAS");
        contentPanel.add(new CadastrosPanel(this), "CADASTROS");
        contentPanel.add(new UsuarioPanel(this), "USUARIOS");
        contentPanel.add(new VendasPanel(this), "VENDAS");
        contentPanel.add(new ManualPanel(this), "MANUAL");
        contentPanel.add(new AtualizadorPanel(this), "ATUALIZADOR");
        contentPanel.add(new SobrePanel(this), "SOBRE");
        contentPanel.add(new BackupPanel(this), "BACKUP");
        contentPanel.add(new ConfiguracoesPanel(this), "CONFIG");
    }

    private JPanel createPlaceholder(String title, String icon) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CONTENT_BG);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(iconLabel);

        center.add(Box.createVerticalStrut(15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(titleLabel);

        center.add(Box.createVerticalStrut(8));

        JLabel desc = new JLabel("Módulo em desenvolvimento");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setForeground(new Color(100, 116, 139));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(desc);

        panel.add(center);
        return panel;
    }

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setPreferredSize(new Dimension(0, 36));
        bar.setBackground(new Color(241, 245, 249));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JLabel status = new JLabel("  Pronto  •  Gestão PGR v1.0  •  Usuário: " + (usuarioNome != null ? usuarioNome : "Não logado"));
        status.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        status.setForeground(new Color(100, 116, 139));
        bar.add(status, BorderLayout.WEST);

        // Imagem + perfil no canto direito
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);

        ImageIcon logoIcon = null;
        try {
            java.net.URL logoUrl = getClass().getClassLoader().getResource("gestao-pgr-256.png");
            if (logoUrl != null) {
                ImageIcon original = new ImageIcon(logoUrl);
                Image scaled = original.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(scaled);
            }
        } catch (Exception ignored) {}

        if (logoIcon != null) {
            rightPanel.add(new JLabel(logoIcon));
        }

        JLabel right = new JLabel("Perfil: " + (usuarioPerfil != null ? usuarioPerfil : "-") + "  ");
        right.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        right.setForeground(new Color(100, 116, 139));
        rightPanel.add(right);
        bar.add(rightPanel, BorderLayout.EAST);

        return bar;
    }

    public void showCard(String card) {
        cardLayout.show(contentPanel, card);
    }
}
