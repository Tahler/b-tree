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

        bTree.close();
    }

    @Test
    public void splitAndAdd() throws Exception {
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

        bTree.close();
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

        bTree.close();
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

        bTree.close();
    }

    @Test
    public void add1000KeysDecreasing() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));

        BTree.create(testFile, 100);
        BTree bTree = BTree.open(testFile);
        int[] keys = new int[1000];
        int[] values = new int[1000];
        for (int i = keys.length - 1; i >= 0; i--) {
            keys[i] = i;
            values[i] = i;
            bTree.addKey(keys[i], values[i]);
        }

        for (int i = keys.length - 1; i >= 0; i--) {
            int key = keys[i];
            int expectedValue = values[i];
            int actualValue = bTree.getValue(key);
            Assert.assertEquals(expectedValue, actualValue);
        }

        bTree.close();
    }

    @Test
    public void updateKeysValue() throws Exception {
        this.add100KeysIncreasing();

        BTree bTree = BTree.open(testFile);
        Assert.assertEquals(20, bTree.getValue(20));

        bTree.updateKeysValue(20, Integer.MIN_VALUE);
        Assert.assertEquals(Integer.MIN_VALUE, bTree.getValue(20));

        bTree.close();
    }

    @Test
    public void updateKeysValue1000Keys() throws Exception {
        this.add1000KeysIncreasing();

        BTree bTree = BTree.open(testFile);
        Assert.assertEquals(660, bTree.getValue(660));

        bTree.updateKeysValue(660, Integer.MIN_VALUE);
        Assert.assertEquals(Integer.MIN_VALUE, bTree.getValue(660));

        bTree.close();
    }

    @Test
    public void deleteKey() throws Exception {
        this.splitAndAdd();

        BTree bTree = BTree.open(testFile);
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(2 * i, bTree.getValue(i));
            bTree.deleteKey(i);
        }

        bTree.close();
    }
}