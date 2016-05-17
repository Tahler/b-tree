package edu.neumont.csc360.btree.storage.test;

import edu.neumont.csc360.btree.storage.MetaDataFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MetaDataFileTest {
    public static final String testFile = "testfile";
    
    @Test
    public void getMetaDataSize() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));
        MetaDataFile.create(testFile, 24);
        MetaDataFile metaDataFile = MetaDataFile.open(testFile);
        Assert.assertEquals(24, metaDataFile.getMetaDataSize());
    }

    @Test
    public void metaDataReadWrite() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));
        MetaDataFile.create(testFile, 4);
        MetaDataFile metaDataFile = MetaDataFile.open(testFile);

        int[] metaData = new int[] { 0, 1, 2, 3 };
        metaDataFile.writeMetaData(metaData);
        Assert.assertArrayEquals(metaData, metaDataFile.getMetaData());

        metaDataFile.close();
        metaDataFile = MetaDataFile.open(testFile);
        Assert.assertArrayEquals(metaData, metaDataFile.getMetaData());
    }

    @Test
    public void readWrite() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));
        MetaDataFile.create(testFile, 256);
        MetaDataFile metaDataFile = MetaDataFile.open(testFile);

        int[] buffer = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        metaDataFile.write(buffer, 0);
        int[] readBuffer = metaDataFile.read(0, buffer.length);
        Assert.assertArrayEquals(buffer, readBuffer);
    }
}