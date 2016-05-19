package edu.neumont.csc360.btree.utils;

public class ByteUtils {
    public static void writeBytesFromIntToByteArray(int integer, int[] destArray, int start) {
        int[] intBytes = new int[] {
                (integer & 0xFF_00_00_00) >> 23,
                (integer & 0x00_FF_00_00) >> 15,
                (integer & 0x00_00_FF_00) >> 7,
                (integer & 0x00_00_00_FF)
        };
        System.arraycopy(intBytes, 0, destArray, start, intBytes.length);
    }

    public static int[] bytesFromInt(int integer) {
        int[] intBytes = new int[4];
        writeBytesFromIntToByteArray(integer, intBytes, 0);
        return intBytes;
    }

    public static int intFromBytesInByteArray(int[] srcArray, int start) {
        int byte0 = srcArray[start];
        int byte1 = srcArray[start + 1];
        int byte2 = srcArray[start + 2];
        int byte3 = srcArray[start + 3];
        int byte0Shifted = byte0 << 23;
        int byte1Shifted = byte1 << 15;
        int byte2Shifted = byte2 << 7;
        int byte3Shifted = byte3;
        return byte0Shifted + byte1Shifted + byte2Shifted + byte3Shifted;
    }

    public static int intFromBytes(int[] bytes) {
        return intFromBytesInByteArray(bytes, 0);
    }

    public static boolean booleanFromByteInByteArray(int[] srcArray, int index) {
        return srcArray[index] != 0;
    }

    public static void writeBooleanToByteArray(boolean isLeaf, int[] destArray, int start) {
        destArray[start] = isLeaf ? 1 : 0;
    }
}
