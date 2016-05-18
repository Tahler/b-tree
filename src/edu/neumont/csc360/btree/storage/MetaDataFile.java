package edu.neumont.csc360.btree.storage;

public class MetadataFile {
    private File file;
    private int metadataSize;
    private int[] metadata;

    private MetadataFile() {}

    /**
     * Creates a MetadataFile with name and the ability to hold metadataSize bytes in its metadata
     * @param name The name of the file to be created.
     * @param metadataSize The number of bytes allowed in the file's metadata.
     */
    public static void create(String name, int metadataSize) {
        if (metadataSize < 0) {
            throw new RuntimeException("metadataSize cannot be less than 0. metadataSize: " + metadataSize);
        }
        File.create(name);
        File file = File.open(name);
        file.writeInt(metadataSize, 0);
    }

    public static MetadataFile open(String name) {
        MetadataFile metadataFile = new MetadataFile();
        metadataFile.file = File.open(name);
        metadataFile.metadataSize = metadataFile.file.readInt(0);
        metadataFile.metadata = metadataFile.file.read(4, metadataFile.metadataSize);
        return metadataFile;
    }

    public static void delete(String name) {
        File.delete(name);
    }

    public void close() {
        this.file.close();
    }

    public int[] getMetadata() {
        return this.metadata;
    }

    /**
     * Directly writes the byte array to disk. The length of metadata must be exactly the metadataSize.
     * @param metadata The byte array, with each value being 0-255, to be written as the metadata.
     */
    public void writeMetadata(int[] metadata) {
        if (metadata.length != this.metadataSize) {
            throw new RuntimeException("The length of metadata must be exactly metadataSize. " +
                    "(metadata.length: " + metadata.length + ", metadataSize: " + this.metadataSize + ")");
        }
        this.metadata = metadata;
        this.file.write(metadata, 4);
    }

    public int getMetadataSize() {
        return this.metadataSize;
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
     * Writes a 4-byte integer to the file at the specified location.
     * @param integer The integer (value must be between 0 and 255) to write.
     * @param location The offset at which to write the byte.
     */
    public void writeInt(int integer, long location) {
        long offset = this.getOffset(location);
        this.file.writeInt(integer, offset);
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

    /**
     * Reads the 4-byte integer at location.
     * @param location The offset to read from.
     * @return The integer read from location.
     */
    public int readInt(long location) {
        long offset = this.getOffset(location);
        return this.file.readInt(offset);
    }

    private long getOffset(long location) {
        return 4 + this.metadataSize + location;
    }
}
