package com.valkryst.gpt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.NonNull;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A singleton class for accessing the OpenAI Chat API and Whisper API.
 */
public class ChatAPI {
    private final static String API_KEY = System.getenv("OPEN_AI_API_KEY");
    private final Gson GSON = new Gson();
    private final OkHttpClient CLIENT = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build();

    private static final ChatAPI INSTANCE = new ChatAPI();

    /**
     * Returns the singleton instance of the {@code ChatAPI} class.
     *
     * @return the singleton instance of the {@code ChatAPI} class.
     */
    public static ChatAPI getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the response text from the OpenAI Chat API.
     *
     * @param chat the chat message to send to the API.
     * @return the response text from the API.
     * @throws IOException if an I/O error occurs.
     */
    public String getResponse(@NonNull Chat chat) throws IOException {
        final var requestBody = new JsonObject();
        requestBody.add("messages", GSON.toJsonTree(chat.toJson()));
        requestBody.addProperty("model", "gpt-3.5-turbo");
        requestBody.addProperty("temperature", 0.5);
        // requestBody.addProperty("max_tokens", chat.getPersonality().getGptModel().getMaxTokens());
        requestBody.addProperty("user", "Valkryst"); // todo Don't use my username.

        final var requestbody = RequestBody.create(requestBody.toString(), MediaType.parse("application/json"));

        final var request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(requestbody)
                .build();

        try (final var response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Request failed with status code " + response.code() + ":" + System.lineSeparator() + response.body().string());
            }

            final var jsonResponse = GSON.fromJson(response.body().string(), JsonObject.class);
            final var choices = jsonResponse.getAsJsonArray("choices");
            final var firstChoice = choices.get(0).getAsJsonObject();
            final var text = firstChoice.getAsJsonObject("message");
            return text.get("content").getAsString();
        }
    }
}