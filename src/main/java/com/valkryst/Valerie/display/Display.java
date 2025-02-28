package com.valkryst.Valerie.display;

import com.valkryst.VMVC.view.View;
import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Display {
    /** Singleton instance. */
    private final static Display INSTANCE = new Display();

    @Getter private final JFrame frame = new JFrame();

    /** Constructs a new Display. */
    private Display() {
        frame.setBackground(Color.BLACK);
        frame.setTitle("Valerie");
        frame.setIgnoreRepaint(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });
        frame.setMinimumSize(new Dimension(900, 500));
        frame.pack();
        frame.setLocationRelativeTo(null); // Must be called after pack()
    }

	public void setContentPane(final @NonNull View view) {
		frame.setContentPane(view);
		frame.revalidate();

        if (!frame.isVisible()) {
            frame.setVisible(true);
        }

		view.requestFocusInWindow();
	}

    /**
     * Retrieves the singleton instance.
     *
     * @return
     *          The singleton instance.
     */
    public static Display getInstance() {
        return INSTANCE;
    }

    /**
     * Displays a {@link JOptionPane#ERROR_MESSAGE} message.
     *
     * @param dialogParent Parent {@link Component} of the {@link JOptionPane}.
     *
     * @param throwable The throwable.
     */
    public static void displayError(final Component dialogParent, final Throwable throwable) {
        final var stringBuilder = new StringBuilder(throwable.getMessage());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(System.lineSeparator());

        for (final var element : throwable.getStackTrace()) {
            stringBuilder.append(element.toString());
            stringBuilder.append(System.lineSeparator());
        }

        final var textArea = new JTextArea(20, 60);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setText(stringBuilder.toString());

        JOptionPane.showMessageDialog(
            dialogParent == null ? INSTANCE.getFrame() : dialogParent,
            new JScrollPane(textArea),
            "An Error Occurred",
            JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Displays a {@link JOptionPane#WARNING_MESSAGE}.
     *
     * @param dialogParent Parent {@link Component} of the {@link JOptionPane}.
     *
     * @param message The message.
     */
    public static void displayWarning(final Component dialogParent, final String message) {
        JOptionPane.showMessageDialog(
            dialogParent == null ? INSTANCE.getFrame() : dialogParent,
            message,
            "Warning",
            JOptionPane.WARNING_MESSAGE
        );
    }
}
