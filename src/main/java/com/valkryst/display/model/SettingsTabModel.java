package com.valkryst.display.model;

import com.valkryst.display.controller.SettingsTabController;
import com.valkryst.display.view.SettingsTabView;
import lombok.NonNull;

public class SettingsTabModel extends Model<SettingsTabController, SettingsTabView> {
    @Override
    protected SettingsTabController createController() {
        return new SettingsTabController(this);
    }

    @Override
    protected SettingsTabView createView(final @NonNull SettingsTabController controller) {
        return new SettingsTabView(controller);
    }
}
