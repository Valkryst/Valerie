package com.valkryst.display.controller;

import com.valkryst.display.model.HomeTabModel;
import lombok.NonNull;

public class HomeTabController extends Controller<HomeTabModel> {
    /**
     * Constructs a new ChatPanelController.
     *
     * @param model The model for this controller.
     */
    public HomeTabController(final @NonNull HomeTabModel model) {
        super(model);
    }
}