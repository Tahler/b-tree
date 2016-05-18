package edu.neumont.csc360.btree.storage.test;

import edu.neumont.csc360.btree.storage.MetadataFile;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MetadataFileTest {
    public static final String testFile = "testfile";
    
    @Test
    public void getMetadataSize() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));
        MetadataFile.create(testFile, 24);
        MetadataFile metadataFile = MetadataFile.open(testFile);
        Assert.assertEquals(24, metadataFile.getMetadataSize());
    }

    @Test
    public void metadataReadWrite() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));
        MetadataFile.create(testFile, 4);
        MetadataFile metadataFile = MetadataFile.open(testFile);

        int[] metadata = new int[] { 0, 1, 2, 3 };
        metadataFile.writeMetadata(metadata);
        Assert.assertArrayEquals(metadata, metadataFile.getMetadata());

        metadataFile.close();
        metadataFile = MetadataFile.open(testFile);
        Assert.assertArrayEquals(metadata, metadataFile.getMetadata());
    }

    @Test
    public void readWrite() throws Exception {
        Files.deleteIfExists(Paths.get(testFile));
        MetadataFile.create(testFile, 256);
        MetadataFile metadataFile = MetadataFile.open(testFile);

        int[] buffer = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        metadataFile.write(0, buffer);
        int[] readBuffer = metadataFile.read(0, buffer.length);
        Assert.assertArrayEquals(buffer, readBuffer);
    }
}