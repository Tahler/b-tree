package edu.neumont.csc360.btree;

import edu.neumont.csc360.btree.cache.Cache;

public class CachedPersistentArray {
    private static final int CACHE_SIZE = 10;

    private PersistentArray persistentArray;
    private Cache<Integer, Buffer> cache;

    private CachedPersistentArray() {}

    public static void create(String name, int metadataSize, int bufferSize) {
        PersistentArray.create(name, metadataSize, bufferSize);
    }

    public static CachedPersistentArray open(String name) {
        CachedPersistentArray cachedPersistentArray = new CachedPersistentArray();
        cachedPersistentArray.persistentArray = PersistentArray.open(name);
        cachedPersistentArray.cache = new Cache<>(CACHE_SIZE);
        return cachedPersistentArray;
    }

    public static void delete(String name) {
        PersistentArray.delete(name);
    }

    public void close() {
        this.persistentArray.close();
    }

    public int[] getMetadata() {
        return this.persistentArray.getMetadata();
    }

    public void writeMetadata(int[] metadata) {
        this.persistentArray.writeMetadata(metadata);
    }

    public int allocate() {
        return this.persistentArray.allocate();
    }

    public void deallocate(int index) {
        if (this.cache.containsKey(index)) {
            this.cache.remove(index);
        }
        this.persistentArray.deallocate(index);
    }

    public int[] getBuffer(int index) {
        return this.cache.containsKey(index)
                ? this.cache.get(index).data
                : this.persistentArray.getBuffer(index);
    }

    public void putBuffer(int index, int[] buffer) {
        this.cache.put(index, new Buffer(buffer));
        this.persistentArray.putBuffer(index, buffer);
    }

    private class Buffer {
        private int[] data;

        public Buffer(int[] data) {
            this.data = data;
        }
    }
}
