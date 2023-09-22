package com.valkryst.display.view;

import com.valkryst.AudioRecorder;
import com.valkryst.api.whisper.WhisperLocal;
import com.valkryst.display.Display;
import com.valkryst.display.component.ChatMessageTextArea;
import com.valkryst.display.controller.ChatController;
import com.valkryst.display.model.MessageModel;
import com.valkryst.gpt.Message;
import com.valkryst.gpt.MessageRole;
import lombok.NonNull;
import net.miginfocom.swing.MigLayout;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.nio.file.Files;

public class ChatView extends View<ChatController> {
    /** The message text area. */
    private final JTextArea messageTextArea;

    /** The record button. */
    private final JButton recordButton;

    /** The send button. */
    private final JButton sendButton = createMessageSendButton();


    /** The messages view. */
    private final JPanel messagesView = new JPanel();

    private final JLabel tokenCountLabel;

    /** The chat scroll pane. */
    private final JScrollPane chatScrollPane;

    /**
     * Constructs a new {@code View}.
     *
     * @param controller The {@code Controller} associated with this view.
     */
    public ChatView(@NonNull ChatController controller) {
        super(controller);

        messagesView.setLayout(new MigLayout("fillx, flowy", "[fill]"));
        controller.getMessagePanels().forEach(messagesView::add);

        chatScrollPane = new JScrollPane(messagesView);
        chatScrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        chatScrollPane.setOpaque(true);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(3);
        chatScrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                chatScrollPane.getViewport().revalidate();
            }
        });

        final var buttonPanel = new JPanel();
        recordButton = createAudioRecordButton(buttonPanel);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        final var personality = controller.getPersonality();
        if (personality == null) {
            tokenCountLabel = new JLabel("");
            messageTextArea = new ChatMessageTextArea(null, tokenCountLabel);
        } else {
            final var gptModel = controller.getPersonality().getGptModel();
            tokenCountLabel = new JLabel("0/" + gptModel.getMaxTokens() + " Tokens");
            messageTextArea = new ChatMessageTextArea(gptModel, tokenCountLabel);
        }

        buttonPanel.add(tokenCountLabel);
        buttonPanel.add(recordButton);
        buttonPanel.add(sendButton);

        final var messagePanel = new JPanel();
        messagePanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
        messagePanel.setLayout(new BorderLayout());
        messagePanel.add(new JScrollPane(messageTextArea), BorderLayout.CENTER);
        messagePanel.add(buttonPanel, BorderLayout.SOUTH);

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder("Selected Chat"));
        this.add(chatScrollPane, BorderLayout.CENTER);
        this.add(messagePanel, BorderLayout.SOUTH);
    }

    /**
     * Adds a message to the chat and automatically scrolls to the bottom.
     *
     * @param message The message to add.
     */
    private void addMessageToChat(final Message message) {
        if (message == null) {
            return;
        }

        // Automatically scroll to the bottom of the chat when a new message is added.
        chatScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                final var adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                chatScrollPane.getVerticalScrollBar().removeAdjustmentListener(this);
            }
        });

        final var messageView = new MessageModel(message).createView();

        if (chatScrollPane.getComponents().length % 2 == 0) {
            messageView.setBackground(messageView.getBackground().darker());
        }

        messagesView.add(messageView);
        chatScrollPane.revalidate();
    }

    private void sendMessage() {
        final var message = messageTextArea.getText();
        messageTextArea.setText("Sending message and waiting for response...");
        messageTextArea.repaint();

        messageTextArea.setEnabled(false);
        recordButton.setEnabled(false);
        sendButton.setEnabled(false);

        addMessageToChat(new Message(MessageRole.USER, message));

        final var thread = new Thread(() -> {
            final var responseMessage = controller.sendMessage(message);

            SwingUtilities.invokeLater(() -> {
                addMessageToChat(responseMessage);

                messageTextArea.setEnabled(true);
                recordButton.setEnabled(true);
                sendButton.setEnabled(true);

                messageTextArea.setText("");
            });
        });
        thread.start();
    }

    private JButton createAudioRecordButton(final @NonNull JPanel buttonPanel) {
        final var button = new JButton("Start Audio Transcription");
        button.setToolTipText("Starts an audio transcription session.\nWhen the session is complete, any existing text will be replaced with the transcription.");
        button.setEnabled(!controller.isChatNull());

        // todo Should select mic based on settings.
        final AudioRecorder audioRecorder;
        try {
            audioRecorder = new AudioRecorder(AudioRecorder.getInputSourceNames().get(0));
        } catch (final LineUnavailableException e) {
            Display.displayError(this, e);

            button.setEnabled(false);
            return button;
        }

        button.addActionListener(e -> {
            try {
                audioRecorder.startRecording();
            } catch (final IOException ex) {
                Display.displayError(this, ex);
                return;
            }

            buttonPanel.remove(button);

            final var stopButton = createStopRecordingButton(buttonPanel, audioRecorder);
            buttonPanel.add(stopButton, 1);
            stopButton.requestFocus();
            buttonPanel.revalidate();
        });

        return button;
    }

    private JButton createStopRecordingButton(final @NonNull JPanel buttonPanel, final @NonNull AudioRecorder audioRecorder) {
        final var button = new JButton("Stop Audio Transcription");
        button.setToolTipText("Stop Audio Transcription");
        button.setEnabled(!controller.isChatNull());

        button.addActionListener(e -> {
            try {
                final var path = audioRecorder.stopRecording();

                try {
                    final var whisper = new WhisperLocal(path);
                    messageTextArea.setText(whisper.getTranscription());
                    Files.delete(path);
                } catch (final IllegalArgumentException | IOException | UnsupportedAudioFileException ex) {
                    Display.displayError(this, ex); // todo Button never switches when error happens
                }
            } catch (final InterruptedException ex) {
                /*
                 * If the recording cannot be stopped, then we must exit the program to prevent the recorder from
                 * continuing to run in the background. If we do not, then the recorder may continue to record and
                 * write to the file, which will waste disk space.
                 */
                Display.displayError(this, ex);
                System.exit(1);
            }

            buttonPanel.remove(button);

            buttonPanel.add(recordButton, 1);
            recordButton.requestFocus();
            buttonPanel.revalidate();
        });

        return button;
    }

    /**
     * Creates the message send button.
     *
     * @return The message send button.
     */
    private JButton createMessageSendButton() {
        final var button = new JButton("Submit");
        button.setEnabled(!controller.isChatNull());
        button.addActionListener(e -> sendMessage());
        return button;
    }
}
