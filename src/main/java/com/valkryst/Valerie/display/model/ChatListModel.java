package com.valkryst.Valerie.display.model;

import com.valkryst.VMVC.model.Model;
import com.valkryst.Valerie.display.controller.ChatListController;
import com.valkryst.Valerie.display.view.ChatListView;
import com.valkryst.Valerie.gpt.Chat;
import com.valkryst.Valerie.gpt.Personality;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

/** The model for a {@link ChatListController}. */
public class ChatListModel extends Model<ChatListController, ChatListView> {
    /** The personality. */
    @Getter private final Personality personality;

    /** The list of chats. */
    @Getter private final DefaultListModel<Chat> chats;

    /** The currently selected chat. */
    @Getter @Setter private Chat selectedChat = null;

    /** Constructs a new {@code ChatListModel}. */
    public ChatListModel(final Personality personality) {
        this.personality = personality;
        chats = personality == null ? new DefaultListModel<>() : personality.getChats();
    }

    @Override
    protected ChatListController createController() {
        return new ChatListController(this);
    }

    @Override
    protected ChatListView createView(ChatListController controller) {
        return new ChatListView(controller);
    }
}
