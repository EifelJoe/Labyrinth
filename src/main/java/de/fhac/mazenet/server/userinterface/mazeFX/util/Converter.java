package de.fhac.mazenet.server.userinterface.mazeFX.util;

import javafx.scene.paint.Color;

/**
 * Collection of methods which convert things into other things.
 *
 * @author Richard Zameitat
 */
public class Converter {

    /**
     * Private constructor to prevent instantiation of this class
     */
    private Converter() {}

    /**
     * Converts color objects into web-usable rgb hex strings
     *
     * @param c Color to convert
     * @return  RGB hex string, e.g. #00ff42
     */
    public static String colorToRgbHex(Color c){
        int r = (int)Math.round(c.getRed() * 255);
        int g = (int)Math.round(c.getGreen() * 255);
        int b = (int)Math.round(c.getBlue() * 255);
        return String.format("#%02x%02x%02x" , r, g, b);
    }
}
