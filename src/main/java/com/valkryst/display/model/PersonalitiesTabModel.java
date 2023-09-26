package com.valkryst.display.model;

import com.valkryst.VMVC.model.Model;
import com.valkryst.display.controller.PersonalitiesTabController;
import com.valkryst.display.view.PersonalitiesTabView;
import lombok.NonNull;

public class PersonalitiesTabModel extends Model<PersonalitiesTabController, PersonalitiesTabView> {
    @Override
    protected PersonalitiesTabController createController() {
        return new PersonalitiesTabController(this);
    }

    @Override
    protected PersonalitiesTabView createView(final @NonNull  PersonalitiesTabController controller) {
        return new PersonalitiesTabView(controller);
    }
}