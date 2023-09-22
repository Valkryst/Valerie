package com.valkryst.display.view;

import com.valkryst.display.Display;
import com.valkryst.display.controller.WhisperSettingsController;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class WhisperSettingsView extends View<WhisperSettingsController> {
    /**
     * Constructs a new {@code WhisperSettingsView}.
     *
     * @param controller The controller associated with this view.
     */
    public WhisperSettingsView(@NonNull WhisperSettingsController controller) {
        super(controller);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        // Executable file path label and button
        JLabel executableLabel = new JLabel("Executable file path:");
        c.gridx = 0;
        c.gridy = 0;
        add(executableLabel, c);

        JTextField executableField = new JTextField();
        executableField.setEditable(false);
        executableField.setText(controller.getExecutablePath().toString());
        executableField.setToolTipText(controller.getExecutablePath().toString());
        c.gridx = 1;
        c.gridy = 0;
        add(executableField, c);

        c.gridx = 2;
        c.gridy = 0;
        add(createExecutableSelectionButton(controller, executableField), c);

        // Model file path label and button
        JLabel modelLabel = new JLabel("Model file path:");
        c.gridx = 0;
        c.gridy = 1;
        add(modelLabel, c);

        JTextField modelField = new JTextField();
        modelField.setEditable(false);
        modelField.setText(controller.getModelPath().toString());
        modelField.setToolTipText(controller.getModelPath().toString());
        c.gridx = 1;
        c.gridy = 1;
        add(modelField, c);

        c.gridx = 2;
        c.gridy = 1;
        add(createModelSelectButton(controller, modelField), c);

        // Locale selection
        JLabel localeLabel = new JLabel("Locale:");
        c.gridx = 0;
        c.gridy = 2;
        add(localeLabel, c);

        c.gridx = 1;
        c.gridy = 2;
        add(createLanguageComboBox(controller), c);

        // Processor selection
        JLabel processorLabel = new JLabel("Processors:");
        c.gridx = 0;
        c.gridy = 3;
        add(processorLabel, c);

        c.gridx = 1;
        c.gridy = 3;
        add(createProcessorsComboBox(controller), c);
    }

    /**
     * Creates a button for selecting the executable file.
     *
     * @param controller The controller.
     *
     * @return The button.
     */
    private JButton createExecutableSelectionButton(final @NonNull WhisperSettingsController controller, final @NonNull JTextField textField) {
        final var button = new JButton("Browse");
        button.addActionListener(e -> {
            final var path = showFileChooser(controller.getModelPath());

            if (path != null) {
                try {
                    textField.setText(path.toString());
                    textField.setToolTipText(path.toString());

                    controller.setExecutablePath(path);
                } catch (final IOException ex) {
                    Display.displayError(button.getParent(), ex);
                }
            }
        });

        return button;
    }

    /**
     * Creates a combo box for selecting the language.
     *
     * @param controller The controller.
     *
     * @return The combo box.
     */
    private JComboBox<Locale> createLanguageComboBox(final @NonNull WhisperSettingsController controller) {
        final var renderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                final var locale = (Locale) value;
                final var name = locale.getDisplayName(Locale.getDefault());
                return super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
            }
        };

        final var locales = Locale.getAvailableLocales();
        Arrays.sort(locales, Comparator.comparing(o -> o.getDisplayName(Locale.getDefault())));

        final var comboBox = new JComboBox<>(locales);
        comboBox.setRenderer(renderer);
        comboBox.setSelectedItem(controller.getLanguage());

        comboBox.addActionListener(e -> {
            try {
                controller.setLanguage((Locale) comboBox.getSelectedItem());
            } catch (final IOException | NullPointerException ex) {
                Display.displayError(comboBox.getParent(), ex);
            }
        });

        return comboBox;
    }

    /**
     * Creates a button for selecting the model file.
     *
     * @param controller The controller.
     *
     * @return The button.
     */
    private JButton createModelSelectButton(final @NonNull WhisperSettingsController controller, final @NonNull JTextField textField) {
        final var button = new JButton("Browse");
        button.addActionListener(e -> {
            final var path = showFileChooser(controller.getModelPath());

            if (path != null) {
                try {
                    textField.setText(path.toString());
                    textField.setToolTipText(path.toString());

                    controller.setModelPath(path);
                } catch (final IOException ex) {
                    Display.displayError(button.getParent(), ex);
                }
            }
        });

        return button;
    }

    /**
     * Creates a combo box for selecting the number of processors to use.
     *
     * @param controller The controller.
     *
     * @return The combo box.
     */
    private JComboBox<Integer> createProcessorsComboBox(final @NonNull WhisperSettingsController controller) {
        final var processorsList = new Integer[controller.getTotalProcessors()];
        for (int i = 0 ; i < processorsList.length ; i++) {
            processorsList[i] = i + 1;
        }

        final var comboBox = new JComboBox<>(processorsList);
        comboBox.setSelectedItem(controller.getProcessors());
        comboBox.setToolTipText("""
            You should not change this value unless you are attempting to transcribe multiple minutes of audio at once.
            \n
            The recording is split among the selected number of processors, so short recordings will be chopped up and
            Whisper will not be able to correctly transcribe the broken audio.
        """);

        comboBox.addActionListener(e -> {
            try {
                controller.setProcessors((Integer) comboBox.getSelectedItem());
            } catch (final IOException | NullPointerException ex) {
                Display.displayError(comboBox.getParent(), ex);
            }
        });

        return comboBox;
    }

    /**
     * Shows a file chooser dialog.
     *
     * @param currentDirectory The current directory.
     *
     * @return The selected file, or null if the user cancelled the dialog.
     */
    private Path showFileChooser(final @NonNull Path currentDirectory) {
        final var fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(currentDirectory.toFile());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        final var returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().toPath();
        }

        return null;
    }
}
