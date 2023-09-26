package com.valkryst.Valerie.gpt;

import com.google.gson.JsonObject;
import lombok.NonNull;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public record Message(@NonNull MessageRole role, @NonNull String content) {
    /**
     * Constructs a new Message.
     *
     * @param role The role of message.
     * @param content The content of the message.
     */
    public Message {
        if (content.isEmpty()) {
            throw new IllegalArgumentException("The content cannot be empty.");
        }
    }

    public Message(final @NonNull JsonObject json) {
        this(
            MessageRole.valueOf(json.get("role").getAsString().toUpperCase()),
            json.get("content").getAsString()
        );
    }

    /**
     * Converts the message to a JSON format ingestible by the GPT API.
     *
     * @return The JSON.
     */
    public JsonObject toJson() {
        final var json = new JsonObject();
        json.addProperty("role", role.toString().toLowerCase());
        json.addProperty("content", content);
        return json;
    }

    /**
     * Converts the message to HTML.
     *
     * @return The HTML.
     */
    public String toHtml() {
        final var renderer = HtmlRenderer.builder().build();
        final var parser = Parser.builder().build();
        return renderer.render(parser.parse(content));
    }
}

