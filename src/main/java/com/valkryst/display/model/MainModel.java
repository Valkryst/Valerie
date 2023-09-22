package com.valkryst.display.model;

import com.valkryst.display.controller.MainController;
import com.valkryst.display.view.MainView;
import lombok.NonNull;

public class MainModel extends Model<MainController, MainView> {
    @Override
    protected MainController createController() {
        return new MainController(this);
    }

    @Override
    protected MainView createView(final @NonNull MainController controller) {
        return new MainView(controller);
    }
}
