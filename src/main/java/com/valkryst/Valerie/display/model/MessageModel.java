package com.valkryst.Valerie.display.model;

import com.valkryst.VMVC.model.Model;
import com.valkryst.Valerie.display.controller.MessageController;
import com.valkryst.Valerie.display.view.MessageView;
import com.valkryst.Valerie.gpt.Message;
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
