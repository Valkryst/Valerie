package com.valkryst.gpt;

import com.google.gson.JsonObject;
import com.valkryst.eleven_labs.ElevenLabsModels;
import com.valkryst.io.FileIO;
import com.valkryst.io.FolderIO;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class Personality {
    /** The unique ID of the Personality. */
    @Getter private String id;

    /** The name of the Personality. */
    @Getter @Setter private String name;

    /** The ChatGPT model to use when chatting with the Personality. */
    @Getter private ChatGptModels gptModel = ChatGptModels.GPT_3_5_TURBO;

    /** The ElevenLabs model to use when chatting with the Personality. */
    @Getter private ElevenLabsModels elevenLabsModel = ElevenLabsModels.BELLA;

    /** The chats that the user has opened with the Personality. */
    @Getter private final DefaultListModel<Chat> chats = new DefaultListModel<>();

    /**
     * Constructs a new {@code Personality}.
     *
     * @param name The name of the Personality.
     */
    public Personality(final @NonNull String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name cannot be empty.");
        }

        id = UUID.randomUUID().toString();
        this.name = name;
    }

    /**
     * Constructs a new {@code Personality}.
     *
     * @param path The path to the JSON file.
     *
     * @throws IOException If an I/O error occurs.
     */
    public Personality(final @NonNull Path path) throws IOException {
        loadPersonality(path);
        loadChats();
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Saves the {@code Personality} to a JSON file, and saves all of the {@code Chat}s.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void save() throws IOException {
        final var folderPath = FolderIO.getFolderPathForType(Personality.class);
        final var filePath = FileIO.getFilePath(folderPath, id + ".json");

        final var json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("name", name);
        json.addProperty("gptModel", gptModel.name());
        json.addProperty("elevenLabsModel", elevenLabsModel.name());

        FileIO.saveJsonToDisk(filePath, json);
    }

    /**
     * Loads the {@code Personality} from a JSON file.
     *
     * @param path The path to the JSON file.
     *
     * @throws IOException If an I/O error occurs.
     */
    private void loadPersonality(final @NonNull Path path) throws IOException {
        final var json = FileIO.loadJsonFromDisk(path);
        id = json.get("id").getAsString();
        name = json.get("name").getAsString();
        gptModel = ChatGptModels.valueOf(json.get("gptModel").getAsString());
        elevenLabsModel = ElevenLabsModels.valueOf(json.get("elevenLabsModel").getAsString());
    }

    /**
     * Loads the {@code Chat}s, belonging to this {@code Personality}, from disk.
     *
     * @throws IOException If an I/O error occurs.
     */
    private void loadChats() throws IOException {
        if (id == null) {
            throw new IllegalStateException("You are likely trying to load the chats before the Personality has been loaded.");
        }

        final var folderPath = FolderIO.getFolderPathForType(Chat.class).resolve(id);
        final var chatPaths = FolderIO.getAllFilesInFolderWithExtension(folderPath, ".json");

        for (final var path : chatPaths) {
            chats.addElement(new Chat(this, path));
        }
    }

    /**
     * Deletes the {@code Personality} from disk.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void delete() throws IOException {
        final var iterator = chats.elements().asIterator();
        while (iterator.hasNext()) {
            iterator.next().delete();
        }

        final var folderPath = FolderIO.getFolderPathForType(Personality.class);
        final var filePath = FileIO.getFilePath(folderPath, id + ".json");
        Files.delete(filePath);
    }
}