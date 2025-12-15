package com.valkryst.Valerie.display.model;

import com.google.gson.JsonObject;
import com.valkryst.VMVC.model.Model;
import com.valkryst.Valerie.display.Display;
import com.valkryst.Valerie.display.controller.ChatGptSettingsController;
import com.valkryst.Valerie.display.view.ChatGptSettingsView;
import com.valkryst.Valerie.gpt.ChatGptModels;
import com.valkryst.Valerie.io.FileIO;
import com.valkryst.Valerie.io.FolderIO;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChatGptSettingsModel extends Model<ChatGptSettingsController, ChatGptSettingsView> {
    /** The path to the settings file. */
    private static final Path FILE_PATH = FileIO.getFilePath(FolderIO.getFolderPath("Settings"), "chat_gpt.json");

    /** The singleton instance. */
    private static ChatGptSettingsModel instance;

    /** The OpenAI API key. */
    @Getter @Setter private String apiKey = "";

    /** The ChatGPT model to use. */
    @Getter @Setter private ChatGptModels model = ChatGptModels.GPT_3_5_TURBO;

    /** Prevents instantiation. */
    private ChatGptSettingsModel() {}

    @Override
    protected ChatGptSettingsController createController() {
        return new ChatGptSettingsController(this);
    }

    @Override
    protected ChatGptSettingsView createView(final @NonNull ChatGptSettingsController controller) {
        return new ChatGptSettingsView(controller);
    }

    /**
     * Saves the settings to disk.
     *
     * @throws IOException If an I/O exception occurs.
     */
    public synchronized void save() throws IOException {
        final var json = new JsonObject();
        json.addProperty("apiKey", apiKey);
        json.addProperty("model", model.name());

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

        final var json = FileIO.loadJsonFromDisk(FILE_PATH);

        var value = json.get("apiKey").getAsString();
        apiKey = value;

        value = json.get("model").getAsString();
        model = ChatGptModels.valueOf(value);
    }

    /**
     * Retrieves the singleton instance.
     *
     * @return The singleton instance.
     */
    public synchronized static ChatGptSettingsModel getInstance() {
        if (instance != null) {
            return instance;
        }

        try {
            instance = new ChatGptSettingsModel();
            instance.load();
        } catch (final IOException e) {
            Display.displayError(null, e);
        }

        return instance;
    }
}
