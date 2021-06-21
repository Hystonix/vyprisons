package com.glitchedturtle.common.util;

public class TAssert {

    public static void assertTrue(boolean bool, String message) {

        if(bool)
            return;

        System.err.println("[Assert] Assertion failed: " + message);
        throw new AssertionError("Assert failed: " + message);

    }

    public static void assertFalse(boolean bool, String message) {
        TAssert.assertTrue(!bool, message);
    }

}
