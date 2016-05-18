package edu.neumont.csc360.btree.storage;

public class PersistentArray {
    private static final int PERSISTENT_ARRAY_METADATA_SIZE = 12;

    private MetadataFile metadataFile;
    private int count;
    private int bufferSize;
    private int nextAvailableIndex;

    private PersistentArray() {}
    
    /**
     * Creates a PersistentArray with the specified name and bufferSize, and a MetadataFile with metadataSize.
     *
     * @param name         The name of the file to be created.
     * @param metadataSize The number of bytes allocated for custom metadata. It must be greater than 0.
     * @param bufferSize   The number of bytes each buffer/element will be. It must be greater than 0.
     */
    public static void create(String name, int metadataSize, int bufferSize) {
        if (metadataSize < 0) {
            throw new RuntimeException("metadataSize cannot be less than 0. metadataSize: " + metadataSize);
        }
        if (bufferSize < 0) {
            throw new RuntimeException("bufferSize cannot be less than 0. bufferSize: " + bufferSize);
        }
        
        // 12 is the size of integers count, bufferSize, and nextAvailableIndex
        MetadataFile.create(name, metadataSize);

        int count = 0;
        int nextAvailableIndex = -1;

        MetadataFile metadataFile = MetadataFile.open(name);
        metadataFile.writeInt(count, 0);
        metadataFile.writeInt(bufferSize, 4);
        metadataFile.writeInt(nextAvailableIndex, 8);
    }
    
    public static PersistentArray open(String name) {
        PersistentArray persistentArray = new PersistentArray();
        MetadataFile metadataFile = MetadataFile.open(name);
        persistentArray.metadataFile = metadataFile;
        persistentArray.count = metadataFile.readInt(0);
        persistentArray.bufferSize = metadataFile.readInt(4);
        persistentArray.nextAvailableIndex = metadataFile.readInt(8);
        return persistentArray;
    }

    public static void delete(String name) {
        MetadataFile.delete(name);
    }

    public void close() {
        this.metadataFile.close();
    }

    public int[] getMetadata() {
        return this.metadataFile.getMetadata();
    }

    public void writeMetadata(int[] metadata) {
        this.metadataFile.writeMetadata(metadata);
    }
}
