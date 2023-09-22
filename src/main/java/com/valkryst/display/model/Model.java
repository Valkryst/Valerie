package com.valkryst.display.model;

import com.valkryst.display.controller.Controller;
import com.valkryst.display.view.View;

/**
 * A model, or data container.
 *
 * @param <C> The type of controller associated with this model.
 * @param <V> The type of view associated with this model.
 */
public abstract class Model<C extends Controller<?>, V extends View<?>> {
	/**
	 * Constructs a controller for the {@code View}.
	 *
	 * @return The controller.
	 */
	protected abstract C createController();

	/**
	 * Constructs a {@code View} of this {@code Model}.
	 *
	 * @return The view.
	 */
	public V createView() {
		return createView(createController());
	}

	/**
	 * Constructs a {@code View} of this model.
	 *
	 * @param controller A controller for the view.
	 * @return The view.
	 */
	protected abstract V createView(final C controller);
}
