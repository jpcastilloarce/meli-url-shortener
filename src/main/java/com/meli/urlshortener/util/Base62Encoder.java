package com.meli.urlshortener.util;

public class Base62Encoder {
    private static final String BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BASE = BASE62.length();

    public static String encode(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(BASE62.charAt((int) (value % BASE)));
            value /= BASE;
        }
        return sb.reverse().toString();
    }

    public static long decode(String str) {
        long value = 0;
        for (int i = 0; i < str.length(); i++) {
            value = value * BASE + BASE62.indexOf(str.charAt(i));
        }
        return value;
    }
}