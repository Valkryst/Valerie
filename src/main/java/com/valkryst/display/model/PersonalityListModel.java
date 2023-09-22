package com.valkryst.display.model;

import com.valkryst.display.Display;
import com.valkryst.display.controller.PersonalityListController;
import com.valkryst.display.view.PersonalityListView;
import com.valkryst.gpt.Personality;
import com.valkryst.io.FolderIO;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.IOException;

public class PersonalityListModel extends Model<PersonalityListController, PersonalityListView> {
    /** The list of personalities. */
    @Getter
    private final DefaultListModel<Personality> personalities = new DefaultListModel<>();

    /** The currently selected personality. */
    @Getter @Setter private Personality selectedPersonality = null;

    /** Constructs a new {@code PersonalityListModel}. */
    public PersonalityListModel() {
        loadPersonalities();

        if (personalities.isEmpty()) {
            final var personality = new Personality("Valerie");
            personalities.addElement(personality);
            selectedPersonality = personality;

            try {
                personality.save();
            } catch (final IOException e) {
                Display.displayError(null, e);
            }
        }
    }

    @Override
    protected PersonalityListController createController() {
        return new PersonalityListController(this);
    }

    @Override
    protected PersonalityListView createView(PersonalityListController controller) {
        return new PersonalityListView(controller);
    }

    /** Loads existing personalities from disk. */
    private void loadPersonalities() {
        final var folderPath = FolderIO.getFolderPathForType(Personality.class);
        final var personalityPaths = FolderIO.getAllFilesInFolderWithExtension(folderPath, "json");

        for (final var path : personalityPaths) {
            try {
                personalities.addElement(new Personality(path));
            } catch (final IOException e) {
                System.err.println("Failed to load personality: " + path);
                e.printStackTrace();
            }
        }

        selectedPersonality = personalities.isEmpty() ? null : personalities.get(0);
    }
}
