package com.valkryst.Valerie.display.view;

import com.valkryst.VMVC.view.View;
import com.valkryst.Valerie.display.component.CodeTextArea;
import com.valkryst.Valerie.display.controller.MessageController;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jsoup.Jsoup;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class MessageView extends View<MessageController> {
    private static final Font FONT = new Font("Lucidia Sans", Font.PLAIN, 16);
    private static final Font CODE_FONT = new Font("Lucida Sans Typewriter", Font.PLAIN, 12);

    private static final int MAX_WIDTH = 800;

    /**
     * Constructs a new {@code MessageView}.
     *
     * @param controller The {@code Controller} associated with this view.
     */
    public MessageView(final MessageController controller) {
        super(controller);

        this.setLayout(new MigLayout("fillx, flowy", "[fill, align center]"));
        this.setMinimumSize(super.getMinimumSize());
        this.setOpaque(true);

        final var document = Jsoup.parse(controller.getMessageContent());
        for (final var element : document.body().children()) {
            switch (element.tagName()) {
                case "ol", "ul" -> this.add(createList(element.outerHtml()), "wmax " + MAX_WIDTH);
                case "p" -> this.add(createParagraph(element.text()), "wmax " + MAX_WIDTH);
                case "pre" -> this.add(createCodeBlock(element.text()), "wmax " + MAX_WIDTH);
            }
        }
    }

    private JPanel createCodeBlock(final String text) {
        final var codeArea = new CodeTextArea(text);
        codeArea.setFont(CODE_FONT);

        final var scrollPane = new RTextScrollPane(codeArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        final var copyButton = new JButton("Copy");
        copyButton.addActionListener(e -> {
            final var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(codeArea.getText()), null);

            copyButton.setText("Copied!");
            new Timer(1000, e2 -> copyButton.setText("Copy")).start();
        });

        final var bottomPanel = new JPanel();
        bottomPanel.setBackground(codeArea.getBackground());
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(new JLabel(codeArea.getSyntaxStyle()));
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(copyButton);

        final var panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JTextArea createParagraph(final String text) {
        final var textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(FONT);
        textArea.setLineWrap(true);
        textArea.setOpaque(false);
        textArea.setWrapStyleWord(true);
        textArea.setText(text);
        return textArea;
    }

    private JEditorPane createList(final String html) {
        final var kit = new HTMLEditorKit();
        final var styleSheet = kit.getStyleSheet();
        styleSheet.addRule("ol, ul { margin-left: 12px; }");
        styleSheet.addRule("li { padding-top: 6px; padding-bottom: 6px; }");

        final var editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setFont(FONT);
        editorPane.setEditorKit(kit);
        editorPane.setText(html);
        return editorPane;
    }
}
