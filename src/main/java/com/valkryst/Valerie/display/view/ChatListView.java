package com.valkryst.Valerie.display.view;

import com.valkryst.VMVC.view.View;
import com.valkryst.Valerie.display.component.JIconButton;
import com.valkryst.Valerie.display.controller.ChatListController;
import com.valkryst.Valerie.gpt.Chat;
import lombok.Getter;
import lombok.NonNull;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;

/** The view for a {@link ChatListController}. */
public class ChatListView extends View<ChatListController> {
    private static final Dimension MAX_VIEW_SIZE = new Dimension(200, Integer.MAX_VALUE);

    private static final Dimension MIN_BUTTON_SIZE = new Dimension(25, 25);

    @Getter private final JList<Chat> list;

    /**
     * Constructs a new {@code View}.
     *
     * @param controller The controller associated with this view.
     */
    public ChatListView(@NonNull ChatListController controller) {
        super(controller);

        final var deletionButton = newDeletionButton(controller);
        final var editButton = newEditButton(controller);

        list = new JList<>(controller.getChats());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            final var selectedChat = list.getSelectedValue();
            controller.setSelectedChat(selectedChat);

            if (selectedChat == null) {
                deletionButton.setEnabled(false);
                editButton.setEnabled(false);
            } else {
                deletionButton.setEnabled(true);
                editButton.setEnabled(true);
            }
        });

        if (list.getSelectedValue() == null) {
            deletionButton.setEnabled(false);
            editButton.setEnabled(false);
        }

        final var buttonPanel = new JPanel(new GridLayout(0, 3));
        buttonPanel.add(newCreationButton(controller));
        buttonPanel.add(editButton);
        buttonPanel.add(deletionButton);

        this.setMaximumSize(MAX_VIEW_SIZE);
        this.setPreferredSize(MAX_VIEW_SIZE);
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder(controller.getPersonalityName() + "'s Chats"));
        this.add(new JScrollPane(list), BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a button to create a {@link Chat}.
     *
     * @param controller The {@link ChatListController}.
     *
     * @return The button.
     */
    private JIconButton newCreationButton(final ChatListController controller) {
        final var button = new JIconButton(FontIcon.of(MaterialDesignC.CHAT_PLUS_OUTLINE, Color.WHITE));
        button.setMinimumSize(MIN_BUTTON_SIZE);
        button.setPreferredSize(MIN_BUTTON_SIZE);

        button.setToolTipText("Create a new Chat.");

        button.addActionListener(e -> {
            final var chatName = showChatCreationDialog();
            controller.createChat(chatName);
            list.setSelectedIndex(list.getModel().getSize() - 1);
        });
        return button;
    }

    /**
     * Creates a button to delete a {@link Chat}.
     *
     * @param controller The {@link ChatListController}.
     *
     * @return The button.
     */
    private JIconButton newDeletionButton(final ChatListController controller) {
        final var button = new JIconButton(FontIcon.of(MaterialDesignC.CHAT_REMOVE_OUTLINE, Color.WHITE));
        button.setMinimumSize(MIN_BUTTON_SIZE);
        button.setPreferredSize(MIN_BUTTON_SIZE);

        button.setToolTipText("Delete the currently selected Chat.");

        button.addActionListener(e -> {
            final var chat = controller.getSelectedChat();
            final var option = showDeletionDialog(chat);
            if (option == JOptionPane.YES_OPTION) {
                controller.deleteChat(chat);
                list.setSelectedIndex(list.getModel().getSize() - 1);
            }
        });
        return button;
    }

    /**
     * Creates a button to edit a {@link Chat}.
     *
     * @param controller The {@link ChatListController}.
     *
     * @return The button.
     */
    private JIconButton newEditButton(final ChatListController controller) {
        final var button = new JIconButton(FontIcon.of(MaterialDesignC.CHAT_PROCESSING_OUTLINE, Color.WHITE));
        button.setMinimumSize(MIN_BUTTON_SIZE);
        button.setPreferredSize(MIN_BUTTON_SIZE);

        button.setToolTipText("Edit the name of the currently selected Chat.");

        button.addActionListener(e -> {
            final var chat = controller.getSelectedChat();
            final var chatName = showEditDialog(chat);
            if (chatName != null) {
                chat.setName(chatName);
                list.repaint();
            }
        });
        return button;
    }

    /**
     * Shows a dialog to create a {@link Chat}.
     *
     * @return The name of the {@link Chat}.
     */
    private String showChatCreationDialog() {
        return (String) JOptionPane.showInputDialog(
            this,
            "Create Chat",
            "Enter Name:",
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            ""
        );
    }

    /**
     * Shows a dialog to confirm the deletion of a {@link Chat}.
     *
     * @param chat The {@link Chat} to delete.
     *
     * @return The option selected by the user.
     */
    private int showDeletionDialog(final @NonNull Chat chat) {
        return JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete the chat '" + chat.getName() + "'?",
            "Delete Chat",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Shows a dialog to edit a {@link Chat}.
     *
     * @param chat The {@link Chat} to edit.
     *
     * @return The new name of the {@link Chat}.
     */
    private String showEditDialog(final @NonNull Chat chat) {
        return (String) JOptionPane.showInputDialog(
            this,
            "Edit Chat",
            "Enter Name:",
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            chat.getName()
        );
    }
}
