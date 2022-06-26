/*
 * Copyright (c) 2019 - 2020 AB Circle Limited. All rights reserved
 */

package com.nereus.craftbeer.util;

public class Hex
{
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHexString(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++)
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * converts a string containing hexadecimal characters to a byte-array
     *
     * @param s hexstring
     * @return a byte array with the corresponding value
     */
    public static byte[] stringToBytes(String s)
    {
        if (s.length() < 2)
        {
            return null;
        }

        byte[] res;
        char c;

        for (int i = 0; i < s.length(); i++)
        {
            c = s.charAt(i);
            if (!(((c >= '0') && (c <= '9')) ||
                  ((c >= 'a') && (c <= 'f')) ||
                  ((c >= 'A') && (c <= 'F'))))
            {
                s = s.substring(0, i) + s.substring(i + 1);
                i--;
            }
        }
        int qlen = s.length() / 2;
        int rlen = s.length() % 2;
        if (rlen != 0)
        {
            qlen++;
        }
        res = new byte[qlen];
        int pos = 0;
        if (rlen != 0)
        {
            res[0] = (byte) Integer.parseInt("0" + s.substring(0, 1), 16);
            pos = 1;
        }
        else
        {
            res[0] = (byte) Integer.parseInt(s.substring(0, 2), 16);
        }
        for (int i = 1; i < qlen; i++)
        {
            res[i] = (byte) Integer.parseInt(s.substring(2 * i - pos,
                                                         (2 * i - pos) + 2), 16);
        }
        return res;
    }

    /**
     * Converts the byte array to HEX string.
     *
     * @param buffer the buffer
     * @return the HEX string
     */
    public static String toHexString(byte[] buffer) {

        /* Check the parameter. */
        if (buffer == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder(3 * buffer.length);

        /* For each byte, convert it to HEX digit. */
        for (int i = 0; i < buffer.length; i++) {

            int tmp = buffer[i] & 0xFF;

            if (i != 0) {
                builder.append(" ");
            }

            builder.append(HEX_ARRAY[tmp >>> 4]);
            builder.append(HEX_ARRAY[tmp & 0x0F]);
        }

        return builder.toString();
    }

}
