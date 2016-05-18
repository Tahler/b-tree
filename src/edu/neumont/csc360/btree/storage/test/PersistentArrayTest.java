package edu.neumont.csc360.btree.storage.test;

import edu.neumont.csc360.btree.storage.MetadataFile;
import edu.neumont.csc360.btree.storage.PersistentArray;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class PersistentArrayTest {
    public static final String testFile = "testfile";

    @Test
    public void allocateDeallocate() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));
        PersistentArray.create(testFile, 0, 16);
        PersistentArray persistentArray = PersistentArray.open(testFile);

        int index0 = persistentArray.allocate();
        Assert.assertEquals(0, index0);

        int index1 = persistentArray.allocate();
        Assert.assertEquals(1, index1);

        int index2 = persistentArray.allocate();
        Assert.assertEquals(2, index2);

        persistentArray.deallocate(index0);
        index0 = persistentArray.allocate();
        Assert.assertEquals(0, index0);

        int index3 = persistentArray.allocate();
        Assert.assertEquals(3, index3);
    }

    @Test
    public void readWrite() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));
        PersistentArray.create(testFile, 0, 16);
        PersistentArray persistentArray = PersistentArray.open(testFile);

        int index = persistentArray.allocate();
        Assert.assertEquals(0, index);

        int[] buffer = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        persistentArray.putBuffer(index, buffer);

        int[] readBuffer = persistentArray.getBuffer(index);
        Assert.assertArrayEquals(buffer, readBuffer);
    }
}