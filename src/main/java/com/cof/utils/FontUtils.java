package com.cof.utils;

import javafx.scene.text.Font;

import java.io.InputStream;

/**
 * Rappresenta la tipologia di font utilizzata per la grafica del programma.
 */
public class FontUtils {
    private InputStream fontStream = getClass().getResourceAsStream("/fonts/LuckiestGuy-Regular.ttf");
    private Font font;

    public FontUtils(int size) {
        font = Font.loadFont(fontStream, size);
    }

    /**
     * @return la tipologia di font.
     */
    public Font getFont() {
        return font;
    }
}
