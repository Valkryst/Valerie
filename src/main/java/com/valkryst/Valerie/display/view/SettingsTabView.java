package com.valkryst.Valerie.display.view;

import com.valkryst.VMVC.view.View;
import com.valkryst.Valerie.display.controller.SettingsTabController;
import com.valkryst.Valerie.display.model.ChatGptSettingsModel;
import com.valkryst.Valerie.display.model.WhisperSettingsModel;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class SettingsTabView extends View<SettingsTabController> {
    /**
     * Constructs a new {@code SettingsTabView}.
     *
     * @param controller The controller associated with this view.
     */
    public SettingsTabView(@NonNull SettingsTabController controller) {
        super(controller);

        final var tabbedPane = new JTabbedPane();
        tabbedPane.addTab("ChatGPT", ChatGptSettingsModel.getInstance().createView());
        tabbedPane.addTab("Whisper", WhisperSettingsModel.getInstance().createView());

        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);
    }
}
