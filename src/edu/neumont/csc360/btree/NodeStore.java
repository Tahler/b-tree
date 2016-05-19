package edu.neumont.csc360.btree;

import edu.neumont.csc360.btree.utils.ByteUtils;

public class NodeStore {
    private static final int NODE_STORE_METADATA_SIZE = 8;
    private CachedPersistentArray cachedPersistentArray;
    private int nodeCapacity;
    private int rootNodeIndex;

    private NodeStore() {}

    public static void create(String name, int nodeCapacity) {
        if (nodeCapacity <= 0) {
            throw new RuntimeException("Node capacity cannot be less than 0 bytes.");
        }
        CachedPersistentArray.create(name, NODE_STORE_METADATA_SIZE, 0); // TODO 0 - sizeof(Node)

        int[] metadata = new int[NODE_STORE_METADATA_SIZE];

        int[] nodeCapacityBytes = ByteUtils.bytesFromInt(nodeCapacity);
        System.arraycopy(nodeCapacityBytes, 0, metadata, 0, 4);

        int rootNodeIndex = 0;
        int[] rootNodeIndexBytes = ByteUtils.bytesFromInt(rootNodeIndex);
        System.arraycopy(rootNodeIndexBytes, 0, metadata, 4, 4);

        CachedPersistentArray.open(name).writeMetadata(metadata);
    }

    public static NodeStore open(String name) {
        CachedPersistentArray cachedPersistentArray = CachedPersistentArray.open(name);

        int[] metadata = cachedPersistentArray.getMetadata();

        int[] nodeCapacityBytes = new int[4];
        System.arraycopy(metadata, 0, nodeCapacityBytes, 0, 4);
        int nodeCapacity = ByteUtils.intFromBytes(nodeCapacityBytes);

        int[] rootNodeIndexBytes = new int[4];
        System.arraycopy(metadata, 4, rootNodeIndexBytes, 0, 4);
        int rootNodeIndex = ByteUtils.intFromBytes(rootNodeIndexBytes);

        NodeStore nodeStore = new NodeStore();
        nodeStore.cachedPersistentArray = cachedPersistentArray;
        nodeStore.nodeCapacity = nodeCapacity;
        nodeStore.rootNodeIndex = rootNodeIndex;
        return nodeStore;
    }

    public static void delete(String name) {
        CachedPersistentArray.delete(name);
    }

    public void close() {
        this.cachedPersistentArray.close();
    }

    public int allocate() {
        return this.cachedPersistentArray.allocate();
    }

    public void deallocate(int index) {
        this.cachedPersistentArray.deallocate(index);
    }

    public void putNode(int index, Node node) {
        node.toByteArray()
    }

    public void addNode(Node node) {
        int index = this.allocate();
        this.putNode(index, node);
    }

    public Node getNode(int index) {
        // TODO
    }

    public Node getRootNode() {
        return this.getNode(this.rootNodeIndex);
    }

    public void setRootNodeIndex(int rootNodeIndex) {
        this.rootNodeIndex = rootNodeIndex;
        this.rewriteMetadata();
    }

    private void rewriteMetadata() {
        int[] metadata = new int[NODE_STORE_METADATA_SIZE];

        int[] nodeCapacityBytes = ByteUtils.bytesFromInt(this.nodeCapacity);
        System.arraycopy(nodeCapacityBytes, 0, metadata, 0, 4);

        int[] rootNodeIndexBytes = ByteUtils.bytesFromInt(this.rootNodeIndex);
        System.arraycopy(rootNodeIndexBytes, 0, metadata, 4, 4);

        this.cachedPersistentArray.writeMetadata(metadata);
    }
}
