package edu.neumont.csc360.btree.storage.test;

import edu.neumont.csc360.btree.storage.CachedPersistentArray;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class CachedPersistentArrayTest {
    public static final String testFile = "testfile";

    @Test
    public void readWrite() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));
        CachedPersistentArray.create(testFile, 0, 16);
        CachedPersistentArray cachedPersistentArray = CachedPersistentArray.open(testFile);

        int[] buffer = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        for (int i = 0; i < 16; i++) {
            cachedPersistentArray.allocate();
            cachedPersistentArray.putBuffer(i, buffer);
        }
        for (int i = 0; i < 16; i++) {
            int[] readBuffer = cachedPersistentArray.getBuffer(i);
            Assert.assertArrayEquals(buffer, readBuffer);
        }
    }
}