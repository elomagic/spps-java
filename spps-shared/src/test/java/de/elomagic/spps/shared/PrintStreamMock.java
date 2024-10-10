package de.elomagic.spps.shared;

import jakarta.annotation.Nonnull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class PrintStreamMock extends PrintStream {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final PrintStream cache;

    public PrintStreamMock() {
        super(System.out, true);
        this.cache = System.out;
        System.setOut(this);
    }

    @Override
    public void write(@Nonnull byte[] buf, int off, int len) {
        buffer.write(buf, off, len);
        super.write(buf, off, len);
    }

    @Override
    public void write(int b) {
        buffer.write(b);
        super.write(b);
    }

    @Override
    public void close() {
        System.setOut(cache);
    }

    public void reset() {
        buffer.reset();
    }

    public String toString() {
        return buffer.toString(StandardCharsets.UTF_8);
    }

}
