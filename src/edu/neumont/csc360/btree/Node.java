package edu.neumont.csc360.btree;

import edu.neumont.csc360.btree.utils.ByteUtils;

public class Node {
    private static final int NODE_METADATA_SIZE = 5;

    // Not persisted
    protected BTree bTree;
    protected Node parent;
    protected int index;

    // Persisted
    private boolean isLeaf;
    private int size;
    private KeyValuePair[] data;

    private Node() {}

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

        int numKvps = (bytes.length - NODE_METADATA_SIZE) / 8;
        KeyValuePair[] data = new KeyValuePair[numKvps];
        for (int i = 0; i < numKvps; i++) {
            int offset = NODE_METADATA_SIZE + i * 8;

            int key = ByteUtils.intFromBytesInByteArray(bytes, offset);
            int value = ByteUtils.intFromBytesInByteArray(bytes, offset + 4);

            data[i] = new KeyValuePair(key, value);
        }
        node.data = data;

        return node;
    }

    protected int[] toByteArray() {
        int[] bytes = new int[NODE_METADATA_SIZE + this.data.length * 8];

        ByteUtils.writeBytesFromIntToByteArray(this.size, bytes, 0);
        for (int i = 0; i < this.data.length; i++) {
            KeyValuePair kvp = this.data[i];
            int offset = NODE_METADATA_SIZE + i * 8;

            ByteUtils.writeBytesFromIntToByteArray(kvp.key, bytes, offset);
            ByteUtils.writeBytesFromIntToByteArray(kvp.value, bytes, offset + 4);
        }

        return bytes;
    }

    /**
     * Traverses down the tree until reaching a leaf node, then inserts the key-value-pair.
     */
    public void addKey(int key, int value) {
        if (this.isLeaf) {
            if (this.isFull()) {
                this.splitAndAdd(key, value);
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
            KeyValuePair foundKvp = this.searchForKvp(key);
            value = foundKvp.value; // technically could be null
        } else {
            Node child = this.childWithKey(key);
            value = child.getValue(key);
        }
        return value;
    }

    public void updateKeysValue(int key, int value) {
        if (this.isLeaf) {
            KeyValuePair toBeUpdated = this.searchForKvp(key);
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

    private KeyValuePair searchForKvp(int key) {
        KeyValuePair foundKvp = null;
        int loIndex = 0;
        int hiIndex = this.data.length - 1;
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
            }
        }
        return foundKvp;
    }

    private KeyValuePair searchForValue(int value) {
        KeyValuePair foundKvp = null;
        int loIndex = 0;
        int hiIndex = this.data.length - 1;
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
        int childIndex = -1;
        for (KeyValuePair kvp : this.data) {
            if (key < kvp.key) {
                childIndex = kvp.value;
            }
        }
        if (childIndex == -1) {
            childIndex = this.size - 1;
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

        KeyValuePair[] firstHalf = new KeyValuePair[firstHalfLength];
        System.arraycopy(this.data, 0, firstHalf, 0, firstHalfLength);

        KeyValuePair[] secondHalf = new KeyValuePair[secondHalfLength];
        System.arraycopy(this.data, firstHalfLength, firstHalf, 0, secondHalfLength);

        this.size = firstHalfLength;
        this.data = firstHalf;

        Node otherNode = new Node(this.isLeaf, secondHalfLength, secondHalf);

        this.bTree.nodeStore.addNode(otherNode);
        this.bTree.nodeStore.writeNode(this);

        if (key < this.maxKey()) {
            this.insertKey(key, value);
        } else {
            otherNode.insertKey(key, value);
        }

        int thisMax = this.maxKey();
        int otherMax = otherNode.maxKey();

        if (this.isRoot()) {
            KeyValuePair routeToThis = new KeyValuePair(thisMax, this.index);
            KeyValuePair routeToOther = new KeyValuePair(otherMax, otherNode.index);
            KeyValuePair[] routes = new KeyValuePair[] { routeToThis, routeToOther };

            Node newRoot = new Node(false, 2, routes);
            this.bTree.nodeStore.addRoot(newRoot);

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
        return this.data[this.data.length - 1].key;
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
            this.rewrite();
        }
    }

    private void updateKeyFromChild(int routeIndex, int newKey) {
        int oldMaxKey = this.maxKey();

        KeyValuePair kvpToUpdate = this.searchForValue(routeIndex);
        kvpToUpdate.key = newKey;

        int newMaxKey = this.maxKey();
        if (oldMaxKey != newMaxKey) {
            this.parent.updateKeyFromChild(this.index, newMaxKey);
        }

        this.rewrite();
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
