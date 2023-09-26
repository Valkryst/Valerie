package com.valkryst.Valerie;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.valkryst.Valerie.display.Display;
import com.valkryst.Valerie.display.model.MainModel;
import javafx.application.Platform;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FlatDraculaIJTheme.setup();

        /*
         * This ensures that the JavaFX thread is kept alive, even when there
         * are no more JavaFX scenes.
         */
        Platform.setImplicitExit(false);

        SwingUtilities.invokeLater(() -> {
            // Ensure tooltips stay visible for 60 seconds.
            ToolTipManager.sharedInstance().setDismissDelay(60000);

            Display.getInstance().setContentPane(new MainModel().createView());
        });
    }
}