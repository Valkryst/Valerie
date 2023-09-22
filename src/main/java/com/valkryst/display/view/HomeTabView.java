package com.valkryst.display.view;

import com.valkryst.display.controller.HomeTabController;
import com.valkryst.display.model.ChatListModel;
import com.valkryst.display.model.ChatModel;
import com.valkryst.display.model.PersonalityListModel;
import com.valkryst.gpt.Personality;
import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class HomeTabView extends View<HomeTabController> {
    private final JPanel listsPanel = new JPanel();

    @Getter  private final PersonalityListView personalityListView;

    private final JRootPane chatView = new JRootPane();

    /**
     * Constructs a new ChatPanelView.
     *
     * @param controller The controller for this view.
     */
    public HomeTabView(final HomeTabController controller) {
        super(controller);

        listsPanel.setLayout(new BorderLayout());

        chatView.setContentPane(new ChatModel(null).createView());

        personalityListView = createPersonalityListView();
        selectFirstItemInList(personalityListView.getList());
        listsPanel.add(personalityListView, BorderLayout.WEST);

        final var chatListView = createChatListView(personalityListView.getList().getSelectedValue());
        selectFirstItemInList(chatListView.getList());
        listsPanel.add(chatListView, BorderLayout.EAST);

        this.setLayout(new BorderLayout());
        this.add(listsPanel, BorderLayout.WEST);
        this.add(chatView, BorderLayout.CENTER);
    }

    /**
     * Selects the last item in the list.
     *
     * @param list The list.
     */
    private void selectFirstItemInList(final @NonNull JList<?> list) {
        final var totalItems = list.getModel().getSize();
        if (totalItems > 0) {
            list.setSelectedIndex(0);
        }
    }

    /**
     * Creates a new {@link ChatListView).
     *
     * @param personality The personality to create the chat list view for.
     *
     * @return The {@link ChatListView).
     */
    private ChatListView createChatListView(final Personality personality) {
        final var chatListView = new ChatListModel(personality).createView();
        chatListView.getList().addListSelectionListener(e -> {
            final var chat = chatListView.getList().getSelectedValue();
            chatView.setContentPane(new ChatModel(chat).createView());
            chatView.revalidate();
        });
        return chatListView;
    }

    /**
     * Creates a new {@link PersonalityListView).
     *
     * @return The {@link PersonalityListView).
     */
    private PersonalityListView createPersonalityListView() {
        final var personalityListView = new PersonalityListModel().createView();

        final var personalityList = personalityListView.getList();
        personalityList.setSelectedIndex(0);
        personalityList.addListSelectionListener(e -> {
            listsPanel.remove(1); // Remove the old chat list view.

            final var chatListView = createChatListView(personalityList.getSelectedValue());
            listsPanel.add(chatListView, BorderLayout.EAST);

            final var list = chatListView.getList();
            final var totalItems = list.getModel().getSize();
            if (totalItems > 0) {
                list.setSelectedIndex(0);
            } else {
                chatView.setContentPane(new ChatModel(null).createView());
            }

            this.revalidate();
        });

        return personalityListView;
    }
}
