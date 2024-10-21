package com.valkryst.Valerie.display.controller;

import com.valkryst.VMVC.controller.Controller;
import com.valkryst.Valerie.display.model.ChatGptSettingsModel;
import com.valkryst.Valerie.gpt.ChatGptModels;
import lombok.NonNull;

import java.io.IOException;

public class ChatGptSettingsController extends Controller<ChatGptSettingsModel> {
    /**
     * Constructs a new {@code WhisperSettingsController}.
     *
     * @param model The model associated with this controller.
     */
    public ChatGptSettingsController(@NonNull ChatGptSettingsModel model) {
        super(model);
    }

    /**
     * Retrieves the OpenAI API key.
     *
     * @return The OpenAI API key.
     */
    public String getApiKey() {
        return model.getApiKey();
    }

    /**
     * Retrieves the ChatGPT model to use.
     *
     * @return The ChatGPT model to use.
     */
    public ChatGptModels getModel() {
        return model.getModel();
    }

    /**
     * Sets the OpenAI API key.
     *
     * @param apiKey The OpenAI API key.
     * @throws IOException If an I/O exception occurs.
     */
    public void setApiKey(final @NonNull String apiKey) throws IOException {
        model.setApiKey(apiKey);
        model.save();
    }

    /**
     * Sets the ChatGPT model to use.
     *
     * @param model The ChatGPT model to use.
     * @throws IOException If an I/O exception occurs.
     */
    public void setModel(final @NonNull ChatGptModels model) throws IOException {
        this.model.setModel(model);
        this.model.save();
    }
}
