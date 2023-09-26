package com.valkryst.Valerie.display.component;

import lombok.NonNull;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/** A button that uses a {@link FontIcon} as its icon, and which automatically resizes the icon to fit the button. */
public class JIconButton extends JButton {
    /**
     * Constructs a new {@code JIconButton}.
     *
     * @param icon
     *         The icon to use.
     */
    public JIconButton(final @NonNull FontIcon icon) {
        super(icon);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                var icon = (FontIcon) JIconButton.super.getIcon();
                icon = FontIcon.of(
                    icon.getIkon(),
                    (int) (Math.min(getWidth(), getHeight()) * 0.8f),
                    isEnabled() ? icon.getIconColor() : icon.getIconColor().darker()
                );

                JIconButton.super.setIcon(icon);
            }
        });
    }
}
