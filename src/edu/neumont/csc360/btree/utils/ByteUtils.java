package edu.neumont.csc360.btree.utils;

public class ByteUtils {
    public static int[] bytesFromInt(int value) {
        int byte0Mask = 0xFF_00_00_00;
        int byte1Mask = 0x00_FF_00_00;
        int byte2Mask = 0x00_00_FF_00;
        int byte3Mask = 0x00_00_00_FF;
        int byte0 = (value & byte0Mask) >> 23;
        int byte1 = (value & byte1Mask) >> 15;
        int byte2 = (value & byte2Mask) >> 7;
        int byte3 = (value & byte3Mask) >> 0;
        return new int[] { byte0, byte1, byte2, byte3 };
    }

    public static int intFromBytes(int[] bytes) {
        int byte0 = bytes[0];
        int byte1 = bytes[1];
        int byte2 = bytes[2];
        int byte3 = bytes[3];
        int byte0Shifted = byte0 << 23;
        int byte1Shifted = byte1 << 15;
        int byte2Shifted = byte2 << 7;
        int byte3Shifted = byte3 << 0;
        return byte0Shifted + byte1Shifted + byte2Shifted + byte3Shifted;
    }
}
