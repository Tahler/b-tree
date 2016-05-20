package edu.neumont.csc360.btree.test;

import edu.neumont.csc360.btree.BTree;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class BTreeTest {
    private static final String testFile = "testfile";

    @Test
    public void add1Key() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));

        BTree.create(testFile, 3);
        BTree bTree = BTree.open(testFile);

        int key = 1;
        int value = 10;
        bTree.addKey(key, value);

        int actualValue = bTree.getValue(key);
        Assert.assertEquals(value, actualValue);
    }

    @Test
    public void split() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));

        BTree.create(testFile, 3);
        BTree bTree = BTree.open(testFile);

        bTree.addKey(1, 10);
        bTree.addKey(2, 20);
        bTree.addKey(3, 30);
        bTree.addKey(0, 1);

        int actualValue = bTree.getValue(1);
        Assert.assertEquals(10, actualValue);
    }

    @Test
    public void add100Keys() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));

        BTree.create(testFile, 3);
        BTree bTree = BTree.open(testFile);
        int[] keys = new int[100];
        int[] values = new int[100];
        for (int i = 0; i < 100; i++) {
            keys[i] = i;
            values[i] = 2 * i;
            bTree.addKey(keys[i], values[i]);
        }

        for (int i = 0; i < keys.length; i++) {
            int key = keys[i];
            int expectedValue = values[i];
            int actualValue = bTree.getValue(key);
            Assert.assertEquals(expectedValue, actualValue);
        }
    }

    @Test
    public void getValue() throws Exception {

    }

    @Test
    public void updateKeysValue() throws Exception {

    }

    @Test
    public void deleteKey() throws Exception {

    }

}