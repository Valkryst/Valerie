package com.valkryst.Valerie.display.model;

import com.valkryst.VMVC.model.Model;
import com.valkryst.Valerie.display.controller.PersonalitiesTabController;
import com.valkryst.Valerie.display.view.PersonalitiesTabView;
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