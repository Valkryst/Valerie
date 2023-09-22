package com.valkryst.display.controller;

import com.valkryst.display.Display;
import com.valkryst.display.model.PersonalityListModel;
import com.valkryst.gpt.Personality;
import lombok.NonNull;

import javax.swing.*;
import java.io.IOException;

public class PersonalityListController extends Controller<PersonalityListModel> {
    /**
     * Constructs a new {@code PersonalityListController}.
     *
     * @param model The model associated with this controller.
     */
    public PersonalityListController(final PersonalityListModel model) {
        super(model);
    }

    /**
     * Creates a new {@link Personality} with the given name.
     *
     * @param name The name of the {@link Personality} to create.
     */
    public void createPersonality(final String name) {
        if (name == null || name.isEmpty()) {
            return;
        }

        final var personality = new Personality(name);
        super.model.getPersonalities().addElement(personality);

        try {
            personality.save();
        } catch (final IOException e) {
            Display.displayError(null, e);
        }
    }

    /**
     * Deletes the specified {@link Personality}.
     *
     * @param personality The {@link Personality} to delete.
     */
    public void deletePersonality(final @NonNull Personality personality) {
        try {
            personality.delete();
            super.model.getPersonalities().removeElement(personality);
        } catch (final IOException e) {
            Display.displayError(null, e);
        }
    }

    /**
     * Returns the list of personalities.
     *
     * @return The list of personalities.
     */
    public DefaultListModel<Personality> getPersonalities() {
        return super.model.getPersonalities();
    }

    /**
     * Returns the selected {@link Personality}.
     *
     * @return The selected {@link Personality}.
     */
    public Personality getSelectedPersonality() {
        return super.model.getSelectedPersonality();
    }

    /**
     * Sets the selected {@link  Personality}.
     *
     * @param personality The {@link  Personality} to select.
     */
    public void setSelectedPersonality(final Personality personality) {
        super.model.setSelectedPersonality(personality);
    }
}
