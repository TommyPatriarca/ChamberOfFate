package com.cof.utils;

import javafx.scene.text.Font;

import java.io.InputStream;

/**
 * Rappresenta la tipologia di font utilizzata per la grafica del programma.
 */
public class FontUtils {

    private static Font loadFont(String path, double size) {
        try (InputStream fontStream = FontUtils.class.getResourceAsStream(path)) {
            if (fontStream != null) {
                return Font.loadFont(fontStream, size);
            } else {
                throw new RuntimeException("Font file not found: " + path);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load font: " + path, e);
        }
    }

    // Load custom fonts
    public static final Font PIXEL_HORROR = loadFont("/fonts/pixelHorror.ttf", 20);

    // Default fonts for fallback
    public static final String TITLE_FONT = "Arial Black";
    public static final String SUBTITLE_FONT = "Arial";
    public static final String BODY_FONT = "Verdana";

}
