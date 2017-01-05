package com.github.t1.testtools;

import org.junit.Test;

import static com.github.t1.testtools.TestLoggerRule.*;
import static org.junit.Assert.*;

public class CamelToSpacesTest {
    @Test
    public void shouldKeepAllLowercase() throws Exception {
        assertEquals("abc", camelToSpaces("abc"));
    }

    @Test
    public void shouldLowerInitialUpper() throws Exception {
        assertEquals("abc", camelToSpaces("Abc"));
    }

    @Test
    public void shouldSpaceInnerUpper() throws Exception {
        assertEquals("a bc", camelToSpaces("aBc"));
    }

    @Test
    public void shouldSpaceLastUpper() throws Exception {
        assertEquals("ab c", camelToSpaces("abC"));
    }

    @Test
    public void shouldNotSpaceTwoUpper() throws Exception {
        assertEquals("a bc", camelToSpaces("aBC"));
    }

    @Test
    public void shouldNotSpaceAllUpper() throws Exception {
        assertEquals("abc", camelToSpaces("ABC"));
    }

    @Test
    public void shouldSpaceInitialDigit() throws Exception {
        assertEquals("1 bc", camelToSpaces("1bc"));
    }

    @Test
    public void shouldSpaceInnerDigit() throws Exception {
        assertEquals("a 1 c", camelToSpaces("a1c"));
    }

    @Test
    public void shouldSpaceLastDigit() throws Exception {
        assertEquals("ab 1", camelToSpaces("ab1"));
    }

    @Test
    public void shouldSpaceFirstDigitFollowedByUpper() throws Exception {
        assertEquals("1 ab", camelToSpaces("1Ab"));
    }

    @Test
    public void shouldSpaceInnerDigitFollowedByUpper() throws Exception {
        assertEquals("a 1 c", camelToSpaces("a1C"));
    }
}
