package edu.neumont.csc360.btree.storage.test;

import edu.neumont.csc360.btree.storage.MetaDataFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MetaDataFileTest {
    @Test
    public void getMetaDataSize() throws Exception {
        Files.deleteIfExists(Paths.get("testfile"));
        MetaDataFile.create("testfile", 24);
        MetaDataFile metaDataFile = MetaDataFile.open("testfile");
        Assert.assertEquals(24, metaDataFile.getMetaDataSize());
    }

    @Test
    public void metaDataReadWrite() throws Exception {
        Files.deleteIfExists(Paths.get("testfile"));
        MetaDataFile.create("testfile", 4);
        MetaDataFile metaDataFile = MetaDataFile.open("testfile");

        int[] metaData = new int[] { 0, 1, 2, 3 };
        metaDataFile.writeMetaData(metaData);
        Assert.assertArrayEquals(metaData, metaDataFile.getMetaData());

        metaDataFile.close();
        metaDataFile = MetaDataFile.open("testfile");
        Assert.assertArrayEquals(metaData, metaDataFile.getMetaData());
    }

    @Test
    public void readWrite() throws Exception {
        Files.deleteIfExists(Paths.get("testfile"));
        MetaDataFile.create("testfile", 256);
        MetaDataFile metaDataFile = MetaDataFile.open("testfile");

        int[] buffer = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        metaDataFile.write(buffer, 0);
        int[] readBuffer = metaDataFile.read(0, buffer.length);
        Assert.assertArrayEquals(buffer, readBuffer);
    }
}