package com.valkryst.Valerie.display.model;

import com.google.gson.JsonObject;
import com.valkryst.VMVC.model.Model;
import com.valkryst.Valerie.display.Display;
import com.valkryst.Valerie.display.controller.GeneralSettingsController;
import com.valkryst.Valerie.display.view.GeneralSettingsTabView;
import com.valkryst.Valerie.io.FileIO;
import com.valkryst.Valerie.io.FolderIO;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GeneralSettingsModel extends Model<GeneralSettingsController, GeneralSettingsTabView> {
    /** The path to the settings file. */
    private static final Path FILE_PATH = FileIO.getFilePath(FolderIO.getFolderPath("Settings"), "general.json");

    /** The singleton instance. */
    private static GeneralSettingsModel instance;

    /** Name of the audio input device to use. */
    @Getter @Setter private String lineInSource = "";

    @Override
    protected GeneralSettingsController createController() {
        return new GeneralSettingsController(this);
    }

    @Override
    protected GeneralSettingsTabView createView(final @NonNull GeneralSettingsController controller) {
        return new GeneralSettingsTabView(controller);
    }

    /**
     * Saves the settings to disk.
     *
     * @throws IOException If an I/O exception occurs.
     */
    public synchronized void save() throws IOException {
        final var json = new JsonObject();
        json.addProperty("lineInSource", lineInSource);

        FileIO.saveJsonToDisk(FILE_PATH, json);
    }

    /**
     * Loads the settings from disk.
     *
     * @throws IOException If an I/O exception occurs.
     */
    private synchronized void load() throws IOException {
        if (Files.notExists(FILE_PATH)) {
            save();
        }
        System.out.println(FILE_PATH);

        final var json = FileIO.loadJsonFromDisk(FILE_PATH);

        lineInSource = json.get("lineInSource").getAsString();
    }

    /**
     * Retrieves the singleton instance.
     *
     * @return The singleton instance.
     */
    public synchronized static GeneralSettingsModel getInstance() {
        if (instance != null) {
            return instance;
        }

        try {
            instance = new GeneralSettingsModel();
            instance.load();
        } catch (final IOException e) {
            Display.displayError(null, e);
        }

        return instance;
    }
}
