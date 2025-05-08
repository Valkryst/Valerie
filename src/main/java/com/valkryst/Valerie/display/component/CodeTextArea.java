package com.valkryst.Valerie.display.component;

import com.jthemedetecor.OsThemeDetector;
import com.valkryst.VCodeLanguageDetection.LanguageDetector;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;

import javax.swing.*;
import java.io.IOException;

@Slf4j
public class CodeTextArea extends RSyntaxTextArea {
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

        final var themeDetector = OsThemeDetector.getDetector();
        this.setTheme(themeDetector.isDark());
        themeDetector.registerListener(isDark -> SwingUtilities.invokeLater(() -> setTheme(isDark)));
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

    private void setTheme(final boolean isDark) {
        final String path = "org/fife/ui/rsyntaxtextarea/themes/" + (isDark ? "dark" : "default") + ".xml";

        try {
            // The load function will close the stream after loading the theme.
            final var stream = CodeTextArea.class.getClassLoader().getResourceAsStream(path);
            Theme.load(stream).apply(this);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
