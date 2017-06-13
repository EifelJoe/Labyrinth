package de.fhac.mazenet.server.config;

import de.fhac.mazenet.server.tools.DebugLevel;
import de.fhac.mazenet.server.userinterface.UI;
import de.fhac.mazenet.server.userinterface.betterUI.BetterUI;
import de.fhac.mazenet.server.userinterface.mazeFX.MazeFX;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class Settings {
    public static int PORT;
    public static int SSL_PORT;

    public static String SSL_CERT_STORE;
    public static String SSL_CERT_STORE_PASSWD;

    /**
     * Die maximal erlaubte Laenge des Loginnamens
     */
    public static int MAX_NAME_LENGTH;

    /**
     * Den Detailgrad der Ausgaben festlegen
     */
    public static DebugLevel DEBUGLEVEL;

    /**
     * Startwert fuer die Spieleranzahl Kann aber noch veraendert werden,
     * deshalb nicht final
     */
    public static int DEFAULT_PLAYERS;
    public static String IMAGEFILEEXTENSION;
    /**
     * Auf das angehaengte / achten
     */
    public static String IMAGEPATH;
    public static Locale LOCALE;
    /**
     * Die Zeit in Milisekunden, nach der ein Logintimeout eintritt LOGINTIMEOUT
     * = 60000 entspricht einer Minute
     */
    public static long LOGINTIMEOUT;
    public static int LOGINTRIES;
    /**
     * Die Zeit in Milisekunden, die die Animation eines Zug (die Bewegung des
     * Pins) benoetigen soll
     */
    public static int MOVEDELAY;
    /**
     * Die maximale Anzahl der Versuche einen gueltigen Zug zu uebermitteln
     */
    public static int MOVETRIES;
    public static long SENDTIMEOUT;
    /**
     * Die Zeit in Milisekunden, die das Einschieben der Shiftcard dauern soll
     */
    public static int SHIFTDELAY;
    /**
     * Wenn TESTBOARD = true ist, dann ist das Spielbrett bei jedem Start
     * identisch (zum Debugging)
     */
    public static boolean TESTBOARD;
    /**
     * Hiermit lassen sich die Testfaelle anpassen (Pseudozufallszahlen)
     */
    public static long TESTBOARD_SEED;
    /**
     * USERINTERFACE definiert die zu verwendende GUI Gueltige Werte:
     * BetterUI, MazeFX
     */
    public static UI USERINTERFACE;

    @SuppressWarnings("nls")
    public static void reload(String path) {
        Properties prop = new Properties();
        try {
            if (path != null) {
                InputStream propStream = new FileInputStream(new File(path));
                prop.load(propStream);
                propStream.close();
            }
        } catch (IOException e) {
            System.err.println("Pfad zum config nicht gefunden, verwende defaults");
        }
        MAX_NAME_LENGTH = Integer.parseInt(prop.getProperty("MAX_NAME_LENGTH", "30"));
        DEFAULT_PLAYERS = Integer.parseInt(prop.getProperty("DEFAULT_PLAYERS", "1"));
        IMAGEFILEEXTENSION = prop.getProperty("IMAGEFILEEXTENSION", ".png");
        IMAGEPATH = prop.getProperty("IMAGEPATH", "/images/");
        LOCALE = new Locale(prop.getProperty("LOCALE", "de"));
        LOGINTIMEOUT = Integer.parseInt(prop.getProperty("LOGINTIMEOUT", "120000"));
        LOGINTRIES = Integer.parseInt(prop.getProperty("LOGINTRIES", "3"));
        MOVEDELAY = Integer.parseInt(prop.getProperty("MOVEDELAY", "400"));
        MOVETRIES = Integer.parseInt(prop.getProperty("MOVETRIES", "3"));
        PORT = Integer.parseInt(prop.getProperty("PORT", "5123"));
        SSL_PORT = Integer.parseInt(prop.getProperty("SSL_PORT", "5432"));
        SSL_CERT_STORE = prop.getProperty("SSL_CERT_STORE", "maze-ssl.jks");
        SSL_CERT_STORE_PASSWD = prop.getProperty("SSL_CERT_STORE_PASSWD", "NukabWiod");
        SENDTIMEOUT = Integer.parseInt(prop.getProperty("SENDTIMEOUT", "20000"));
        SHIFTDELAY = Integer.parseInt(prop.getProperty("SHIFTDELAY", "700"));
        TESTBOARD = Boolean.parseBoolean(prop.getProperty("TESTBOARD", "true"));
        TESTBOARD_SEED = Integer.parseInt(prop.getProperty("TESTBOARD_SEED", "0"));
        DEBUGLEVEL = DebugLevel.DEFAULT;
        String ui = prop.getProperty("USERINTERFACE", "MazeFX");
        switch (ui) {
            case "BetterUI":
                USERINTERFACE = BetterUI.getInstance();
                break;
            case "MazeFX":
                USERINTERFACE = MazeFX.getInstance();
                break;
            default:
                USERINTERFACE = null;
                break;
        }
    }

    private Settings() {
    }

    @SuppressWarnings("nls")
    public static void print() {
        //TODO vervollstaendigen
        System.out.println("Imagepath: " + Settings.IMAGEPATH);
        System.out.println("Imageext: " + Settings.IMAGEFILEEXTENSION);

    }
}
