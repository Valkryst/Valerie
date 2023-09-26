package com.valkryst.display.model;

import com.valkryst.VMVC.model.Model;
import com.valkryst.display.controller.ChatListController;
import com.valkryst.display.view.ChatListView;
import com.valkryst.gpt.Chat;
import com.valkryst.gpt.Personality;
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
