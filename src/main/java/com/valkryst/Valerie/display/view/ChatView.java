package com.valkryst.Valerie.display.view;

import com.valkryst.VLineIn.LineIn;
import com.valkryst.VLineIn.LineInComboBox;
import com.valkryst.VMVC.view.View;
import com.valkryst.Valerie.api.whisper.WhisperLocal;
import com.valkryst.Valerie.display.Display;
import com.valkryst.Valerie.display.component.ChatMessageTextArea;
import com.valkryst.Valerie.display.controller.ChatController;
import com.valkryst.Valerie.display.model.ChatGptSettingsModel;
import com.valkryst.Valerie.display.model.MessageModel;
import com.valkryst.Valerie.gpt.Message;
import com.valkryst.Valerie.gpt.MessageRole;
import com.valkryst.Valerie.io.FileIO;
import com.valkryst.Valerie.io.FolderIO;
import lombok.NonNull;
import net.miginfocom.swing.MigLayout;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class ChatView extends View<ChatController> {
    /** {@link AudioFormat} to use when recording audio. */
    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(16000, 16, 1, true, true);

    /** The message text area. */
    private final JTextArea messageTextArea;

    /** Lists the available line-in sources. */
    private final JComboBox<String> lineInSourcesComboBox = createLineInComboBox();

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
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.getHorizontalScrollBar().setUnitIncrement(5);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(5);
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
        buttonPanel.add(lineInSourcesComboBox);
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

        messagesView.add(messageView);
        if (messagesView.getComponentCount() % 2 == 0) {
            messageView.setBackground(messageView.getBackground().darker());
        }

        messageView.revalidate();
        messageView.repaint();

        chatScrollPane.revalidate();
        chatScrollPane.repaint();

        SwingUtilities.invokeLater(() -> {
            final var scrollBar = chatScrollPane.getVerticalScrollBar();
            scrollBar.setValue(scrollBar.getMaximum());
        });
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

    private JComboBox<String> createLineInComboBox() {
        final var comboBox = new LineInComboBox(AUDIO_FORMAT);
        comboBox.setPlaceholderText("Select Audio Source");
        comboBox.setEnabled(!controller.isChatNull());
        comboBox.setToolTipText("Select an audio source to use for audio transcription.");
        return comboBox;
    }

    private JButton createAudioRecordButton(final @NonNull JPanel buttonPanel) {
        final var button = new JButton("Start Audio Transcription");
        button.setToolTipText("Starts an audio transcription session.\nWhen the session is complete, any existing text will be replaced with the transcription.");
        button.setEnabled(!controller.isChatNull());

        button.addActionListener(e -> {
            final var folderPath = FolderIO.getFolderPathForType(LineIn.class);
            final var outputFilePath = FileIO.getFilePath(folderPath, UUID.randomUUID() + ".wav");

            final var selectedSource = (String) lineInSourcesComboBox.getSelectedItem();
            if (selectedSource == null || selectedSource.equals("Select Audio Source")) {
                Display.displayWarning(this, "You must select an audio source to use for audio transcription.");
                return;
            }

            final LineIn lineIn;
            try {
                lineIn = new LineIn(
                    AUDIO_FORMAT,
                    selectedSource
                );

                Files.createDirectories(outputFilePath.getParent());
                lineIn.startRecording(AudioFileFormat.Type.WAVE, outputFilePath);
            } catch (final Exception ex) {
                Display.displayError(this, ex);
                return;
            }

            buttonPanel.remove(button);

            final var stopButton = createStopRecordingButton(buttonPanel, lineIn, outputFilePath);
            buttonPanel.add(stopButton, 2);
            stopButton.requestFocus();

            buttonPanel.revalidate();
            buttonPanel.repaint();
        });

        return button;
    }

    private JButton createStopRecordingButton(final @NonNull JPanel buttonPanel, final @NonNull LineIn lineIn, final @NonNull Path outputFilePath) {
        final var button = new JButton("Stop Audio Transcription");
        button.setToolTipText("Stop Audio Transcription");
        button.setEnabled(!controller.isChatNull());

        button.addActionListener(e -> {
            try {
                lineIn.stopRecording();

                try {
                    final var whisper = new WhisperLocal(outputFilePath);
                    messageTextArea.setText(whisper.getTranscription());
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
            } finally {
                try {
                    Files.delete(outputFilePath);
                } catch (final IOException ex) {
                    Display.displayError(this, ex);
                }
            }

            buttonPanel.remove(button);

            buttonPanel.add(lineInSourcesComboBox, 1);
            buttonPanel.add(recordButton, 2);
            recordButton.requestFocus();

            buttonPanel.revalidate();
            buttonPanel.repaint();
        });

        return button;
    }

    /**
     * Creates the message send button.
     *
     * @return The message send button.
     */
    private JButton createMessageSendButton() {
        final var gptSettingsModel = ChatGptSettingsModel.getInstance();
        final var isApiKeyBlank = gptSettingsModel.getApiKey().isBlank();

        final var button = new JButton("Submit");
        button.setEnabled(!controller.isChatNull() && !isApiKeyBlank);
        button.setToolTipText(isApiKeyBlank ? "You must add an OpenAI API Key to use this feature." : "Submit the message.");
        button.addActionListener(e -> sendMessage());
        return button;
    }
}
