package com.valkryst.Valerie;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jthemedetecor.OsThemeDetector;
import com.valkryst.Valerie.display.Display;
import com.valkryst.Valerie.display.model.MainModel;
import javafx.application.Platform;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        OsThemeDetector.getDetector().registerListener(Main::updateTheme);

        /*
         * This ensures that the JavaFX thread is kept alive, even when there
         * are no more JavaFX scenes.
         */
        Platform.setImplicitExit(false);

        SwingUtilities.invokeLater(() -> {
            Main.updateTheme(OsThemeDetector.getDetector().isDark());

            // Ensure tooltips stay visible for 60 seconds.
            ToolTipManager.sharedInstance().setDismissDelay(60000);

            Display.getInstance().setContentPane(new MainModel().createView());
        });
    }

    private static void updateTheme(final boolean isDark) {
        SwingUtilities.invokeLater(() -> {
            if (isDark) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }

            FlatLaf.updateUI();
        });
    }
}