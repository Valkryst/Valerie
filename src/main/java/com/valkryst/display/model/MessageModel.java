package com.valkryst.display.model;

import com.valkryst.VMVC.model.Model;
import com.valkryst.display.controller.MessageController;
import com.valkryst.display.view.MessageView;
import com.valkryst.gpt.Message;
import lombok.Getter;
import lombok.NonNull;

public class MessageModel extends Model<MessageController, MessageView> {
    /** The message. */
    @Getter private final Message message;

    /**
     * Constructs a new {@code MessageModel}.
     *
     * @param message The message.
     */
    public MessageModel(final @NonNull Message message) {
        this.message = message;
    }

    @Override
    protected MessageController createController() {
        return new MessageController(this);
    }

    @Override
    protected MessageView createView(final MessageController controller) {
        return new MessageView(controller);
    }
}
