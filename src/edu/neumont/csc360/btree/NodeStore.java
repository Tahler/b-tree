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
        int sizeOfNode = Node.NODE_METADATA_SIZE + nodeCapacity * Node.KEY_VALUE_PAIR_SIZE;
        CachedPersistentArray.create(name, NODE_STORE_METADATA_SIZE, sizeOfNode);

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
        int[] buffer = node.toByteArray();
        this.cachedPersistentArray.putBuffer(index, buffer);
    }

    public void addNode(Node node) {
        int index = this.allocate();
        this.putNode(index, node);
    }

    public void addRoot(Node newRoot) {
        this.addNode(newRoot);
        int newRootIndex = newRoot.index;
        this.setRootNodeIndex(newRootIndex);
    }

    public void writeNode(Node node) {
        int index = node.index;
        this.putNode(index, node);
    }

    public Node getNode(int index) {
        int[] buffer = this.cachedPersistentArray.getBuffer(index);
        return Node.fromByteArray(buffer);
    }

    public Node getRootNode() {
        return this.getNode(this.rootNodeIndex);
    }

    public void setRootNodeIndex(int rootNodeIndex) {
        this.rootNodeIndex = rootNodeIndex;
        this.rewriteMetadata();
    }

    public int getNodeCapacity() {
        return this.nodeCapacity;
    }

    private void rewriteMetadata() {
        int[] metadata = new int[NODE_STORE_METADATA_SIZE];
        ByteUtils.writeBytesFromIntToByteArray(this.nodeCapacity, metadata, 0);
        ByteUtils.writeBytesFromIntToByteArray(this.rootNodeIndex, metadata, 4);
        this.cachedPersistentArray.writeMetadata(metadata);
    }
}
