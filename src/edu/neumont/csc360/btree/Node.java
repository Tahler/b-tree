package edu.neumont.csc360.btree;

import edu.neumont.csc360.btree.utils.ByteUtils;

public class Node {
    protected static final int NODE_METADATA_SIZE = 5;
    protected static final int KEY_VALUE_PAIR_SIZE = 8;

    // Not persisted
    protected BTree bTree;
    protected Node parent;
    protected int index;

    // Persisted
    private boolean isLeaf;
    private int size;
    private KeyValuePair[] data;

    private Node() {}

    /**
     * Constructs a new root node.
     */
    public Node(int capacity) {
        this.isLeaf = true;
        this.size = 0;
        this.data = new KeyValuePair[capacity];
    }

    public Node(BTree bTree, Node parent, int index, boolean isLeaf, int capacity) {
        this(bTree, parent, index, isLeaf, 0, new KeyValuePair[capacity]);
    }

    public Node(BTree bTree, Node parent, int index, boolean isLeaf, int size, KeyValuePair[] data) {
        this.bTree = bTree;
        this.parent = parent;
        this.index = index;
        this.isLeaf = isLeaf;
        this.size = size;
        this.data = data;
    }

    private Node(boolean isLeaf, int size, KeyValuePair[] data) {
        this.isLeaf = isLeaf;
        this.size = size;
        this.data = data;
    }

    protected static Node fromByteArray(int[] bytes) {
        Node node = new Node();

        node.isLeaf = ByteUtils.booleanFromByteInByteArray(bytes, 0);
        node.size = ByteUtils.intFromBytesInByteArray(bytes, 1);

        int numKvps = (bytes.length - NODE_METADATA_SIZE) / KEY_VALUE_PAIR_SIZE;
        KeyValuePair[] data = new KeyValuePair[numKvps];
        for (int i = 0; i < numKvps; i++) {
            int offset = NODE_METADATA_SIZE + i * KEY_VALUE_PAIR_SIZE;

            int key = ByteUtils.intFromBytesInByteArray(bytes, offset);
            int value = ByteUtils.intFromBytesInByteArray(bytes, offset + 4);

            data[i] = new KeyValuePair(key, value);
        }
        node.data = data;

        return node;
    }

    protected int[] toByteArray() {
        int[] bytes = new int[NODE_METADATA_SIZE + this.data.length * KEY_VALUE_PAIR_SIZE];

        ByteUtils.writeBooleanToByteArray(this.isLeaf, bytes, 0);
        ByteUtils.writeBytesFromIntToByteArray(this.size, bytes, 1);
        for (int i = 0; i < this.data.length; i++) {
            KeyValuePair kvp = this.data[i];
            if (kvp != null) {
                int offset = NODE_METADATA_SIZE + i * KEY_VALUE_PAIR_SIZE;
                ByteUtils.writeBytesFromIntToByteArray(kvp.key, bytes, offset);
                ByteUtils.writeBytesFromIntToByteArray(kvp.value, bytes, offset + 4);
            }
        }

        return bytes;
    }

    /**
     * Traverses down the tree until reaching a leaf node, then inserts the key-value-pair.
     */
    public void addKey(int key, int value) {
        if (this.isLeaf) {
            if (this.isRoot()) {
                this.insertKey(key, value);
            } else {
                int oldMaxKey = this.maxKey();
                this.insertKey(key, value);
                int newMaxKey = this.maxKey();
                if (oldMaxKey != newMaxKey) {
                    this.parent.updateKeyFromChild(this.index, newMaxKey);
                }
            }
            this.rewrite();
        } else {
            Node child = this.childWithKey(key);
            child.addKey(key, value);
        }
    }

    public int getValue(int key) {
        int value;
        if (this.isLeaf) {
            KeyValuePair foundKvp = this.searchForKey(key);
            value = foundKvp.value; // technically could be null
        } else {
            Node child = this.childWithKey(key);
            value = child.getValue(key);
        }
        return value;
    }

    public void updateKeysValue(int key, int value) {
        if (this.isLeaf) {
            KeyValuePair toBeUpdated = this.searchForKey(key);
            toBeUpdated.value = value;
            this.rewrite();
        } else {
            Node child = this.childWithKey(key);
            child.updateKeysValue(key, value);
        }
    }

    /**
     * Traverses down the tree until reaching a leaf node, then removes the key-value-pair from that leaf node.
     */
    public void deleteKey(int key) {
        if (this.isLeaf) {
            this.removeKey(key);
        } else {
            Node child = this.childWithKey(key);
            child.deleteKey(key);
        }
    }

    private KeyValuePair searchForKey(int key) {
        KeyValuePair foundKvp = null;
        int loIndex = 0;
        int hiIndex = this.size - 1;
        while (loIndex <= hiIndex) {
            int midIndex = loIndex + (hiIndex - loIndex) / 2;
            KeyValuePair kvp = this.data[midIndex];
            int mid = kvp.key;
            if (key < mid) {
                hiIndex = midIndex - 1;
            } else if (key > mid) {
                loIndex = midIndex + 1;
            } else { // key == mid
                foundKvp = kvp;
                break;
            }
        }
        return foundKvp;
    }

    private KeyValuePair searchForValue(int value) {
        KeyValuePair foundKvp = null;
        int loIndex = 0;
        int hiIndex = this.size - 1;
        while (loIndex <= hiIndex) {
            int midIndex = loIndex + (hiIndex - loIndex) / 2;
            KeyValuePair kvp = this.data[midIndex];
            int mid = kvp.value;
            if (value < mid) {
                hiIndex = midIndex - 1;
            } else if (value > mid) {
                loIndex = midIndex + 1;
            } else { // value == mid
                foundKvp = kvp;
                break;
            }
        }
        return foundKvp;
    }

    public boolean isLeaf() {
        return this.isLeaf;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    private Node childWithKey(int key) {
        int childIndex = this.data[this.size - 1].value; // default value, in case trying to find a key greater than any current keys
        for (int i = 0; i < this.size; i++) {
            KeyValuePair kvp = this.data[i];
            if (key <= kvp.key) {
                childIndex = kvp.value;
                break;
            }
        }
        Node child = this.bTree.nodeStore.getNode(childIndex);
        child.bTree = this.bTree;
        child.parent = this;
        child.index = childIndex;
        return child;
    }

    private void splitAndAdd(int key, int value) {
        int dataLength = this.data.length;
        int firstHalfLength = dataLength / 2;
        int secondHalfLength = dataLength - firstHalfLength;

        KeyValuePair[] firstHalf = new KeyValuePair[dataLength];
        System.arraycopy(this.data, 0, firstHalf, 0, firstHalfLength);

        KeyValuePair[] secondHalf = new KeyValuePair[dataLength];
        System.arraycopy(this.data, firstHalfLength, secondHalf, 0, secondHalfLength);

        this.size = firstHalfLength;
        this.data = firstHalf;

        int otherNodeIndex = this.bTree.nodeStore.allocate();
        Node otherNode = new Node(this.bTree, this.parent, -1, this.isLeaf, secondHalfLength, secondHalf);
        otherNode.index = otherNodeIndex;

        this.bTree.nodeStore.putNode(otherNodeIndex, otherNode);
        this.bTree.nodeStore.writeNode(this);

        if (key < this.maxKey()) {
            this.insertKey(key, value);
        } else {
            otherNode.insertKey(key, value);
        }

        int thisMax = this.maxKey();
        int otherMax = otherNode.maxKey();

        if (this.isRoot()) {
            Node newRoot = new Node(this.bTree, null, -1, false, 0, new KeyValuePair[this.bTree.nodeCapacity]);
            newRoot.insertKey(thisMax, this.index);
            newRoot.insertKey(otherMax, otherNode.index);

            int newRootIndex = this.bTree.nodeStore.allocate();
            newRoot.index = newRootIndex;
            this.bTree.nodeStore.putNode(newRootIndex, newRoot);

            this.bTree.root = newRoot;

            this.parent = newRoot;
            otherNode.parent = newRoot;
        } else {
            this.parent.updateKeyFromChild(this.index, thisMax);
            this.parent.insertKey(otherMax, otherNode.index);
        }
    }

    private boolean isFull() {
        return this.size == this.bTree.nodeCapacity;
    }

    private boolean isEmpty() {
        return this.size == 0;
    }

    private int maxKey() {
        return this.size > 0 ? this.data[this.size - 1].key : 0;
    }

    private void insertKey(int key, int value) {
        if (this.isFull()) {
            this.splitAndAdd(key, value);
        } else {
            // find it, shifting as you insert
            int previousIndex = this.size;
            int currentIndex = previousIndex - 1;
            while (currentIndex >= 0 && key < this.data[currentIndex].key) {
                this.data[previousIndex] = this.data[currentIndex];
                previousIndex -= 1;
                currentIndex -= 1;
            }
            this.data[previousIndex] = new KeyValuePair(key, value);
            this.size += 1;
        }
    }

    private void updateKeyFromChild(int routeIndex, int newKey) {
        int oldMaxKey = this.maxKey();

        KeyValuePair kvpToUpdate = this.searchForValue(routeIndex);
        kvpToUpdate.key = newKey;

        int newMaxKey = this.maxKey();
        if (!this.isRoot() && oldMaxKey != newMaxKey) {
            this.parent.updateKeyFromChild(this.index, newMaxKey);
        }

        this.rewrite(); // TODO index is not known here
    }

    private void removeKey(int key) {
        int oldMaxKey = this.maxKey();
        KeyValuePair previous = null;
        int currentIndex = this.data.length - 1;
        boolean found = false;
        while (!found || currentIndex >= 0) {
            KeyValuePair current = this.data[currentIndex];
            int mid = current.key;
            found = mid == key;
            this.data[currentIndex] = previous;
            previous = current;
            currentIndex -= 1;
        }
        int deletedKey = previous.key;
        this.size -= 1;
        if (this.isEmpty()) {
            if (this.isRoot()) {
                this.isLeaf = true;
            } else {
                this.parent.removeKey(deletedKey);
                this.bTree.nodeStore.deallocate(this.index);
            }
        } else {
            if (oldMaxKey == deletedKey) {
                this.parent.updateKeyFromChild(this.index, this.maxKey());
            }
            this.rewrite();
        }
    }

    private void rewrite() {
        this.bTree.nodeStore.writeNode(this);
    }

    private static class KeyValuePair {
        private int key;
        private int value;

        public KeyValuePair(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}
