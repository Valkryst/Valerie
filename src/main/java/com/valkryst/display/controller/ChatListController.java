package com.valkryst.display.controller;

import com.valkryst.VMVC.controller.Controller;
import com.valkryst.display.Display;
import com.valkryst.display.model.ChatListModel;
import com.valkryst.gpt.Chat;
import lombok.NonNull;

import javax.swing.*;
import java.io.IOException;

/** The controller for a {@link ChatListModel}. */
public class ChatListController extends Controller<ChatListModel> {
    /**
     * Constructs a new {@code ChatListController}.
     *
     * @param model The model associated with this controller.
     */
    public ChatListController(@NonNull ChatListModel model) {
        super(model);
    }

    /**
     * Creates a new {@link Chat} with the given name.
     *
     * @param name The name of the {@link Chat} to create.
     */
    public void createChat(final String name) {
        if (name == null || name.isEmpty()) {
            return;
        }

        final var chat = new Chat(super.model.getPersonality(), name);
        super.model.getChats().addElement(chat);

        try {
            chat.save();
        } catch (final IOException e) {
            Display.displayError(null, e);
        }
    }

    /**
     * Deletes the specified {@link Chat}.
     *
     * @param chat The {@link Chat} to delete.
     */
    public void deleteChat(final @NonNull Chat chat) {
        try {
            chat.delete();
            super.model.getChats().removeElement(chat);
        } catch (final IOException e) {
            Display.displayError(null, e);
        }
    }

    /**
     * Returns the list of {@link Chat}s.
     *
     * @return The list of {@link Chat}s.
     */
    public DefaultListModel<Chat> getChats() {
        return super.model.getChats();
    }

    /**
     * Returns the name of the personality.
     *
     * @return The name of the personality.
     */
    public String getPersonalityName() {
        final var personality = super.model.getPersonality();
        return personality == null ? "" : personality.getName();
    }

    /**
     * Returns the selected {@link Chat}.
     *
     * @return The selected {@link Chat}.
     */
    public Chat getSelectedChat() {
        return super.model.getSelectedChat();
    }

    /**
     * Sets the selected {@code Chat}.
     *
     * @param chat The {@code Chat} to select.
     */
    public void setSelectedChat(final Chat chat) {
        super.model.setSelectedChat(chat);
    }
}
