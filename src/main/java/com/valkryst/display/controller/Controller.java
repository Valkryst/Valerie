package com.valkryst.display.controller;

import com.valkryst.display.Display;
import com.valkryst.display.model.Model;
import lombok.NonNull;

import java.lang.reflect.InvocationTargetException;

/**
 * A controller of a {@link Model}.
 *
 * @param <M> The type of model associated with the controller.
 */
public abstract class Controller<M extends Model<?, ?>> {
	/** The model. */
	final M model;

	/**
	 * Constructs a new {@code Controller}.
	 *
	 * @param model The model associated with this controller.
	 */
	public Controller(final @NonNull M model) {
		this.model = model;
	}

	public void setContentPane(final Class<? extends Model> modelClass, final Object ... modelArgs) {
		// Handle models using the singleton pattern.
		try {
			final var method = modelClass.getDeclaredMethod("getInstance");
			final var model = modelClass.cast(method.invoke(null, modelArgs));
			final var view = model.createView();
			Display.getInstance().setContentPane(view);
			return;
		} catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}

		// Handle models using a public constructor.
		try {
			final Class<?>[] modelArgTypes = new Class[modelArgs.length];
			for (int i = 0 ; i < modelArgs.length ; i++) {
				modelArgTypes[i] = modelArgs[i].getClass();
			}

			final var model = modelClass.getDeclaredConstructor(modelArgTypes).newInstance(modelArgs);
			final var view = model.createView();
			Display.getInstance().setContentPane(view);
		} catch (final NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
