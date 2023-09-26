package com.valkryst.Valerie.display.model;

import com.valkryst.VMVC.model.Model;
import com.valkryst.Valerie.display.controller.SettingsTabController;
import com.valkryst.Valerie.display.view.SettingsTabView;
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
