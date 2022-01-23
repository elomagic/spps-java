package de.elomagic.spps.shared;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class PrintStreamMock extends PrintStream {

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private PrintStream cache;

    public PrintStreamMock() {
        super(System.out, true);
        this.cache = System.out;
        System.setOut(this);
    }

    @Override
    public void write(@NotNull byte[] buf, int off, int len) {
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
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }

}
