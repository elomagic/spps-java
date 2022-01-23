package de.elomagic.spps.shared;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UTF8Test {

    @Test
    void testToByteArray() {

        assertNull(UTF8.toCharArray(null));

        String value = "hello,world\nNǐ hǎo,shìjiè\n你好,世界\n";
        char[] valueArray = value.toCharArray();
        char[] nullArray = new char[valueArray.length];
        Arrays.fill(nullArray, '*');

        byte[] bytes = UTF8.toByteArray(valueArray);

        assertEquals(value, new String(bytes, StandardCharsets.UTF_8));
        assertArrayEquals(nullArray, valueArray);

    }

    @Test
    void testToCharArray() {

        assertNull(UTF8.toCharArray(null));

        String value = "hello,world\nNǐ hǎo,shìjiè\n你好,世界\n";
        byte[] valueArray = value.getBytes(StandardCharsets.UTF_8);
        byte[] nullArray = new byte[valueArray.length];
        Arrays.fill(nullArray, (byte)0);

        char[] chars = UTF8.toCharArray(valueArray);

        assertEquals(value, new String(chars));
        assertArrayEquals(nullArray, valueArray);

    }

}