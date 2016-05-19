package edu.neumont.csc360.btree;

public class BTree {
    protected NodeStore nodeStore;
    protected Node root;
    protected int nodeCapacity;

    private BTree() {}

    public static void create(String name, int nodeCapacity) {
        NodeStore.create(name, nodeCapacity);
        NodeStore.open(name).allocate();
    }

    public static BTree open(String name) {
        BTree bTree = new BTree();

        NodeStore nodeStore = NodeStore.open(name);
        bTree.nodeStore = nodeStore;

        Node root = nodeStore.getRootNode();
        root.bTree = bTree;
        root.parent = null;
        root.index = 0;
        bTree.root = root;
        bTree.nodeCapacity = nodeStore.getNodeCapacity();

        return bTree;
    }

    public static void delete(String name) {
        NodeStore.delete(name);
    }

    public void addKey(int key, int value) {
        this.root.addKey(key, value);
    }

    public int getValue(int key) {
        return this.root.getValue(key);
    }

    public void updateKeysValue(int key, int value) {
        this.root.updateKeysValue(key, value);
    }

    public void deleteKey(int key) {
        this.root.deleteKey(key);
    }
}
