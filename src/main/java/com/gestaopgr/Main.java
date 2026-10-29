package com.gestaopgr;

import com.formdev.flatlaf.FlatLightLaf;
import com.gestaopgr.config.DatabaseConfig;
import com.gestaopgr.ui.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseConfig.getInstance();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao inicializar banco de dados:\n" + e.getMessage(),
                "Erro Fatal", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
            UIManager.put("TabbedPane.tabHeight", 36);
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Não foi possível aplicar FlatLaf: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
