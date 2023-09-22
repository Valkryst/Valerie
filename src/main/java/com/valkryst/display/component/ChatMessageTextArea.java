package com.valkryst.display.component;

import com.valkryst.gpt.ChatGptModels;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ChatMessageTextArea extends JTextArea implements DocumentListener, FocusListener {
    private final Color originalForegroundColor = super.getForeground();

    /** The debounce timer, used to prevent the token count from updating too frequently. */
    private Timer debounceTimer;

    /**
     * Constructs a new {@code ChatMessageTextArea}.
     *
     * @param gptModel The GPT model to use.
     *
     * @param tokenCountLabel The label that displays the number of tokens in the text area.
     */
    public ChatMessageTextArea(final ChatGptModels gptModel, final JLabel tokenCountLabel) {
        super.setLineWrap(true);
        super.setWrapStyleWord(true);
        super.setToolTipText("Enter a message to send to the chat. Markdown formatting is supported.");
        super.setText(super.getToolTipText());
        super.setForeground(Color.GRAY);

        if (gptModel == null) {
            super.setEnabled(false);
            return;
        }

        super.getDocument().addDocumentListener(this);
        super.addFocusListener(this);

        final var filter = new LengthLimitDocumentFilter(gptModel);
        ((AbstractDocument) super.getDocument()).setDocumentFilter(filter);

        debounceTimer = new Timer(500, e -> {
            final var encoding = gptModel.getEncoding();
            final int tokens = encoding.countTokens(super.getText());
            tokenCountLabel.setText(tokens + "/" + gptModel.getMaxTokens() + " Tokens");
        });
        debounceTimer.setRepeats(false);
    }

    @Override
    public void focusGained(final FocusEvent e) {
        // Remove prompt text
        if (super.getText().equals(super.getToolTipText())) {
            super.setText("");
            super.setForeground(originalForegroundColor);
        }
    }

    @Override
    public void focusLost(final FocusEvent e) {
        // Add prompt text
        if (super.getText().isEmpty()) {
            super.setText(super.getToolTipText());
            super.setForeground(Color.GRAY);
        }
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        debounceTimer.restart();
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        debounceTimer.restart();
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
        debounceTimer.restart();
    }

    /** Filters out text that would cause the text area to exceed the maximum number of tokens. */
    private static class LengthLimitDocumentFilter extends DocumentFilter {
        private final ChatGptModels gptModel;

        public LengthLimitDocumentFilter(final @NonNull ChatGptModels gptModel) {
            this.gptModel = gptModel;
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            final var currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            final var replacedText = currentText.substring(0, offset) + text + currentText.substring(offset + length);

            if (gptModel.getEncoding().countTokens(replacedText) <= gptModel.getMaxTokens()) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            final var currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            final var insertedText = currentText.substring(0, offset) + string + currentText.substring(offset);

            if (gptModel.getEncoding().countTokens(insertedText) <= gptModel.getMaxTokens()) {
                super.insertString(fb, offset, string, attr);
            }
        }
    }
}
