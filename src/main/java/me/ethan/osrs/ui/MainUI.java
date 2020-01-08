package me.ethan.osrs.ui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javax.swing.*;


public class MainUI {
    private JFrame frame;
    private JFXPanel jfxPanel = new JFXPanel();

    public MainUI() {
        loadUI();
    }

    public void loadUI() {
        Platform.runLater(() -> {
            try {
                frame = new JFrame("Ethan's Account Creator");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                jfxPanel = new JFXPanel();
                final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MainUI.fxml"));
                final Parent root = fxmlLoader.load();
                final Scene scene = new Scene(root, 280, 215);
                scene.getStylesheets().add(getClass().getResource("/dark.css").toExternalForm());
                jfxPanel.setScene(scene);
                SwingUtilities.invokeLater(() -> {
                    frame.add(jfxPanel);
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                    frame.setResizable(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
