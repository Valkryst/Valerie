package com.valkryst.display.view;

import com.valkryst.VMVC.view.View;
import com.valkryst.display.controller.PersonalitiesTabController;
import com.valkryst.display.model.PersonalityListModel;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class PersonalitiesTabView extends View<PersonalitiesTabController> {
    private final JPanel settingsPanel = new JPanel();

    /**
     * Constructs a new {@code PersonalitiesTabView}.
     *
     * @param controller The controller for this view.
     */
    public PersonalitiesTabView(final @NonNull PersonalitiesTabController controller) {
        super(controller);

        this.setLayout(new BorderLayout());
        this.add(createPersonalityListView(), BorderLayout.WEST);
        this.add(settingsPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a new {@link PersonalityListView).
     *
     * @return The {@link PersonalityListView).
     */
    private PersonalityListView createPersonalityListView() {
        final var personalityListView = new PersonalityListModel().createView();

        final var personalityList = personalityListView.getList();
        personalityList.addListSelectionListener(e -> {
            settingsPanel.setBorder(BorderFactory.createTitledBorder(personalityList.getSelectedValue().getName() + "'s Personality"));
        });

        personalityList.setSelectedIndex(0);
        return personalityListView;
    }
}