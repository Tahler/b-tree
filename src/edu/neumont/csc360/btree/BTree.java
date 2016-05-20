package edu.neumont.csc360.btree;

public class BTree {
    protected NodeStore nodeStore;
    protected Node root;
    protected int nodeCapacity;

    private BTree() {}

    public static void create(String name, int nodeCapacity) {
        NodeStore.create(name, nodeCapacity);
        NodeStore nodeStore = NodeStore.open(name);
        int rootIndex = nodeStore.allocate();
        Node root = new Node(null, null, rootIndex, true, 0, new Node.KeyValuePair[nodeCapacity]);
        nodeStore.putNode(rootIndex, root);
    }

    public static BTree open(String name) {
        BTree bTree = new BTree();

        NodeStore nodeStore = NodeStore.open(name);
        bTree.nodeStore = nodeStore;

        int rootNodeIndex = nodeStore.getRootNodeIndex();
        bTree.root = nodeStore.getNode(rootNodeIndex, bTree, null);
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
