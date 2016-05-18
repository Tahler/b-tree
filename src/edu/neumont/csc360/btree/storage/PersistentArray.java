package edu.neumont.csc360.btree.storage;

public class PersistentArray {
    private MetadataFile metadataFile;
    private static final int METADATA_SIZE = 12;
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
        int actualMetadataSize = metadataSize + METADATA_SIZE;
        MetadataFile.create(name, actualMetadataSize);

        int count = 0;
        int nextAvailableIndex = -1;
        int[] metadata = new int[actualMetadataSize];
        metadata[0] = count;
        metadata[1] = bufferSize;
        metadata[2] = nextAvailableIndex;

        MetadataFile metadataFile = MetadataFile.open(name);
        metadataFile.writeMetadata(metadata);
    }
    
    public static PersistentArray open(String name) {
        PersistentArray persistentArray = new PersistentArray();
        persistentArray.metadataFile = MetadataFile.open(name);
        int[] metadata = persistentArray.metadataFile.getMetadata();
        persistentArray.count = metadata[0];
        persistentArray.bufferSize = metadata[1];
        persistentArray.nextAvailableIndex = metadata[2];
        return persistentArray;
    }

    public static void delete(String name) {
        MetadataFile.delete(name);
    }

    public void close() {
        this.metadataFile.close();
    }

    public int[] readMetadata() {
        return new int[0]; // tODO
    }

    public void writeMetadata(int[] metadata) {
        int userMetadataSize = this.metadataFile.getMetadataSize() - METADATA_SIZE;
        if (metadata.length != userMetadataSize) {
            throw new RuntimeException("The length of metadata must be exactly metadataSize. " +
                    "(metadata.length: " + metadata.length + ", metadataSize: " + userMetadataSize + ")");
        }
        int[] copied = new int[metadata.length + 12];
//        this.metadataFile.writeMetadata(); // TODO
    }
}
