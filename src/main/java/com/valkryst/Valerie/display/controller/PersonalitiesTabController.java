package com.valkryst.Valerie.display.controller;

import com.valkryst.VMVC.controller.Controller;
import com.valkryst.Valerie.display.model.PersonalitiesTabModel;
import lombok.NonNull;

public class PersonalitiesTabController extends Controller<PersonalitiesTabModel> {
    /**
     * Constructs a new {@code PersonalitiesTabController}.
     *
     * @param model The model for this controller.
     */
    public PersonalitiesTabController(final @NonNull PersonalitiesTabModel model) {
        super(model);
    }
}