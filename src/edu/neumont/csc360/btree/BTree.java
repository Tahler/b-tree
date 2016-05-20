package edu.neumont.csc360.btree;

import java.util.Map;

public class BTree {
    protected NodeStore nodeStore;
    protected Node root;
    protected int nodeCapacity;

    private BTree() {}

    /**
     * Initializes a BTree with the given name. The BTree will persist its data in a file <code>name</code> and each
     * node will contain <code>nodeCapacity</code> key-value-pairs.
     * @param name The name given to the BTree.
     * @param nodeCapacity The number of key-value-pairs each each node can contain.
     */
    public static void create(String name, int nodeCapacity) {
        NodeStore.create(name, nodeCapacity);
        NodeStore nodeStore = NodeStore.open(name);
        int rootIndex = nodeStore.allocate();
        Node root = new Node(null, null, rootIndex, true, 0, new KeyValuePair[nodeCapacity]);
        nodeStore.putNode(rootIndex, root);
    }

    /**
     * Opens a BTree with the given name. The file must exist and have been created using
     * <code>BTree::create(String, int)</code>.
     * @param name The name of the BTree to be opened.
     * @return The opened BTree.
     */
    public static BTree open(String name) {
        BTree bTree = new BTree();

        NodeStore nodeStore = NodeStore.open(name);
        bTree.nodeStore = nodeStore;

        int rootNodeIndex = nodeStore.getRootNodeIndex();
        bTree.root = nodeStore.getNode(rootNodeIndex, bTree, null);
        bTree.nodeCapacity = nodeStore.getNodeCapacity();

        return bTree;
    }

    /**
     * Deletes the BTree with the given name by deleting its file.
     * @param name The name of the BTree to delete.
     */
    public static void delete(String name) {
        NodeStore.delete(name);
    }

    /**
     * Closes the file being used by the BTree.
     */
    public void close() {
        this.nodeStore.close();
    }

    /**
     * Adds a key-value-pair to the BTree. The BTree must not contain the key already.
     * @param key   The key to be added to the BTree.
     * @param value The associated value to be added in correspondence with the key.
     */
    public void addKey(int key, int value) {
        this.root.addKey(key, value);
    }

    /**
     * Returns the value associated with the given key. The key must currently exist in the BTree.
     * @param key The key to be searched for.
     * @return The associated value of the key.
     */
    public int getValue(int key) {
        return this.root.getValue(key);
    }

    /**
     * Updates the key-value-pair with the given key to a new value. The key must currently exist in the BTree.
     * @param key   The key whose value will be updated.
     * @param value The new value to be associated with the given key.
     */
    public void updateKeysValue(int key, int value) {
        this.root.updateKeysValue(key, value);
    }

    /**
     * Deletes the key-value-pair with the given key. The key must currently exist in the BTree.
     * @param key The key to be searched for and then deleted.
     */
    public void deleteKey(int key) {
        this.root.deleteKey(key);
    }
}
