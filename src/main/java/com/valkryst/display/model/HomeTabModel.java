package com.valkryst.display.model;

import com.valkryst.VMVC.model.Model;
import com.valkryst.display.controller.HomeTabController;
import com.valkryst.display.view.HomeTabView;

public class HomeTabModel extends Model<HomeTabController, HomeTabView> {
    @Override
    protected HomeTabController createController() {
        return new HomeTabController(this);
    }

    @Override
    protected HomeTabView createView(HomeTabController controller) {
        return new HomeTabView(controller);
    }
}
