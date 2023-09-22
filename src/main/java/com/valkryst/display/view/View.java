package com.valkryst.display.view;

import com.valkryst.display.controller.Controller;
import com.valkryst.display.model.Model;
import lombok.NonNull;

import javax.swing.*;

/**
 * A view of a {@link Model}.
 *
 * @param <C> The type of controller.
 */
public abstract class View<C extends Controller<?>> extends JPanel {
	/** The controller. */
	final C controller;

	/**
	 * Constructs a new {@code View}.
	 *
	 * @param controller The controller associated with this view.
	 */
	public View(final @NonNull C controller) {
		this.controller = controller;
	}
}
