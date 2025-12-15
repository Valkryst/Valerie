package com.valkryst.Valerie.display.controller;

import com.valkryst.VMVC.controller.Controller;
import com.valkryst.Valerie.display.model.GeneralSettingsModel;
import lombok.NonNull;

import java.io.IOException;

public class GeneralSettingsController extends Controller<GeneralSettingsModel> {
    /**
     * Constructs a new {@code GeneralSettingsTabController}.
     *
     * @param model The model associated with this controller.
     */
    public GeneralSettingsController(final @NonNull GeneralSettingsModel model) {
        super(model);
    }

    /**
     * Retrieves the line in source.
     *
     * @return The line in source.
     */
    public String getLineInSource() {
        return model.getLineInSource();
    }

    /**
     * Sets the line in source.
     *
     * @param audioInputName The line in source.
     * @throws IOException If an I/O exception occurs when saving the settings.
     */
    public void setLineInSource(final @NonNull String audioInputName) throws IOException {
        model.setLineInSource(audioInputName);
        model.save();
    }
}
