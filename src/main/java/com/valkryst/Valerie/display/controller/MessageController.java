package com.valkryst.Valerie.display.controller;

import com.valkryst.VMVC.controller.Controller;
import com.valkryst.Valerie.display.model.MessageModel;

public class MessageController extends Controller<MessageModel> {
    /**
     * Constructs a new {@code MessageController}.
     *
     * @param model The {@code Model} associated with this controller.
     */
    public MessageController(final MessageModel model) {
        super(model);
    }

    public String getMessageContent() {
        return super.model.getMessage().toHtml();
    }

    public String getMessageSender() {
        return super.model.getMessage().role().name();
    }
}
