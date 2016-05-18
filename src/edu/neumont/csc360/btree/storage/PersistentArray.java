package edu.neumont.csc360.btree.storage;

public class PersistentArray {
    private static final int PERSISTENT_ARRAY_METADATA_SIZE = 12;
    private static final int COUNT_LOCATION = 0;
    private static final int BUFFER_SIZE_LOCATION = 4;
    private static final int NEXT_AVAILABLE_INDEX_LOCATION = 8;

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
        if (bufferSize < 4) {
            throw new RuntimeException("bufferSize cannot be less than 4. bufferSize: " + bufferSize);
        }
        
        // 12 is the size of integers count, bufferSize, and nextAvailableIndex
        MetadataFile.create(name, metadataSize);

        int count = 0;
        int nextAvailableIndex = -1;

        MetadataFile metadataFile = MetadataFile.open(name);
        metadataFile.writeInt(COUNT_LOCATION, count);
        metadataFile.writeInt(BUFFER_SIZE_LOCATION, bufferSize);
        metadataFile.writeInt(NEXT_AVAILABLE_INDEX_LOCATION, nextAvailableIndex);
    }
    
    public static PersistentArray open(String name) {
        PersistentArray persistentArray = new PersistentArray();
        MetadataFile metadataFile = MetadataFile.open(name);
        persistentArray.metadataFile = metadataFile;
        persistentArray.count = metadataFile.readInt(COUNT_LOCATION);
        persistentArray.bufferSize = metadataFile.readInt(BUFFER_SIZE_LOCATION);
        persistentArray.nextAvailableIndex = metadataFile.readInt(NEXT_AVAILABLE_INDEX_LOCATION);
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

    /**
     * Allocates space in the file for a new buffer of bufferSize. Returns the index of the allocated space.
     * @return The index of the allocated space.
     */
    public int allocate() {
        int allocatedIndex;
        if (this.nextAvailableIndex == -1) {
            allocatedIndex = this.count;
        } else {
            allocatedIndex = this.nextAvailableIndex;
            this.nextAvailableIndex = this.readIntFromBlock(allocatedIndex);
        }
        this.incrementCount();
        return allocatedIndex;
    }

    public void deallocate(int index) {
        this.writeIntToBlock(this.nextAvailableIndex, index);
        this.nextAvailableIndex = index;
        this.metadataFile.writeInt(NEXT_AVAILABLE_INDEX_LOCATION, this.nextAvailableIndex);
        this.decrementCount();
    }

    public int[] getBuffer(int index) {
        long location = this.getLocation(index);
        return this.metadataFile.read(location, this.bufferSize);
    }

    public void putBuffer(int index, int[] bytes) {
        long location = this.getLocation(index);
        this.metadataFile.write(location, bytes);
    }

    private void incrementCount() {
        this.count += 1;
        this.metadataFile.writeInt(COUNT_LOCATION, this.count);
    }

    private void decrementCount() {
        this.count -= 1;
        this.metadataFile.writeInt(COUNT_LOCATION, this.count);
    }

    private int readIntFromBlock(int index) {
        long location = this.getLocation(index);
        return this.metadataFile.readInt(location);
    }

    private void writeIntToBlock(int integer, int index) {
        long location = this.getLocation(index);
        this.metadataFile.writeInt(location, integer);
    }

    private long getOffset() {
        return PERSISTENT_ARRAY_METADATA_SIZE;
    }

    private long getLocation(int index) {
        return PERSISTENT_ARRAY_METADATA_SIZE + this.bufferSize * index;
    }
}
