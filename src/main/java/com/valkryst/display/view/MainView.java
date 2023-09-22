package com.valkryst.display.view;

import com.valkryst.display.controller.MainController;
import com.valkryst.display.model.HomeTabModel;
import com.valkryst.display.model.PersonalitiesTabModel;
import com.valkryst.display.model.SettingsTabModel;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class MainView extends View<MainController> {
    /**
     * Constructs a new {@code MainView}.
     *
     * @param controller The controller for this view.
     */
    public MainView(final @NonNull MainController controller) {
        super(controller);

        final var tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Home", new HomeTabModel().createView());
        tabbedPane.addTab("Personalities", new PersonalitiesTabModel().createView());
        tabbedPane.addTab("Settings", new SettingsTabModel().createView());

        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);
    }
}
