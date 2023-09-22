package com.valkryst.display.controller;

import com.valkryst.display.model.SettingsTabModel;
import lombok.NonNull;

public class SettingsTabController extends Controller<SettingsTabModel> {
    /**
     * Constructs a new {@code SettingsTabController}.
     *
     * @param model The model associated with this controller.
     */
    public SettingsTabController(@NonNull SettingsTabModel model) {
        super(model);
    }
}
