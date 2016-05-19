package edu.neumont.csc360.btree;

import edu.neumont.csc360.btree.storage.NodeStore;

public class BTree {
    private NodeStore nodeStore;
    private Node root;

    private BTree() {}

    public static void create(String name, int nodeCapacity) {
        NodeStore.create(name, nodeCapacity);
        NodeStore.open(name).allocate();
    }

    public static BTree open(String name) {
        NodeStore nodeStore = NodeStore.open(name);
        BTree bTree = new BTree();
        bTree.nodeStore = nodeStore;
        bTree.root = nodeStore.getRootNode();
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
