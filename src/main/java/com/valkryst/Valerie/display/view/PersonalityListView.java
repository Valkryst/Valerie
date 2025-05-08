package com.valkryst.Valerie.display.view;

import com.jthemedetecor.OsThemeDetector;
import com.valkryst.JIconButton.JIconButton;
import com.valkryst.VMVC.view.View;
import com.valkryst.Valerie.display.Display;
import com.valkryst.Valerie.display.controller.PersonalityListController;
import com.valkryst.Valerie.gpt.Chat;
import com.valkryst.Valerie.gpt.Personality;
import lombok.Getter;
import lombok.NonNull;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;

public class PersonalityListView extends View<PersonalityListController> {
    private static final Dimension MAX_VIEW_SIZE = new Dimension(200, Integer.MAX_VALUE);

    private static final Dimension MIN_BUTTON_SIZE = new Dimension(25, 25);

    @Getter private final JList<Personality> list;

    /**
     * Constructs a new {@code PersonalityListView}.
     *
     * @param controller The controller associated with this view.
     */
    public PersonalityListView(final PersonalityListController controller) {
        super(controller);

        final var creationButton = newCreationButton(controller);
        final var deletionButton = newDeletionButton(controller);
        final var editButton = newEditButton(controller);

        list = new JList<>(controller.getPersonalities());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            final var selectedPersonality = list.getSelectedValue();
            controller.setSelectedPersonality(selectedPersonality);

            if (selectedPersonality == null) {
                deletionButton.setEnabled(false);
                editButton.setEnabled(false);
            } else {
                deletionButton.setEnabled(true);
                editButton.setEnabled(true);
            }
        });

        final var buttonPanel = new JPanel(new GridLayout(0, 3));
        buttonPanel.add(creationButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deletionButton);

        this.setPreferredSize(new Dimension(160, Integer.MAX_VALUE));
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder("Personalities List"));
        this.add(new JScrollPane(list), BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a button to create a {@link Personality}.
     *
     * @param controller The {@link PersonalityListController}.
     *
     * @return The button.
     */
    private JIconButton newCreationButton(final PersonalityListController controller) {
        final var button = new JIconButton(FontIcon.of(MaterialDesignP.PLUS, UIManager.getColor("Button.foreground")));
        button.setMinimumSize(MIN_BUTTON_SIZE);
        button.setPreferredSize(MIN_BUTTON_SIZE);

        button.setToolTipText("Create a new Personality.");

        button.addActionListener(e -> {
            final var chatName = showCreationDialog();
            controller.createPersonality(chatName);
            list.setSelectedIndex(list.getModel().getSize() - 1);
        });

        OsThemeDetector.getDetector().registerListener(isDark -> SwingUtilities.invokeLater(() -> {
            ((FontIcon) button.getIcon()).setIconColor(isDark ? Color.WHITE : Color.BLACK);
        }));
        return button;
    }

    /**
     * Creates a button to delete a {@link Personality}.
     *
     * @param controller The {@link PersonalityListController}.
     *
     * @return The button.
     */
    private JIconButton newDeletionButton(final PersonalityListController controller) {
        final var button = new JIconButton(FontIcon.of(MaterialDesignM.MINUS, UIManager.getColor("Button.foreground")));
        button.setMinimumSize(MIN_BUTTON_SIZE);
        button.setPreferredSize(MIN_BUTTON_SIZE);

        button.setToolTipText("Delete the currently selected Personality.");

        button.addActionListener(e -> {
            final var personality = controller.getSelectedPersonality();
            if (controller.getPersonalities().size() == 1) {
                Display.displayWarning(null, "You must always have at least one Personality.");
                return;
            }

            final var option = showDeletionDialog(personality);
            if (option == JOptionPane.YES_OPTION) {
                controller.deletePersonality(personality);
                list.setSelectedIndex(list.getModel().getSize() - 1);
            }
        });

        OsThemeDetector.getDetector().registerListener(isDark -> SwingUtilities.invokeLater(() -> {
            ((FontIcon) button.getIcon()).setIconColor(isDark ? Color.WHITE : Color.BLACK);
        }));
        return button;
    }

    /**
     * Creates a button to edit a {@link Personality}.
     *
     * @param controller The {@link PersonalityListController}.
     *
     * @return The button.
     */
    private JIconButton newEditButton(final PersonalityListController controller) {
        final var button = new JIconButton(FontIcon.of(MaterialDesignP.PENCIL, UIManager.getColor("Button.foreground")));
        button.setMinimumSize(MIN_BUTTON_SIZE);
        button.setPreferredSize(MIN_BUTTON_SIZE);

        button.setToolTipText("Edit the name of the currently selected chat.");

        button.addActionListener(e -> {
            // todo Switch to Personalities tab, and ensure this personality is selected there.
        });

        OsThemeDetector.getDetector().registerListener(isDark -> SwingUtilities.invokeLater(() -> {
            ((FontIcon) button.getIcon()).setIconColor(isDark ? Color.WHITE : Color.BLACK);
        }));
        return button;
    }

    /**
     * Shows a dialog to create a {@link Chat}.
     *
     * @return The name of the {@link Chat}.
     */
    private String showCreationDialog() {
        return (String) JOptionPane.showInputDialog(
            this,
            "Create Personality",
            "Enter Name:",
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            ""
        );
    }

    /**
     * Shows a dialog to confirm the deletion of a {@link Personality}.
     *
     * @param chat The {@link Personality} to delete.
     *
     * @return The option selected by the user.
     */
    private int showDeletionDialog(final @NonNull Personality chat) {
        return JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete the Personality '" + chat.getName() + "'?",
            "Delete Personality",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
    }
}
