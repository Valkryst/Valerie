package com.valkryst.Valerie.display.component;

import com.valkryst.VCodeLanguageDetection.LanguageDetector;
import lombok.Getter;
import lombok.NonNull;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;

public class CodeTextArea extends RSyntaxTextArea {
    private static final Theme theme = loadTheme();

    @Getter private final String syntaxStyle;

    public CodeTextArea(final @NonNull String text) {
        this.setAutoIndentEnabled(true);
        this.setBracketMatchingEnabled(true);
        this.setClearWhitespaceLinesEnabled(true);
        this.setCodeFoldingEnabled(true);
        this.setEditable(false);
        this.setHighlightCurrentLine(false);
        this.setHyperlinksEnabled(true);
        this.setSyntaxEditingStyle(syntaxStyle = detectSyntaxStyle(text));
        this.setText(text);

        if (theme != null) {
            theme.apply(this);
        }
    }

    private static Theme loadTheme() {
        final var url = CodeTextArea.class.getClassLoader().getResource("DraculaRSyntaxTheme.xml");

        if (url == null) {
            return null;
        }

        try (
            final var inputStream = url.openStream();
        ) {
            return Theme.load(inputStream);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String detectSyntaxStyle(final @NonNull String code) {
        final var languages = LanguageDetector.getInstance().detect(code);
        final var entry = languages.entrySet().iterator().next();

        if (entry.getValue() == 0) {
            return RSyntaxTextArea.SYNTAX_STYLE_NONE;
        }

        try {
            final var styleName = "SYNTAX_STYLE_" + entry.getKey().toUpperCase();
            final var field = SyntaxConstants.class.getDeclaredField(styleName);
            return (String) field.get(null);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            return RSyntaxTextArea.SYNTAX_STYLE_NONE;
        }
    }
}
