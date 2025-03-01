package com.valkryst.Valerie.display.view;

import com.valkryst.JToggleablePasswordField.JToggleablePasswordField;
import com.valkryst.VMVC.view.View;
import com.valkryst.Valerie.display.Display;
import com.valkryst.Valerie.display.controller.ChatGptSettingsController;
import com.valkryst.Valerie.gpt.ChatGptModels;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class ChatGptSettingsView extends View<ChatGptSettingsController> {
    /**
     * Constructs a new {@code WhisperSettingsView}.
     *
     * @param controller The controller associated with this view.
     */
    public ChatGptSettingsView(@NonNull ChatGptSettingsController controller) {
        super(controller);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        // API key label and text field
        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("OpenAI API key:"), c);

        final var apiKeyField = new JToggleablePasswordField();
        apiKeyField.setText(controller.getApiKey());
        apiKeyField.setToolTipText("The OpenAI API key to use when generating text.");
        c.gridx = 1;
        add(apiKeyField, c);

        // Model label and combo box
        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("Model:"), c);

        c.gridx = 1;
        add(createModelsComboBox(controller), c);
    }

    /**
     * Creates a combo box for selecting the ChatGPT model to use.
     *
     * @param controller The controller.
     *
     * @return The combo box.
     */
    private JComboBox<ChatGptModels> createModelsComboBox(final @NonNull ChatGptSettingsController controller) {
        final var models = ChatGptModels.values();
        Arrays.sort(models, Comparator.comparing(ChatGptModels::getName));

        final var comboBox = new JComboBox<>(models);
        comboBox.setSelectedItem(controller.getModel());
        comboBox.setToolTipText("The model to use when generating text.");

        comboBox.addActionListener(e -> {
            try {
                controller.setModel((ChatGptModels) Objects.requireNonNull(comboBox.getSelectedItem()));
            } catch (final IOException | NullPointerException ex) {
                Display.displayError(comboBox.getParent(), ex);
            }
        });

        return comboBox;
    }
}
