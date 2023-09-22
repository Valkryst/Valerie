package com.valkryst.gpt;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.valkryst.io.FileIO;
import com.valkryst.io.FolderIO;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Represents a chat between two individuals. */
public class Chat {
    /** The Personality that the Chat belongs to. */
    @Getter private final Personality personality;

    /** The unique ID of the chat. */
    private String id;

    /** The name of the chat. */
    @Getter @Setter private String name;

    /** The messages in the chat. */
    @Getter @Setter private List<Message> messages = new ArrayList<>();

    /**
     * Constructs a new {@code Chat}.
     *
     * @param personality The Personality that the Chat belongs to.
     * @param name The name of the chat.
     */
    public Chat(final Personality personality, final @NonNull String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name cannot be empty.");
        }

        id = UUID.randomUUID().toString();
        this.personality = personality;
        this.name = name;
    }

    /**
     * Constructs a new {@code Chat}.
     *
     * @param personality The Personality that the Chat belongs to.
     * @param path The path to the JSON file.
     *
     * @throws IOException If an I/O error occurs.
     */
    public Chat(final Personality personality, final @NonNull Path path) throws IOException {
        this.personality = personality;

        load(path);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Converts the Chat to a JSON format ingestible by the GPT API.
     *
     * @return The JSON.
     */
    public JsonArray toJson() {
        final var json = new JsonArray();
        for (final var message : messages) {
            json.add(message.toJson());
        }
        return json;
    }

    /**
     * Saves the Chat to a JSON file.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void save() throws IOException {
        final var folderPath = FolderIO.getFolderPathForType(Chat.class).resolve(personality.getId());
        final var filePath = FileIO.getFilePath(folderPath, id + ".json");

        final var json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("name", name);

        final var messagesJson = new JsonArray();
        for (final var message : messages) {
            messagesJson.add(message.toJson());
        }
        json.add("messages", messagesJson);

        FileIO.saveJsonToDisk(filePath, json);
    }

    /**
     * Loads the Chat from a JSON file.
     *
     * @param path The path to the JSON file.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void load(final @NonNull Path path) throws IOException {
        final var json = FileIO.loadJsonFromDisk(path);
        id = json.get("id").getAsString();
        name = json.get("name").getAsString();

        final var messagesJson = json.get("messages").getAsJsonArray();
        for (final var messageJson : messagesJson) {
            messages.add(new Message(messageJson.getAsJsonObject()));
        }
    }

    /**
     * Deletes the Chat from disk.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void delete() throws IOException {
        final var folderPath = FolderIO.getFolderPathForType(Chat.class).resolve(personality.getId());
        final var filePath = FileIO.getFilePath(folderPath, id + ".json");
        Files.delete(filePath);
    }
}