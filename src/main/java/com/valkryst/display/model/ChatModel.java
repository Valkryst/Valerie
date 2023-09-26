package com.valkryst.display.model;

import com.valkryst.VMVC.model.Model;
import com.valkryst.display.controller.ChatController;
import com.valkryst.display.view.ChatView;
import com.valkryst.gpt.Chat;
import lombok.Getter;

public class ChatModel extends Model<ChatController, ChatView> {
    @Getter private final Chat chat;

    public ChatModel(final Chat chat) {
        this.chat = chat;
    }

    @Override
    protected ChatController createController() {
        return new ChatController(this);
    }

    @Override
    protected ChatView createView(ChatController controller) {
        return new ChatView(controller);
    }
}
