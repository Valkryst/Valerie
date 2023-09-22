package com.valkryst.display.controller;

import com.valkryst.display.model.WhisperSettingsModel;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

public class WhisperSettingsController extends Controller<WhisperSettingsModel> {
    /**
     * Constructs a new {@code WhisperSettingsController}.
     *
     * @param model The model associated with this controller.
     */
    public WhisperSettingsController(@NonNull WhisperSettingsModel model) {
        super(model);
    }

    /**
     * Retrieves the path to the executable.
     *
     * @return The path to the executable.
     */
    public Path getExecutablePath() {
        return model.getExecutablePath();
    }

    /**
     * Retrieves the path to the model.
     *
     * @return The path to the model.
     */
    public Path getModelPath() {
        return model.getModelPath();
    }

    /**
     * Retrieves the language to use.
     *
     * @return The language to use.
     */
    public Locale getLanguage() {
        return model.getLanguage();
    }

    /**
     * Retrieves the number of processors to use.
     *
     * @return The number of processors to use.
     */
    public int getProcessors() {
        return model.getProcessors();
    }

    /**
     * Retrieves the total number of processors on the system.
     *
     * @return The total number of processors on the system.
     */
    public int getTotalProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Sets the path to the executable.
     *
     * @param path The path to the executable.
     *
     * @throws IOException If an I/O exception occurs when saving the settings.
     */
    public void setExecutablePath(final Path path) throws IOException {
        model.setExecutablePath(path);
        model.save();
    }

    /**
     * Sets the path to the model.
     *
     * @param path The path to the model.
     *
     * @throws IOException If an I/O exception occurs when saving the settings.
     */
    public void setModelPath(final Path path) throws IOException {
        model.setModelPath(path);
        model.save();
    }

    /**
     * Sets the language to use.
     *
     * @param language The language to use.
     *
     * @throws IOException If an I/O exception occurs when saving the settings.
     */
    public void setLanguage(final Locale language) throws IOException {
        model.setLanguage(language);
        model.save();
    }

    /**
     * Sets the number of processors to use.
     *
     * @param processors The number of processors to use.
     *
     * @throws IOException If an I/O exception occurs when saving the settings.
     */
    public void setProcessors(final int processors) throws IOException {
        if (processors < 1) {
            throw new IllegalArgumentException("The number of processors must be greater than 0.");
        }

        if (processors > getTotalProcessors()) {
            throw new IllegalArgumentException("The number of processors must be less than or equal to the total number of processors on the system.");
        }

        model.setProcessors(processors);
        model.save();
    }
}
