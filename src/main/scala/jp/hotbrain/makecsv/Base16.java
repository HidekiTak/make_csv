package jp.hotbrain.makecsv;

import java.nio.charset.StandardCharsets;

public class Base16 {

    public final static char[] HEX_DEC = new char[]{'0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getDump(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2 + 2);
        for (byte b : data) {
            getHex(sb, b >> 4);
            getHex(sb, b);
        }
        return sb.toString();
    }

    private static void getHex(StringBuilder sb, int hex) {
        sb.append(HEX_DEC[hex & 0xF]);
    }

    public final static byte[] BASE16 = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15};
    private final static byte[] NullBytes = new byte[0];

    public static byte[] toByteArray(String hexString) {
        if (null == hexString || hexString.isEmpty())
            return NullBytes;
        if (0 != (hexString.length() & 1)) {
            throw new IllegalArgumentException("String.length must be even \""
                    + hexString + "\"");
        }
        int len = hexString.length() / 2;
        byte[] result = new byte[len];

        int index = 0;
        int v = 0;
        boolean odd = true;
        byte[] bytes = hexString.getBytes(StandardCharsets.US_ASCII);
        for (byte bx : bytes) {
            int b = bx - '0';
            if (0 <= b && b < BASE16.length) {
                b = BASE16[b];
            }
            if (0 <= b && b < 16) {
                if (odd) {
                    v = b * 16;
                } else {
                    result[index] = (byte) (v | b);
                    v = 0;
                    index = index + 1;
                }
                odd = !odd;
            } else {
                throw new IllegalArgumentException("String must be 0-9,a-f");
            }
        }
        return result;
    }
}
