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

        for (int i = 0; i < 4; i++) {
            int key = i;
            int value = 2 * i;

            bTree.addKey(key, value);

            int gotValue = bTree.getValue(key);
            Assert.assertEquals(value, gotValue);
        }
    }

    @Test
    public void add100KeysIncreasing() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));

        BTree.create(testFile, 3);
        BTree bTree = BTree.open(testFile);
        int[] keys = new int[100];
        int[] values = new int[100];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = i;
            values[i] = i;
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
    public void add1000KeysIncreasing() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));

        BTree.create(testFile, 100);
        BTree bTree = BTree.open(testFile);
        int[] keys = new int[1000];
        int[] values = new int[1000];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = i;
            values[i] = i;
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