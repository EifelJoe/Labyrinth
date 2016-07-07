package de.fhaachen.mazenet.server.userInterface.mazeFX.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Created by Richard Zameitat on 26.05.2016.
 */
public class BetterOutputStream extends ByteArrayOutputStream {
    private Consumer<String> cons;

    public BetterOutputStream(Consumer<String> strC){
        this.cons = strC;
    }

    @Override
    public void write(byte[] ba){
        cons.accept(new String(ba));
    }
}
