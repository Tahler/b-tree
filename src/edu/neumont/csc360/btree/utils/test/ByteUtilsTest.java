package edu.neumont.csc360.btree.utils.test;

import edu.neumont.csc360.btree.utils.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ByteUtilsTest {
    @Test
    public void zeroInt() throws Exception {
        int test = 0;
        int[] toBytes = ByteUtils.bytesFromInt(test);
        int fromBytes = ByteUtils.intFromBytes(toBytes);
        Assert.assertEquals(test, fromBytes);
    }

    @Test
    public void maxInt() throws Exception {
        int test = Integer.MAX_VALUE;
        int[] toBytes = ByteUtils.bytesFromInt(test);
        int fromBytes = ByteUtils.intFromBytes(toBytes);
        Assert.assertEquals(test, fromBytes);
    }

    @Test
    public void minInt() throws Exception {
        int test = Integer.MIN_VALUE;
        int[] toBytes = ByteUtils.bytesFromInt(test);
        int fromBytes = ByteUtils.intFromBytes(toBytes);
        Assert.assertEquals(test, fromBytes);
    }

    @Test
    public void negInt() throws Exception {
        int test = -1;
        int[] toBytes = ByteUtils.bytesFromInt(test);
        int fromBytes = ByteUtils.intFromBytes(toBytes);
        Assert.assertEquals(test, fromBytes);
    }
}