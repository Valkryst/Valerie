package com.valkryst.Valerie.display.model;

import com.google.gson.JsonObject;
import com.valkryst.VMVC.model.Model;
import com.valkryst.Valerie.display.Display;
import com.valkryst.Valerie.display.controller.WhisperSettingsController;
import com.valkryst.Valerie.display.view.WhisperSettingsView;
import com.valkryst.Valerie.io.FileIO;
import com.valkryst.Valerie.io.FolderIO;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class WhisperSettingsModel extends Model<WhisperSettingsController, WhisperSettingsView> {
    /** The path to the settings file. */
    private static final Path FILE_PATH = FileIO.getFilePath(FolderIO.getFolderPath("Settings"), "whisper.json");

    /** The singleton instance. */
    private static WhisperSettingsModel instance;

    /** The path to the executable. */
    @Getter @Setter private Path executablePath = Path.of("");

    /** The path to the trained model. */
    @Getter @Setter private Path modelPath = Path.of("");

    /** The language to use. */
    @Getter @Setter private Locale language = Locale.getDefault();

    /** The number of processors to use. */
    @Getter @Setter private int processors = 1;

    /** Prevents instantiation. */
    private WhisperSettingsModel() {}

    @Override
    protected WhisperSettingsController createController() {
        return new WhisperSettingsController(this);
    }

    @Override
    protected WhisperSettingsView createView(final @NonNull WhisperSettingsController controller) {
        return new WhisperSettingsView(controller);
    }


    /**
     * Saves the settings to disk.
     *
     * @throws IOException If an I/O exception occurs.
     */
    public void save() throws IOException {
        final var json = new JsonObject();
        json.addProperty("modelPath", modelPath.toString());
        json.addProperty("executablePath", executablePath.toString());
        json.addProperty("language", language.toLanguageTag());
        json.addProperty("processors", processors);

        FileIO.saveJsonToDisk(FILE_PATH, json);
    }

    /**
     * Loads the settings from disk.
     *
     * @throws IOException If an I/O exception occurs.
     */
    private void load() throws IOException {
        if (Files.notExists(FILE_PATH)) {
            save();
        }

        final var json = FileIO.loadJsonFromDisk(FILE_PATH);

        var value = json.get("modelPath").getAsString();
        modelPath = value.isEmpty() ? Path.of("") : Path.of(value);

        value = json.get("executablePath").getAsString();
        executablePath = value.isEmpty() ? Path.of("") : Path.of(value);

        value = json.get("language").getAsString();
        language = value.isEmpty() ? Locale.ENGLISH : Locale.forLanguageTag(value);

        value = json.get("processors").getAsString();
        processors = value.isEmpty() ? 1 : Integer.parseInt(value);
    }

    /**
     * Retrieves the singleton instance.
     *
     * @return The singleton instance.
     */
    public synchronized static WhisperSettingsModel getInstance() {
        if (instance != null) {
            return instance;
        }

        try {
            instance = new WhisperSettingsModel();
            instance.load();
        } catch (final IOException e) {
            Display.displayError(null, e);
        }

        return instance;
    }
}
