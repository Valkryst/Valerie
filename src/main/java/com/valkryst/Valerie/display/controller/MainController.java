package com.valkryst.Valerie.display.controller;

import com.valkryst.VMVC.controller.Controller;
import com.valkryst.Valerie.display.model.MainModel;
import lombok.NonNull;

public class MainController extends Controller<MainModel> {
    /**
     * Constructs a new {@code MainController}.
     *
     * @param model The model for this controller.
     */
    public MainController(final @NonNull MainModel model) {
        super(model);
    }
}
