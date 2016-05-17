package edu.neumont.csc360.btree.storage;

public class MetaDataFile {
    private File file;
    private int metaDataSize;
    private int[] metaData;

    private MetaDataFile() {}

    /**
     * Creates a MetaDataFile with name and the ability to hold metaDataSize bytes in its metadata
     * @param name The name of the file to be created.
     * @param metaDataSize The number of bytes allowed in the file's metadata.
     */
    public static void create(String name, int metaDataSize) {
        if (metaDataSize < 0) {
            throw new RuntimeException("metaDataSize cannot be less than 0. metaDataSize: " + metaDataSize);
        }
        File.create(name);
        File file = File.open(name);
        file.writeInt(metaDataSize, 0);
    }

    public static MetaDataFile open(String name) {
        MetaDataFile metaDataFile = new MetaDataFile();
        metaDataFile.file = File.open(name);
        metaDataFile.metaDataSize = metaDataFile.file.readInt(0);
        metaDataFile.metaData = metaDataFile.file.read(4, metaDataFile.metaDataSize);
        return metaDataFile;
    }

    public static void delete(String name) {
        File.delete(name);
    }

    public void close() {
        this.file.close();
    }

    /**
     * Directly writes the byte array to disk. The length of metaData must be exactly the metaDataSize.
     * @param metaData The byte array, with each value being 0-255, to be written as the metaData.
     */
    public void writeMetaData(int[] metaData) {
        if (metaData.length != this.metaDataSize) {
            throw new RuntimeException("The length of metaData must be exactly metaDataSize. " +
                    "(metaData.length: " + metaData.length + ", metaDataSize: " + this.metaDataSize + ")");
        }
        this.metaData = metaData;
        this.file.write(metaData, 4);
    }

    /**
     * Writes the entire buffer as a byte[] to the file.
     * @param buffer The byte array to write, with each int value being between 0 and 255.
     * @param location The location to write to
     */
    public void write(int[] buffer, long location) {
        long offset = this.getOffset(location);
        this.file.write(buffer, offset);
    }

    /**
     * Reads the buffer of size length starting at location.
     * @param location The starting position of the buffer.
     * @param length The length of the buffer to be read.
     * @return An int array acting as an unsigned byte array (all values are between 0 and 255).
     */
    public int[] read(long location, int length) {
        long offset = this.getOffset(location);
        return this.file.read(offset, length);
    }

    private long getOffset(long location) {
        return 4 + this.metaDataSize + location;
    }

    public int getMetaDataSize() {
        return this.metaDataSize;
    }

    public int[] getMetaData() {
        return this.metaData;
    }
}
