package com.valkryst.Valerie.display.view;

import com.valkryst.JToggleablePasswordField.JToggleablePasswordField;
import com.valkryst.VMVC.view.View;
import com.valkryst.Valerie.display.Display;
import com.valkryst.Valerie.display.controller.ChatGptSettingsController;
import com.valkryst.Valerie.gpt.ChatGptModels;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class ChatGptSettingsView extends View<ChatGptSettingsController> {
    /**
     * Constructs a new {@code ChatGptSettingsView}.
     *
     * @param controller The controller associated with this view.
     */
    public ChatGptSettingsView(@NonNull final ChatGptSettingsController controller) {
        super(controller);

        setLayout(new GridBagLayout());

        final var c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        // API key label
        final var apiKeyLabel = new JLabel("OpenAI API key:");
        c.gridx = 0;
        c.gridy = 0;
        add(apiKeyLabel, c);

        // API key field
        final var apiKeyField = new JToggleablePasswordField();
        apiKeyField.setText(controller.getApiKey());
        apiKeyField.setToolTipText("The OpenAI API key to use when generating text.");
        final int fieldHeight = apiKeyField.getPreferredSize().height;
        apiKeyField.setPreferredSize(new Dimension(300, fieldHeight));

        // Add debounced auto-save functionality
        final Timer saveTimer = new Timer(1000, e -> {
            try {
                controller.setApiKey(new String(apiKeyField.getPassword()));
            } catch (final IOException ex) {
                Display.displayError(this, ex);
            }
        });
        saveTimer.setRepeats(false);

        apiKeyField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                saveTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                saveTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                saveTimer.restart();
            }
        });

        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        add(apiKeyField, c);

        // Save button
        final var saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                controller.setApiKey(new String(apiKeyField.getPassword()));
            } catch (IOException ex) {
                Display.displayError(this, ex);
            }
        });
        c.gridx = 2;
        c.gridy = 0;
        add(saveButton, c);

        // Model label
        final var modelLabel = new JLabel("Model:");
        c.gridx = 0;
        c.gridy = 1;
        add(modelLabel, c);

        // Model combo box
        final var modelsComboBox = createModelsComboBox(controller);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        add(modelsComboBox, c);
    }

    /**
     * Creates a combo box for selecting the ChatGPT model to use.
     *
     * @param controller The controller.
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