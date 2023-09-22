package com.valkryst.display.controller;

import com.valkryst.display.Display;
import com.valkryst.display.model.ChatModel;
import com.valkryst.display.model.MessageModel;
import com.valkryst.gpt.ChatAPI;
import com.valkryst.gpt.Message;
import com.valkryst.gpt.MessageRole;
import com.valkryst.gpt.Personality;
import lombok.NonNull;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatController extends Controller<ChatModel> {
    /**
     * Constructs a new {@code ChatController}.
     *
     * @param model The {@code Model} associated with this controller.
     */
    public ChatController(@NonNull ChatModel model) {
        super(model);
    }

    public Message sendMessage(final String message) {
        final var chat = super.model.getChat();

        chat.getMessages().add(new Message(MessageRole.USER, message));

        // Prompt ChatGPT for a response.
        try {
            final var response = ChatAPI.getInstance().getResponse(chat);
            final var responseMessage = new Message(MessageRole.ASSISTANT, response);
            chat.getMessages().add(responseMessage);
            chat.save();
            return responseMessage;
        } catch (final IOException ex) {
            Display.displayError(null, ex);
            return null;
        }
    }

    /**
     * Returns whether the Chat is null.
     *
     * @return Whether the Chat is null.
     */
    public boolean isChatNull() {
        return super.model.getChat() == null;
    }

    public List<JPanel> getMessagePanels() {
        final var chat = super.model.getChat();
        if (chat == null) {
            return List.of();
        }

        final var messages = new ArrayList<JPanel>();
        for (final var message : chat.getMessages()) {
            final var view = new MessageModel(message).createView();
            messages.add(view);

            if (message.role().equals(MessageRole.ASSISTANT)) {
                view.setBackground(view.getBackground().darker());
            }
        }

        return messages;
    }

    /**
     * Returns the Personality that the Chat belongs to.
     *
     * @return The Personality that the Chat belongs to.
     */
    public Personality getPersonality() {
        final var chat = super.model.getChat();
        return chat == null ? null : chat.getPersonality();
    }
}
