package com.valkryst.Valerie.display.view;

import com.valkryst.VLineIn.LineInComboBox;
import com.valkryst.VMVC.view.View;
import com.valkryst.Valerie.display.Display;
import com.valkryst.Valerie.display.controller.GeneralSettingsController;
import lombok.NonNull;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.IOException;

public class GeneralSettingsTabView extends View<GeneralSettingsController> {
    /** {@link AudioFormat} to use when recording audio. */
    public static final AudioFormat AUDIO_FORMAT = new AudioFormat(16000, 16, 1, true, true);

    /**
     * Constructs a new {@code GeneralSettingsTabView}.
     *
     * @param controller The controller associated with this view.
     */
    public GeneralSettingsTabView(final @NonNull GeneralSettingsController controller) {
        super(controller);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        this.add(new JLabel("Audio Input Source:"), c);

        c.gridx = 1;
        this.add(this.createLineInComboBox(controller), c);
    }

    private JComboBox<String> createLineInComboBox(final @NonNull GeneralSettingsController controller) {
        final var comboBox = new LineInComboBox(AUDIO_FORMAT);
        comboBox.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                try {
                    controller.setLineInSource((String) e.getItem());
                } catch (final IOException ex) {
                    Display.displayError(this, ex);
                }
            }
        });
        comboBox.setPlaceholderText("Select Audio Source");
        comboBox.setToolTipText("Select an audio source to use for audio transcription.");
        comboBox.setPreferredSize(new Dimension(600, comboBox.getPreferredSize().height));

        if (controller.getLineInSource() != null) {
            comboBox.setSelectedItem(controller.getLineInSource());
        }

        return comboBox;
    }
}
